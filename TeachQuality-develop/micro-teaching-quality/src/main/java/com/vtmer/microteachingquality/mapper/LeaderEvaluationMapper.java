package com.vtmer.microteachingquality.mapper;

import com.vtmer.microteachingquality.model.entity.LeaderEvaluation;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface LeaderEvaluationMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(LeaderEvaluation record);

    LeaderEvaluation selectByPrimaryKey(Integer id);

    List<LeaderEvaluation> selectAll();

    int updateByPrimaryKey(LeaderEvaluation record);

    /**
     * 根据用户id查询
     *
     * @param userId
     * @return
     */
    List<LeaderEvaluation> selectByUserId(Integer userId);

    /**
     * 根据用户id和专业id查询
     *
     * @param userId
     * @param majorId
     * @return
     */
    List<LeaderEvaluation> selectByUserIdAndMajorId(Integer userId, Integer majorId);

}