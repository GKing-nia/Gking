package com.vtmer.microteachingquality.service.impl;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hung.microoauth2commons.commonutils.api.Result;
import com.vtmer.microteachingquality.mapper.*;
import com.vtmer.microteachingquality.model.dto.result.ScrollResult;
import com.vtmer.microteachingquality.model.entity.*;
import com.vtmer.microteachingquality.model.dto.result.NotificationVO;
import com.vtmer.microteachingquality.service.NotifyService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static com.hung.microoauth2commons.commonutils.utils.RedisConstants.*;


/**
 * @author : Gking
 * @date : 2022-04-24 16:30
 **/
public class NotifyServiceImpl extends ServiceImpl<NotifyMapper, Notify> implements NotifyService {
    @Autowired
    private NotifyMapper notifyMapper;
    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private ClazzMapper clazzMapper;
    @Autowired
    private ClazzExpertManageInfoMapper clazzExpertManageInfoMapper;
    @Autowired
    private MajorMapper majorMapper;
    @Autowired
    private ManageInfoMapper manageInfoMapper;
    @Autowired
    private LeaderInfoMapper leaderInfoMapper;
    /**
     * 课程负责人上传文件后通知评审专家评审
     * @param userId
     * @return
     */
    @Override
    public Boolean sendNotificationByClazzPrincipal(Integer userId){
        //获取课程负责人信息
        User user = userMapper.selectById(userId);
        String userBelong = user.getUserBelong();
        //获取负责人对应的课程
        Clazz clazz = clazzMapper.selectByName(userBelong);
        //获取对应的课程评审专家
        QueryWrapper<ClazzExpertManageInfo> clazzExpertManageInfoQueryWrapper = new QueryWrapper<>();
        QueryWrapper<ClazzExpertManageInfo> clazz_id = clazzExpertManageInfoQueryWrapper.eq("clazz_id", clazz.getId());
        List<ClazzExpertManageInfo> clazzExpertManageInfos = clazzExpertManageInfoMapper.selectList(clazz_id);
        for (ClazzExpertManageInfo clazzExpertManageInfo:clazzExpertManageInfos) {
            //保存通知实体类
            User user1 = userMapper.selectById(clazzExpertManageInfo.getUserId());
            Notify notify = new Notify();
            notify.setSenderId(userId);
            notify.setSenderName(user.getRealName());
            notify.setRecipientId(user1.getId());
            notify.setTargetBelong(user1.getUserBelong());
            notify.setTargetName(user1.getRealName());
            String stringBuilder = "<html><head><title></title></head><body>" +
                    "您好！" +
                    "您的课程负责人已上传评审文件，请及时评审！！！<br/>" ;
            notify.setMessage(stringBuilder);
            notify.setStatus(false);
            int insert = notifyMapper.insert(notify);
            if (insert<=0){
                return false;
            }
            //获取评审专家id
            Integer id = user1.getId();
            //推送消息
            String key=FEED_CLAZZ_EXPERT_KEY+id;
            stringRedisTemplate.opsForZSet().add(key, notify.getId().toString(), System.currentTimeMillis());
        }
        return true;
    }

    /**
     * 当课程负责人上传文件成功，课程评审专家获取通知
     * @param max
     * @param offset
     * @return
     */
    @Override
    public Result<String> queryNotificationByClazzExpert(Long max, Integer offset){
        // 1.获取当前用户
        User loginUser = JSON.parseObject(JSONUtil.toJsonStr(SecurityContextHolder.getContext().getAuthentication().getPrincipal()), User.class);
        Integer userId = loginUser.getId();
        // 2.查询收件箱 ZREVRANGEBYSCORE key Max Min LIMIT offset count
        String key = FEED_CLAZZ_EXPERT_KEY+ userId;
        Set<ZSetOperations.TypedTuple<String>> typedTuples = stringRedisTemplate.opsForZSet()
                .reverseRangeByScoreWithScores(key, 0, max, offset, 2);
        // 3.非空判断
        if (typedTuples == null || typedTuples.isEmpty()) {
            return Result.success("查询成功");
        }
        // 4.解析数据：blogId、minTime（时间戳）、offset
        List<Long> ids = new ArrayList<>(typedTuples.size());
        long minTime = 0; // 2
        int os = 1; // 2
        for (ZSetOperations.TypedTuple<String> tuple : typedTuples) { // 5 4 4 2 2
            // 4.1.获取id
            ids.add(Long.valueOf(tuple.getValue()));
            // 4.2.获取分数(时间戳）
            long time = tuple.getScore().longValue();
            if(time == minTime){
                os++;
            }else{
                minTime = time;
                os = 1;
            }
        }

        // 5.根据id查询blog
        String idStr = StrUtil.join(",", ids);
        List<Notify> notifications = query().in("id", ids).last("ORDER BY FIELD(id," + idStr + ")").list();
        List<NotificationVO> notificationVOList=new ArrayList<>();
        for (Notify notify:notifications){
            NotificationVO notificationVO=new NotificationVO();
            BeanUtils.copyProperties(notify,notificationVO);
            notificationVOList.add(notificationVO);
        }
        for (Notify notify : notifications ) {
            // 5. 阅读时间和阅读状态的添加
            notify.setStatus(true);
            notify.setReadAt(new DateTime());

        }

        // 6.封装并返回
        ScrollResult r = new ScrollResult();
        r.setList(notificationVOList);
        r.setOffset(os);
        r.setMinTime(minTime);

        return Result.success("");
    }

    /**
     * 课程评审专家组长退回评审发送通知给课程负责人
     * @param userId
     * @param leaderId
     * @return
     */
    @Override
    public Boolean sendNotificationByClazzExpertLeader(Integer userId, Integer leaderId) {

        //获取课程负责人
        User user = userMapper.selectById(userId);
        //获取评审组长的名字
        User LeaderUser = userMapper.selectById(leaderId);
        String realName1 = LeaderUser.getRealName();
            //保存通知实体类
            Notify notify = new Notify();
            notify.setSenderId(leaderId);
            notify.setSenderName(realName1);
            notify.setRecipientId(user.getId());
            notify.setTargetBelong(user.getUserBelong());
            notify.setTargetName(user.getRealName());
            String stringBuilder = "<html><head><title></title></head><body>" +
                    "您好！" +
                    "您的评审已被退回，请重新处理！！！<br/>" ;
            notify.setMessage(stringBuilder);
            notify.setStatus(false);
            int insert = notifyMapper.insert(notify);
            if (insert<=0){
                return false;
            }
            //推送消息
            String key=FEED_CLAZZ_LEADER_KEY+userId;
            stringRedisTemplate.opsForZSet().add(key, notify.getId().toString(), System.currentTimeMillis());
        return true;
    }

    /**
     * 课程负责人获取通知
     * @param max
     * @param offset
     * @return
     */
    @Override
    public Result<String> queryNotificationByClazzLeader(Long max, Integer offset) {
        // 1.获取当前用户
        User loginUser = JSON.parseObject(JSONUtil.toJsonStr(SecurityContextHolder.getContext().getAuthentication().getPrincipal()), User.class);
        Integer userId = loginUser.getId();
        // 2.查询收件箱 ZREVRANGEBYSCORE key Max Min LIMIT offset count
        String key = FEED_CLAZZ_LEADER_KEY + userId;
        Set<ZSetOperations.TypedTuple<String>> typedTuples = stringRedisTemplate.opsForZSet()
                .reverseRangeByScoreWithScores(key, 0, max, offset, 2);
        // 3.非空判断
        if (typedTuples == null || typedTuples.isEmpty()) {
            return Result.success("查询成功");
        }
        // 4.解析数据：notifyId、minTime（时间戳）、offset
        List<Long> ids = new ArrayList<>(typedTuples.size());
        long minTime = 0; // 2
        int os = 1; // 2
        for (ZSetOperations.TypedTuple<String> tuple : typedTuples) { // 5 4 4 2 2
            // 4.1.获取id
            ids.add(Long.valueOf(tuple.getValue()));
            // 4.2.获取分数(时间戳）
            long time = tuple.getScore().longValue();
            if(time == minTime){
                os++;
            }else{
                minTime = time;
                os = 1;
            }
        }

        // 5.根据id查询notify
        String idStr = StrUtil.join(",", ids);
        List<Notify> notifications = query().in("id", ids).last("ORDER BY FIELD(id," + idStr + ")").list();
        List<NotificationVO> notificationVOList=new ArrayList<>();
        for (Notify notify:notifications){
            NotificationVO notificationVO=new NotificationVO();
            BeanUtils.copyProperties(notify,notificationVO);
            notificationVOList.add(notificationVO);
        }
        for (Notify notify : notifications ) {
            // 5. 阅读时间和阅读状态的添加
            notify.setStatus(true);
            notify.setReadAt(new DateTime());

        }

        // 6.封装并返回
        ScrollResult r = new ScrollResult();
        r.setList(notificationVOList);
        r.setOffset(os);
        r.setMinTime(minTime);

        return Result.success("");
    }

    /**
     * 专业负责人上传文件后，发送通知给评审专家
     * @param id
     * @return
     */
    @Override
    public Boolean sendNotificationByMajorLeader(Integer id) {
        //获取专业负责人信息
        User principal = userMapper.selectById(id);

        //获取专业负责人对应的专业
        Major major = majorMapper.selectByMajorName(principal.getUserBelong());
        //获取评审专家信息,并逐个发送通知
        QueryWrapper<ManageInfo> manageInfoQueryWrapper = new QueryWrapper<>();
        QueryWrapper<ManageInfo> major_id = manageInfoQueryWrapper.eq("major_id", major.getId());
        List<ManageInfo> manageInfos = manageInfoMapper.selectList(major_id);
        for (ManageInfo manageInfo : manageInfos) {
            User user = userMapper.selectById(manageInfo.getUserId());
            //创建通知实体类
            Notify notify=new Notify();
            notify.setSenderId(principal.getId());
            notify.setSenderName(principal.getRealName());
            notify.setRecipientId(user.getId());
            notify.setTargetBelong(user.getUserBelong());
            notify.setTargetName(user.getRealName());
            String stringBuilder = "<html><head><title></title></head><body>" +
                    "您好！" +
                    "您的专业负责人已上传评审文件，请及时评审！！！<br/>" ;
            notify.setMessage(stringBuilder);
            notify.setStatus(false);
            int insert = notifyMapper.insert(notify);
            if (insert<=0){
                return false;
            }
            Integer id1 = user.getId();
            //推送消息
            String key=FEED_CLAZZ_EXPERT_KEY+id1;
            stringRedisTemplate.opsForZSet().add(key, notify.getId().toString(), System.currentTimeMillis());
        }
        return true;
    }

    @Override
    public Result<String> queryNotificationByMajorExpert(Long max, Integer offset) {
        // 1.获取当前用户
        User loginUser = JSON.parseObject(JSONUtil.toJsonStr(SecurityContextHolder.getContext().getAuthentication().getPrincipal()), User.class);
        Integer userId = loginUser.getId();
        // 2.查询收件箱 ZREVRANGEBYSCORE key Max Min LIMIT offset count
        String key = FEED_MAJOR_EXPERT_KEY+ userId;
        Set<ZSetOperations.TypedTuple<String>> typedTuples = stringRedisTemplate.opsForZSet()
                .reverseRangeByScoreWithScores(key, 0, max, offset, 2);
        // 3.非空判断
        if (typedTuples == null || typedTuples.isEmpty()) {
            return Result.success("查询成功");
        }
        // 4.解析数据：notifyId、minTime（时间戳）、offset
        List<Long> ids = new ArrayList<>(typedTuples.size());
        long minTime = 0; // 2
        int os = 1; // 2
        for (ZSetOperations.TypedTuple<String> tuple : typedTuples) { // 5 4 4 2 2
            // 4.1.获取id
            ids.add(Long.valueOf(tuple.getValue()));
            // 4.2.获取分数(时间戳）
            long time = tuple.getScore().longValue();
            if(time == minTime){
                os++;
            }else{
                minTime = time;
                os = 1;
            }
        }

        // 5.根据id查询notify
        String idStr = StrUtil.join(",", ids);
        List<Notify> notifications = query().in("id", ids).last("ORDER BY FIELD(id," + idStr + ")").list();
        List<NotificationVO> notificationVOList=new ArrayList<>();
        for (Notify notify:notifications){
            NotificationVO notificationVO=new NotificationVO();
            BeanUtils.copyProperties(notify,notificationVO);
            notificationVOList.add(notificationVO);
        }
        for (Notify notify : notifications ) {
            // 5. 阅读时间和阅读状态的添加
            notify.setStatus(true);
            notify.setReadAt(new DateTime());

        }

        // 6.封装并返回
        ScrollResult r = new ScrollResult();
        r.setList(notificationVOList);
        r.setOffset(os);
        r.setMinTime(minTime);

        return Result.success("");


    }

    @Override
    public Boolean sendNotificationByMajorExpert(Integer id) {
        //获取评审专家组长信息
        QueryWrapper<ManageInfo> manageInfoQueryWrapper = new QueryWrapper<>();
        QueryWrapper<ManageInfo> user_id = manageInfoQueryWrapper.eq("user_id", id);
        ManageInfo manageInfo = manageInfoMapper.selectOne(user_id);
        User user1 = userMapper.selectById(id);
        //获取对应的专业
        Major major = majorMapper.selectById(manageInfo.getMajorId());
        //获取对应专业负责人
        QueryWrapper<User> majorQueryWrapper = new QueryWrapper<>();
        QueryWrapper<User> LeaderUser = majorQueryWrapper.eq("user_belong", major.getName());
        User user = userMapper.selectOne(LeaderUser);
        //发送通知
        Notify notify = new Notify();
        notify.setSenderId(user1.getId());
        notify.setSenderName(user1.getRealName());
        notify.setRecipientId(user.getId());
        notify.setTargetBelong(user.getUserBelong());
        notify.setTargetName(user.getRealName());
        String stringBuilder = "<html><head><title></title></head><body>" +
                "您好！" +
                "您的评审已被退回，请重新处理！！！<br/>" ;
        notify.setMessage(stringBuilder);
        notify.setStatus(false);
        int insert = notifyMapper.insert(notify);
        if (insert<=0){
            return false;
        }
        Integer id1 = user.getId();
        //推送消息
        String key=FEED_MAJOR_LEADER_KEY+id1;
        stringRedisTemplate.opsForZSet().add(key, notify.getId().toString(), System.currentTimeMillis());
        return true;
    }

    @Override
    public Result<String> queryNotificationByMajorLeader(Long max, Integer offset) {
        // 1.获取当前用户
        User loginUser = JSON.parseObject(JSONUtil.toJsonStr(SecurityContextHolder.getContext().getAuthentication().getPrincipal()), User.class);
        Integer userId = loginUser.getId();
        // 2.查询收件箱 ZREVRANGEBYSCORE key Max Min LIMIT offset count
        String key = FEED_MAJOR_LEADER_KEY + userId;
        Set<ZSetOperations.TypedTuple<String>> typedTuples = stringRedisTemplate.opsForZSet()
                .reverseRangeByScoreWithScores(key, 0, max, offset, 2);
        // 3.非空判断
        if (typedTuples == null || typedTuples.isEmpty()) {
            return Result.success("查询成功");
        }
        // 4.解析数据：notifyId、minTime（时间戳）、offset
        List<Integer> ids = new ArrayList<>(typedTuples.size());
        long minTime = 0; // 2
        int os = 1; // 2
        for (ZSetOperations.TypedTuple<String> tuple : typedTuples) { // 5 4 4 2 2
            // 4.1.获取id
            ids.add(Integer.valueOf(tuple.getValue()));
            // 4.2.获取分数(时间戳）
            long time = tuple.getScore().longValue();
            if(time == minTime){
                os++;
            }else{
                minTime = time;
                os = 1;
            }
        }

        // 5.根据id查询notify
        String idStr = StrUtil.join(",", ids);
        List<Notify> notifications = query().in("id", ids).last("ORDER BY FIELD(id," + idStr + ")").list();
        List<NotificationVO> notificationVOList=new ArrayList<>();
        for (Notify notify:notifications){
            NotificationVO notificationVO=new NotificationVO();
            BeanUtils.copyProperties(notify,notificationVO);
            notificationVOList.add(notificationVO);
        }
        for (Notify notify : notifications ) {
            // 5. 阅读时间和阅读状态的添加
            notify.setStatus(true);
            notify.setReadAt(new DateTime());

        }

        // 6.封装并返回
        ScrollResult r = new ScrollResult();
        r.setList(notificationVOList);
        r.setOffset(os);
        r.setMinTime(minTime);

        return Result.success("");
    }


}
