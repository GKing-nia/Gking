package com.vtmer.microteachingquality.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.vtmer.microteachingquality.model.entity.ClazzEvaluateOptionRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ClazzEvaluateOptionRecordMapper extends BaseMapper<ClazzEvaluateOptionRecord> {
    int deleteByPrimaryKey(Integer id);

    int insert(ClazzEvaluateOptionRecord record);

    int insertSelective(ClazzEvaluateOptionRecord record);

    int insertList(List<ClazzEvaluateOptionRecord> recordList);

    ClazzEvaluateOptionRecord selectByPrimaryKey(Integer id);

    @Select("select  id, user_id, evaluation_id, evaluation_option_id, mark, create_time, update_time from clazz_evaluate_option_record" +
            "        where evaluation_id= #{evaluationId} and user_id= #{userId};")
    List<ClazzEvaluateOptionRecord> selectByClazzIdAndUserId(Long evaluationId, Integer userId);

    int updateByPrimaryKeySelective(ClazzEvaluateOptionRecord record);

    int updateByPrimaryKey(ClazzEvaluateOptionRecord record);

    int deleteByClazzIdAndUserId(Integer clazzId, Integer userId);
}