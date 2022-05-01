package com.vtmer.microteachingquality.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.vtmer.microteachingquality.model.entity.ClazzEvaluateOption;
import com.vtmer.microteachingquality.model.entity.ClazzEvaluateOptionRecord;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ClazzEvaluateOptionMapper extends BaseMapper<ClazzEvaluateOptionRecord> {
    int deleteByPrimaryKey(Integer id);

    int insert(ClazzEvaluateOption record);

    int insertSelective(ClazzEvaluateOption record);

    ClazzEvaluateOption selectByPrimaryKey(Integer id);

    List<ClazzEvaluateOption> selectByClazzType(String clazzType);

    int updateByPrimaryKeySelective(ClazzEvaluateOption record);

    int updateByPrimaryKey(ClazzEvaluateOption record);
}