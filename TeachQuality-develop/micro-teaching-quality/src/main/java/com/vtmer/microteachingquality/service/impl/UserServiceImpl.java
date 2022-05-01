package com.vtmer.microteachingquality.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.extra.mail.MailUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hung.microoauth2commons.commonutils.utils.RegexUtils;
import com.vtmer.microteachingquality.common.constant.enums.UserType;
import com.vtmer.microteachingquality.common.exception.user.UserLeaderExistException;
import com.vtmer.microteachingquality.common.exception.user.UserNameExistException;
import com.vtmer.microteachingquality.common.exception.user.UserNotExistException;
import com.vtmer.microteachingquality.mapper.RoleMapper;
import com.vtmer.microteachingquality.mapper.UserMapper;
import com.vtmer.microteachingquality.mapper.UserRoleMapper;
import com.vtmer.microteachingquality.model.dto.insert.UserDTO;
import com.vtmer.microteachingquality.model.dto.result.UserInfoResult;
import com.vtmer.microteachingquality.model.entity.Role;
import com.vtmer.microteachingquality.model.entity.User;
import com.vtmer.microteachingquality.model.entity.UserRole;
import com.vtmer.microteachingquality.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.hung.microoauth2commons.commonutils.utils.RedisConstants.*;
import static com.hung.microoauth2commons.commonutils.utils.RedisConstants.FORGET_CODE_TTL;

@Service
@Transactional(rollbackFor = Exception.class)
public class UserServiceImpl  extends ServiceImpl<UserMapper, User> implements UserService{

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private UserRoleMapper userRoleMapper;
    @Autowired
    private RoleMapper roleMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int saveUser(User user) {
        //因为存在用户是 开设年级专业不同但课程名字相同的情况，所以暂时放开对重复用户名的校验
        User existUser = userMapper.selectByUserName(user.getUserName());
        if (ObjectUtil.isNotNull(existUser)) {
            throw new UserNameExistException();
        }
        if (user.getUserType().equals(UserType.LEADER.getType())) {
            List<User> existUserList = userMapper.selectByUserTypeAndUserBelong(UserType.LEADER.getType(), user.getUserBelong());
            if (CollUtil.isNotEmpty(existUserList)) {
                throw new UserLeaderExistException();
            }
        }
        return userMapper.insertUser(user);
    }

    @Override
    public int updatePwd(User loginUser, String newPwd) {
        // 对新密码进行加密
        return userMapper.updatePwdByUserId(passwordEncoder.encode(newPwd), loginUser.getId());
    }

    @Override
    public List<UserInfoResult> listUserInfo() {
        List<User> userList = userMapper.selectAll();
        return listUserInfoResult(userList);
    }

    @Override
    public List<UserInfoResult> listUserInfoByUserBelong(String userBelong) {
        List<User> userList = userMapper.selectByUserBelong(userBelong);
        return listUserInfoResult(userList);
    }

    @Override
    public List<UserInfoResult> listUserInfoByUserTypeAndUserBelong(String userType, String userBelong) {
        List<User> userList = userMapper.selectByUserTypeAndUserBelong(userType, userBelong);
        return ObjectUtil.isNotNull(userList) ? listUserInfoResult(userList) : new ArrayList<>();
    }

    @Override
    public int cancelAccount(Integer userId) {
        return userMapper.deleteByPrimaryKey(userId);
    }

    @Override
    public int updatePwdByUserId(Integer userId) {
        User user = userMapper.selectByPrimaryKey(userId);
        if (ObjectUtil.isNull(user)) {
            throw new UserNotExistException();
        }
        String newPassword = passwordEncoder.encode(user.getUserName());
        return userMapper.updatePwdByUserId(newPassword, userId);
    }

    /**
     * 转换用户信息输出结果
     *
     * @param userList
     * @return
     */
    private List<UserInfoResult> listUserInfoResult(List<User> userList) {
        List<UserInfoResult> userInfoResultList = new ArrayList<>();
        if (ObjectUtil.isNotNull(userList)) {
            for (User user : userList) {
                UserInfoResult userInfoResult = new UserInfoResult();
                BeanUtils.copyProperties(user, userInfoResult);
                userInfoResultList.add(userInfoResult);
            }
        }
        return userInfoResultList;
    }

/*    @Override
    public Integer updatePwdByUserNameAndRealNameAndUserBelong(String userName, String realName, String userBelong) {
        String newPwd = null;
        User user = userMapper.selectByUserName(userName);
        logger.info("帐号 {} 尝试修改密码", userName);
        if (ObjectUtil.isNull(user)) {
            logger.info("帐号 {} 尝试修改密码失败", userName);
            throw new UserNotExistException();
        }
        if (!user.getRealName().equals(realName) || !user.getUserBelong().equals(userBelong)) {
            throw new UserNotMatchException();
        }
        newPwd = passwordEncoder.encode(user.getUserName());
        int result = userMapper.updatePwdByUserNameAndRealNameAndUserBelong(userName, realName, userBelong, newPwd);
        if (result <= 0) {
            logger.info("用户 {} 修改密码失败", user.getRealName());
        } else {
            logger.info("用户 {} 修改密码成功", user.getRealName());
        }
        return result;
    }*/

    @Override
    public Boolean sendCode(String email) {
        if (RegexUtils.isEmailInvalid(email)){
            return false;
        }
        String code= RandomUtil.randomNumbers(6);
        stringRedisTemplate.opsForValue().set(USER_CODE_KEY+email,code,USER_CODE_TTL, TimeUnit.MINUTES);
        String stringBuilder = "<html><head><title></title></head><body>" +
                "您好<br/>" +
                "您的验证码是：" + code + "<br/>" +
                "您可以复制此验证码并返回至科研管理系统找回密码页面，以验证您的邮箱。<br/>" +
                "此验证码只能使用一次，在" +
                USER_CODE_TTL +
                "分钟内有效。验证成功则自动失效。<br/>" +
                "如果您没有进行上述操作，请忽略此邮件。";
        try {
            MailUtil.send(email,"邮箱验证-本科质量管理系统", stringBuilder,true);
        }catch (Exception e){
            e.printStackTrace();
        }
        return true;
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public int register(UserDTO userDTO) {

        //因为存在用户是 开设年级专业不同但课程名字相同的情况，所以暂时放开对重复用户名的校验
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_name", userDTO.getUserName());
        User existUser = userMapper.selectOne(queryWrapper);
        if (ObjectUtil.isNotNull(existUser)) {
            throw new UserNameExistException();
        }
        if (userDTO.getUserType().equals(UserType.LEADER.getType())) {
            QueryWrapper<User> userDTOQueryWrapper = new QueryWrapper<>();
            QueryWrapper<User> eq = userDTOQueryWrapper.eq("user_type", UserType.LEADER.getType())
                    .eq("user_belong", userDTO.getUserBelong());
            List<User> existUserList = userMapper.selectList(eq);
            if (CollUtil.isNotEmpty(existUserList)) {
                throw new UserLeaderExistException();
            }
        }
        User user=new User();
        BeanUtils.copyProperties(userDTO,user);
        BCryptPasswordEncoder bCryptPasswordEncoder=new BCryptPasswordEncoder();
        user.setUserPwd(bCryptPasswordEncoder.encode(userDTO.getUserPwd()));
        //明文加密
        //String passwordSecret = DigestUtils.md5DigestAsHex(user.getUserPwd().getBytes(StandardCharsets.UTF_8));
        //user.setUserPwd(passwordSecret);
        return userMapper.insert(user);
    }

    @Override
    public Boolean putRole(Integer userId, String role){
        QueryWrapper<Role> roleQueryWrapper = new QueryWrapper<>();
        QueryWrapper<Role> roleOne = roleQueryWrapper.eq("role_name", role);
        Role role1 = roleMapper.selectOne(roleOne);
        UserRole userRole = new UserRole();
        userRole.setUserId(userId);
        userRole.setRoleId(role1.getId());
        int insert = userRoleMapper.insert(userRole);
        return insert > 0;
    }

    @Override
    public Boolean sendCodeWithForgetting(String email) {
        if (RegexUtils.isEmailInvalid(email)){
            return false;
        }
        String code= RandomUtil.randomNumbers(6);
        stringRedisTemplate.opsForValue().set(FORGET_CODE_KEY+email,code,FORGET_CODE_TTL, TimeUnit.MINUTES);
        String stringBuilder = "<html><head><title></title></head><body>" +
                "您好<br/>" +
                "您的验证码是：" + code + "<br/>" +
                "您可以复制此验证码并返回至科研管理系统找回密码页面，以验证您的邮箱。<br/>" +
                "此验证码只能使用一次，在" +
                FORGET_CODE_TTL +
                "分钟内有效。验证成功则自动失效。<br/>" +
                "如果您没有进行上述操作，请忽略此邮件。";
        try {
            MailUtil.send(email,"邮箱验证-本科质量管理系统", stringBuilder,true);
        }catch (Exception e){
            e.printStackTrace();
        }
        return true;
    }
}
