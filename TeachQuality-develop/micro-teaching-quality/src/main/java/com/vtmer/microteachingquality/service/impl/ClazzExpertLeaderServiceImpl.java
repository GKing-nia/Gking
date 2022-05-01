package com.vtmer.microteachingquality.service.impl;

import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.vtmer.microteachingquality.common.constant.enums.ClazzStatusEnums;
import com.vtmer.microteachingquality.mapper.ClazzExpertManageInfoMapper;
import com.vtmer.microteachingquality.mapper.ClazzLeaderInfoMapper;
import com.vtmer.microteachingquality.mapper.ClazzMapper;
import com.vtmer.microteachingquality.mapper.UserMapper;
import com.vtmer.microteachingquality.model.dto.result.ClazzLeaderGetMembersEvaluationInfoMemberClazzResult;
import com.vtmer.microteachingquality.model.dto.result.ClazzLeaderGetMembersEvaluationInfoResult;
import com.vtmer.microteachingquality.model.dto.result.ClazzLeaderGetNotEvaluateClazzInfoResult;
import com.vtmer.microteachingquality.model.dto.result.NotEvaluateClazzInfoResult;
import com.vtmer.microteachingquality.model.entity.Clazz;
import com.vtmer.microteachingquality.model.entity.ClazzExpertManageInfo;
import com.vtmer.microteachingquality.model.entity.ClazzLeaderInfo;
import com.vtmer.microteachingquality.model.entity.User;
import com.vtmer.microteachingquality.service.ClazzExpertLeaderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(rollbackFor = Exception.class)
public class ClazzExpertLeaderServiceImpl implements ClazzExpertLeaderService {

    @Autowired
    private ClazzMapper clazzMapper;
    @Autowired
    private ClazzExpertManageInfoMapper clazzExpertManageInfoMapper;
    @Autowired
    private ClazzLeaderInfoMapper clazzLeaderInfoMapper;
    @Autowired
    private UserMapper userMapper;

    /**
     * 课程组长获取待评价的课程信息
     */
    @Override
    public ClazzLeaderGetNotEvaluateClazzInfoResult getNotEvaluateClazzInfo(Integer startPage, Integer pageSize) {
        Integer userId = JSON.parseObject(JSONUtil.toJsonStr(SecurityContextHolder.getContext().getAuthentication().getPrincipal()), User.class).getId();
        Integer startIndex = (startPage - 1) * pageSize;
        List<ClazzExpertManageInfo> infoList = clazzExpertManageInfoMapper.selectByUserIdAndStatus(userId, ClazzStatusEnums.NOT_EVALUATE.getCode(), startIndex, pageSize);
        if (infoList == null) {
            return null;
        }
        ClazzLeaderGetNotEvaluateClazzInfoResult result = new ClazzLeaderGetNotEvaluateClazzInfoResult();
        List<NotEvaluateClazzInfoResult> infoResultList = new ArrayList<>();
        for (ClazzExpertManageInfo infoTemp : infoList) {
            NotEvaluateClazzInfoResult resultTemp = new NotEvaluateClazzInfoResult();
            Clazz clazz = clazzMapper.selectByPrimaryKey(infoTemp.getClazzId());
            resultTemp.setClazzName(clazz.getName());
            resultTemp.setClazzId(clazz.getId());
            infoResultList.add(resultTemp);
        }
        result.setClazzInfo(infoResultList);
        result.setTotalSize(clazzExpertManageInfoMapper.selectCountsByUserIdAndStatus(userId, ClazzStatusEnums.NOT_EVALUATE.getCode()));
        return result;
    }

    @Override
    public List<ClazzLeaderGetMembersEvaluationInfoResult> getMembersEvaluationInfo() {
        Integer leaderId = JSON.parseObject(JSONUtil.toJsonStr(SecurityContextHolder.getContext().getAuthentication().getPrincipal()), User.class).getId();
        List<ClazzLeaderInfo> clazzLeaderInfoList = clazzLeaderInfoMapper.selectByLeaderId(leaderId);
        if (clazzLeaderInfoList == null || clazzLeaderInfoList.isEmpty()) {
            return null;
        }
        List<ClazzLeaderGetMembersEvaluationInfoResult> resultList = new ArrayList<>();
        for (ClazzLeaderInfo clazzLeaderInfoTemp : clazzLeaderInfoList) {
            ClazzLeaderGetMembersEvaluationInfoResult result = new ClazzLeaderGetMembersEvaluationInfoResult();
            result.setMemberId(clazzLeaderInfoTemp.getMemberId());
            User user = userMapper.selectByPrimaryKey(clazzLeaderInfoTemp.getMemberId());
            result.setMemberName(user.getRealName());
            //获取每个下属管理的课程
            List<ClazzLeaderGetMembersEvaluationInfoMemberClazzResult> memberClazzResultList = new ArrayList<>();
            List<ClazzExpertManageInfo> clazzExpertManageInfoList = clazzExpertManageInfoMapper.selectByUserId(clazzLeaderInfoTemp.getMemberId());
            for (ClazzExpertManageInfo clazzExpertManageInfoTemp : clazzExpertManageInfoList) {
                ClazzLeaderGetMembersEvaluationInfoMemberClazzResult memberClazzResult = new ClazzLeaderGetMembersEvaluationInfoMemberClazzResult();
                memberClazzResult.setClazzId(clazzExpertManageInfoTemp.getClazzId());
                memberClazzResult.setStatus(clazzExpertManageInfoTemp.getStatus());
                Clazz clazz = clazzMapper.selectByPrimaryKey(clazzExpertManageInfoTemp.getClazzId());
                memberClazzResult.setClazzName(clazz.getName());
                memberClazzResult.setClazzCollege(clazz.getCollege());

                memberClazzResultList.add(memberClazzResult);
            }
            result.setMemberClazz(memberClazzResultList);

            resultList.add(result);
        }
        return resultList;
    }
}
