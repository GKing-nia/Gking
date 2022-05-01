package com.vtmer.microteachingquality.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.vtmer.microteachingquality.common.constant.enums.EvaluationProcessStatus;
import com.vtmer.microteachingquality.common.constant.enums.UserTypeConstant;
import com.vtmer.microteachingquality.mapper.*;
import com.vtmer.microteachingquality.model.dto.insert.SubmitEvaluationRecordBO;
import com.vtmer.microteachingquality.model.dto.result.*;
import com.vtmer.microteachingquality.model.entity.*;
import com.vtmer.microteachingquality.service.CourseEvaluationExpertService;
import com.vtmer.microteachingquality.util.UserUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Hung
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class CourseEvaluationExpertServiceImpl implements CourseEvaluationExpertService, EvaluationProcessStatus, UserTypeConstant {

    @Autowired
    private ClazzExpertManageInfoMapper clazzExpertManageInfoMapper;
    @Autowired
    private ClazzMapper clazzMapper;
    @Autowired
    private ClazzFileMapper clazzFileMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private ClazzEvaluateOptionMapper clazzEvaluateOptionMapper;
    @Autowired
    private ClazzEvaluateOptionRecordMapper recordMapper;
    @Autowired
    private ClazzOpinionRecordMapper clazzOpinionRecordMapper;
    @Autowired
    private EvaluationProcessMapper evaluationProcessMapper;

    /**
     * 评审课程报告信息
     *
     * @param userId
     * @return 返回评审的课程报告信息
     */
    @Override
    public FinishedClazzReviewVO selectCourseMessageofEvaluationExpert(Integer userId, Integer status, Integer pageNum, Integer pageSize) {
        Integer startIndex = (pageNum - 1) * pageSize;
        Integer length = pageSize;
        List<ClazzExpertManageInfo> recordList = clazzExpertManageInfoMapper.selectByUserIdAndStatus(userId, status, startIndex, length);

        FinishedClazzReviewVO result = new FinishedClazzReviewVO();
        List<ClazzToReviewResult> resultList = new ArrayList<>();
        //先获取了所有管理的信息，再根据课程id和状态去进行查询
        for (ClazzExpertManageInfo expertManageInfo : recordList) {

            Clazz clazz = clazzMapper.selectByPrimaryKey(expertManageInfo.getClazzId());
            if (clazz == null) {
                continue;
            }
            ClazzToReviewResult resultTemp = new ClazzToReviewResult();
            resultTemp.setClazzName(clazz.getName());
            resultTemp.setClazzCollege(clazz.getCollege());
            resultTemp.setClazzId(clazz.getId());
            List<User> userList = userMapper.selectByUserBelong(clazz.getName());
            if (userList.size() == 1) {
                //通过userBelong获取的虽然是list，但应该只有一个用户，所以直接get下标0
                User user = userList.get(0);
                resultTemp.setPrincipalName(user.getRealName());
            }
            resultList.add(resultTemp);

        }
        result.setClazzToReviewResultList(resultList);
        result.setTotalCounts(clazzExpertManageInfoMapper.countClazzToReview(userId, status));

        return result;
    }

    /**
     * 获取评审信息
     *
     * @param clazzId
     * @return 返回评审信息
     */
    @Override
    public List<ClazzInJudgeResultDTO> getClazzInJudgeByClazzType(Integer clazzId) {
        String type = clazzMapper.selectById(clazzId).getType();
        List<ClazzEvaluateOption> optionList = clazzEvaluateOptionMapper.selectByClazzType(type);
        return optionList.stream()
                .map(clazzEvaluateOption -> new ClazzInJudgeResultDTO(clazzEvaluateOption.getId(), clazzEvaluateOption.getFirstTarget(), clazzEvaluateOption.getDetail()))
                .collect(Collectors.toList());
    }

    @Override
    public ClazzFinishResult getSingleClazzFinishReview(Long evaluationId, Integer userId) {
        List<ClazzEvaluateOptionRecord> optionRecordList = recordMapper.selectByClazzIdAndUserId(evaluationId, userId);
        List<ClazzFinishQuestionResult> questionResultList = optionRecordList.stream().map(clazzEvaluateOptionRecord -> {
            ClazzEvaluateOption option = clazzEvaluateOptionMapper.selectByPrimaryKey(clazzEvaluateOptionRecord.getEvaluationOptionId());
            return new ClazzFinishQuestionResult(option.getId(), option.getFirstTarget(), option.getDetail(), clazzEvaluateOptionRecord.getMark());
        }).collect(Collectors.toList());
        ClazzOpinionRecord clazzOpinionRecord = clazzOpinionRecordMapper.selectByEvaluationIdAndUserId(evaluationId, userId);
        return new ClazzFinishResult(evaluationId, userId, questionResultList, clazzOpinionRecord.getClazzAdvantage(), clazzOpinionRecord.getClazzProblem(), clazzOpinionRecord.getClazzAdvice());
    }

    /**
     * @author 达
     * 课程评审专家提交评审记录
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Boolean insertEvaluationRecord(SubmitEvaluationRecordBO submitEvaluationRecordBO) {
        //将选择题 评审插入
        submitEvaluationRecordBO.getInsertEvaluationRecordDTOList()
                .forEach(insertEvaluationRecordDTO -> new ClazzEvaluateOptionRecord(submitEvaluationRecordBO.getOriginReviewExpertId(),
                        submitEvaluationRecordBO.getEvaluationId(), insertEvaluationRecordDTO.getOptionId(),
                        insertEvaluationRecordDTO.getMark()).insert());

        //将填空题 插入
        ClazzOpinionRecord opinionRecord = new ClazzOpinionRecord(submitEvaluationRecordBO.getOriginReviewExpertId(),
                submitEvaluationRecordBO.getAdvantage(), submitEvaluationRecordBO.getProblem(), submitEvaluationRecordBO.getAdvice(), submitEvaluationRecordBO.getEvaluationId());

        //更新评审流程状态
        User currentUser = UserUtil.getCurrentUser();
        List<String> userType = UserUtil.getUserType(currentUser);
        EvaluationProcess evaluationProcess = evaluationProcessMapper.selectById(submitEvaluationRecordBO.getEvaluationId());
        if (userType.contains(ENGINEERING_EXPERT) || userType.contains(HUMANITIES_AND_SOCIAL_SCIENCES_EXPERT) || userType.contains(ARTS_AND_SCIENCES_EXPERT)) {
            evaluationProcess.setCurrentPhases(EVALUATION_PROCESS_EXPERT_REVIEW);
        }
        if (userType.contains(ARTS_AND_SCIENCES_EXPERT_LEADER) || userType.contains(ENGINEERING_EXPERT_LEADER) || userType.contains(HUMANITIES_AND_SOCIAL_SCIENCES_EXPERT_LEADER)) {
            evaluationProcess.setCurrentPhases(EVALUATION_PROCESS_EXPERT_LEADER_REVIEW);
        }

        return opinionRecord.insert() && evaluationProcess.insertOrUpdate();
    }

    /**
     * 根据课程名字返回文件列表
     */
    @Override
    public List<GetClazzFilesResult> getAllEvaluationFiles(Long evaluationId) {
        QueryWrapper<ClazzFile> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("evaluation_id", evaluationId);
        List<ClazzFile> clazzFiles = clazzFileMapper.selectList(queryWrapper);
        return clazzFiles.stream()
                .map(clazzFile -> new GetClazzFilesResult(clazzFile.getId(), clazzFile.getUserId(), clazzFile.getClazzId(), clazzFile.getFileName(), clazzFile.getPath()))
                .collect(Collectors.toList());
    }


    @Override
    public Integer insertEmptyData(String tableName, Integer size) {
        int result = 0;
        for (int i = 0; i < size; i++) {
            result += clazzMapper.insertEmptyData(tableName);
        }
        return result;
    }
}
