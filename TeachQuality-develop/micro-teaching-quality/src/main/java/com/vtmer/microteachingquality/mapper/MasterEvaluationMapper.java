package com.vtmer.microteachingquality.mapper;

import com.vtmer.microteachingquality.model.entity.MasterEvaluation;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface MasterEvaluationMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(MasterEvaluation record);

    MasterEvaluation selectByPrimaryKey(Integer id);

    List<MasterEvaluation> selectAll();

    int updateByPrimaryKey(MasterEvaluation record);

    /**
     * 根据用户id查询评审意见
     *
     * @param userId
     * @return
     */
    List<MasterEvaluation> selectByUserId(Integer userId);


    /**
     * 根据用户id和专业id获取评审意见
     *
     * @param userId
     * @param majorId
     * @return
     */
    List<MasterEvaluation> selectByUserIdAndMajorId(Integer userId, Integer majorId);

    /**
     * 根据用户id修改评估状态
     *
     * @param userId
     * @param status
     * @return
     */
    int updateStatusByUserId(Integer userId, String status);

    /**
     * 根据用户id和专业id更新评估状态
     *
     * @param userId
     * @param status
     * @return
     */
    int updateStatusByUserIdAndMajorId(Integer userId, Integer majorId, String status);
}