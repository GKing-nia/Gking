package com.vtmer.microteachingquality.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.vtmer.microteachingquality.common.constant.enums.EvaluationProcessStatus;
import com.vtmer.microteachingquality.common.exception.CommonRuntimeException;
import com.vtmer.microteachingquality.mapper.*;
import com.vtmer.microteachingquality.model.dto.insert.ClazzEvaluationLeaderBO;
import com.vtmer.microteachingquality.model.dto.result.ClazzEvaluationLeaderReviewVO;
import com.vtmer.microteachingquality.model.dto.result.FinishedReviewVO;
import com.vtmer.microteachingquality.model.dto.result.GetUploadedFilesResult;
import com.vtmer.microteachingquality.model.dto.select.ClazzOpinionRecordDTO;
import com.vtmer.microteachingquality.model.entity.*;
import com.vtmer.microteachingquality.service.EvaluationProcessService;
import com.vtmer.microteachingquality.service.NotifyService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Hung
 * @date 2022/4/20 20:02
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class EvaluationProcessServiceImpl extends ServiceImpl<EvaluationProcessMapper, EvaluationProcess> implements EvaluationProcessService, EvaluationProcessStatus {

    @Autowired
    private EvaluationProcessMapper evaluationProcessMapper;
    @Autowired
    private  ClazzMapper clazzMapper;
    @Autowired
    private ClazzFileMapper clazzFileMapper;
    @Autowired
    private ClazzOpinionRecordMapper clazzOpinionRecordMapper;
    @Autowired
    private ClazzOpinionLeaderRecordMapper clazzOpinionLeaderRecordMapper;
    @Autowired
    private UserMapper userMapper;
    @Resource
    private ClazzLeaderInfoMapper clazzLeaderInfoMapper;
    @Autowired
    private ClazzExpertManageInfoMapper clazzExpertManageInfoMapper;
    @Resource
    private NotifyService notifyService;

    @Override
    public List<EvaluationProcess> getEvaluationProcesses(Integer userId, Integer clazzId) {
        QueryWrapper<EvaluationProcess> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("creator_id", userId);
        queryWrapper.eq("clazz_id", clazzId);
        return evaluationProcessMapper.selectList(queryWrapper);
    }

    @Override
    public List<GetUploadedFilesResult> getUploadedFiles(Integer userId, Long evaluationId) {
        QueryWrapper<ClazzFile> clazzFileQueryWrapper = new QueryWrapper<>();
        clazzFileQueryWrapper.eq("user_id", userId);
        clazzFileQueryWrapper.eq("evaluation_id", evaluationId);
        List<ClazzFile> fileList = clazzFileMapper.selectList(clazzFileQueryWrapper);
        return fileList.stream()
                .map(clazzFile -> new GetUploadedFilesResult(clazzFile.getId(), clazzFile.getFileName(), clazzFile.getPath(), clazzFile.getUpdateTime()))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public Boolean sendBackEvaluation(Long evaluationId) {
        EvaluationProcess evaluationProcess = evaluationProcessMapper.selectById(evaluationId);
        evaluationProcess.setCurrentPhases(EVALUATION_PROCESS_MATERIAL_BACK);
        if (!evaluationProcess.insertOrUpdate()) {
            throw new CommonRuntimeException("退回数据失败");
        }
        //发送通知
        //获取课程
        Clazz clazz = clazzMapper.selectById(evaluationProcess.getClazzId());
        //获取课程负责人id
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        QueryWrapper<User> user_belong = userQueryWrapper.eq("user_belong", clazz.getName());
        List<User> users = userMapper.selectByUserBelong(clazz.getName());
        //获取评审组长的id
        //通过课程id获取一个评审专家的id
        QueryWrapper<ClazzExpertManageInfo> clazzExpertManageInfoQueryWrapper = new QueryWrapper<>();
        QueryWrapper<ClazzExpertManageInfo> clazz_id = clazzExpertManageInfoQueryWrapper.eq("clazz_id", clazz.getId());
        ClazzExpertManageInfo clazzExpertManageInfo = clazzExpertManageInfoMapper.selectOne(clazz_id);
        //通过评审专家id获取评审组长的id
        QueryWrapper<ClazzLeaderInfo> clazzLeaderInfoQueryWrapper = new QueryWrapper<>();
        QueryWrapper<ClazzLeaderInfo> member_id = clazzLeaderInfoQueryWrapper.eq("member_id", clazzExpertManageInfo.getUserId());
        ClazzLeaderInfo clazzLeaderInfo = clazzLeaderInfoMapper.selectOne(member_id);
        for (User user: users) {
            notifyService.sendNotificationByClazzExpertLeader(user.getId(),clazzLeaderInfo.getLeaderId());
        }
        return true;
    }

    @Override
    public List<FinishedReviewVO> getAllFinishedReviews(Long evaluation) {

        List<ClazzOpinionRecordDTO> reviewInfo = clazzOpinionRecordMapper.getAllReviewInfo(evaluation);
        return reviewInfo.stream().map(clazzOpinionRecordDTO -> {
            User user = new User().selectById(clazzOpinionRecordDTO.getUserId());
            return new FinishedReviewVO(user.getId(), user.getRealName(), clazzOpinionRecordDTO.getUpdateTime());
        }).collect(Collectors.toList());
    }

    @Override
    public Boolean postClazzEvaluationLeaderReview(ClazzEvaluationLeaderBO evaluationLeaderBO) {
        ClazzOpinionLeaderRecord leaderRecord = new ClazzOpinionLeaderRecord();
        BeanUtils.copyProperties(evaluationLeaderBO, leaderRecord);
        EvaluationProcess evaluationProcess = evaluationProcessMapper.selectById(evaluationLeaderBO.getEvaluationId());
        evaluationProcess.setCurrentPhases(EVALUATION_PROCESS_EXPERT_LEADER_OPINION);
        return leaderRecord.insert() && evaluationProcess.insertOrUpdate();
    }

    @Override
    public List<ClazzEvaluationLeaderReviewVO> getClazzEvaluationLeaderReviews(Long evaluationId) {
        QueryWrapper<ClazzOpinionLeaderRecord> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("evaluation_id", evaluationId);
        List<ClazzOpinionLeaderRecord> records = clazzOpinionLeaderRecordMapper.selectList(queryWrapper);
        return records.stream().map(clazzOpinionLeaderRecord -> {
            User user = userMapper.selectByPrimaryKey(clazzOpinionLeaderRecord.getUserId());
            return new ClazzEvaluationLeaderReviewVO(user.getId(), user.getRealName(), clazzOpinionLeaderRecord.getEvaluationOpinion(), clazzOpinionLeaderRecord.getUpdateTime());
        }).collect(Collectors.toList());
    }
}
