package com.vtmer.microteachingquality.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.vtmer.microteachingquality.model.entity.ClazzExpertManageInfo;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ClazzExpertManageInfoMapper  extends BaseMapper<ClazzExpertManageInfo> {
    int deleteByPrimaryKey(Integer id);

    int insert(ClazzExpertManageInfo record);

    int insertSelective(ClazzExpertManageInfo record);

    ClazzExpertManageInfo selectByPrimaryKey(Integer id);

    int countClazzToReview(Integer userId, Integer status);

    List<ClazzExpertManageInfo> selectByUserIdAndStatus(Integer userId, Integer status, Integer startIndex, Integer length);

    int selectCountsByUserIdAndStatus(Integer userId, Integer status);

    int updateByPrimaryKeySelective(ClazzExpertManageInfo record);

    int updateByPrimaryKey(ClazzExpertManageInfo record);

    int updateByUserIdAndClazzId(Integer userId, Integer clazzId, String status);

    List<ClazzExpertManageInfo> selectByUserId(Integer userId);

    int deleteByClazzIdAndUserId(Integer clazzId, Integer userId);

    ClazzExpertManageInfo selectByUserIdAndClazzId(Integer userId, Integer clazzId);

    @Insert("INSERT into clazz_expert_manage_info(user_id, clazz_id, status) values(#{userId},#{clazzId},0)")
    int insertManageInfo(Integer userId, Integer clazzId);
}