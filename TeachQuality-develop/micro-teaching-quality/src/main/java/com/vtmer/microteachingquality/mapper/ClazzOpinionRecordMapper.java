package com.vtmer.microteachingquality.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.vtmer.microteachingquality.model.dto.select.ClazzOpinionRecordDTO;
import com.vtmer.microteachingquality.model.entity.ClazzOpinionRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ClazzOpinionRecordMapper extends BaseMapper<ClazzOpinionRecord> {
    int deleteByPrimaryKey(Integer id);

    int insert(ClazzOpinionRecord record);

    int insertSelective(ClazzOpinionRecord record);

    ClazzOpinionRecord selectByClazzId(Integer clazzId);

    @Select("select id, evaluation_id, user_id,clazz_advantage,clazz_problem,clazz_advice, create_time, update_time from clazz_opinion_record " +
            "where user_id = #{userId} and evaluation_id = #{evaluationId}")
    ClazzOpinionRecord selectByEvaluationIdAndUserId(Long evaluationId, Integer userId);

    ClazzOpinionRecord selectByPrimaryKey(Integer id);

    List<ClazzOpinionRecord> selectAll();

    int updateByPrimaryKeySelective(ClazzOpinionRecord record);

    int updateByPrimaryKey(ClazzOpinionRecord record);

    int deleteByClazzIdAndUserId(Integer clazzId, Integer userId);

    @Select("select user_id,update_time from clazz_opinion_record where evaluation = ?")
    List<ClazzOpinionRecordDTO> getAllReviewInfo(Long evaluationId);
}