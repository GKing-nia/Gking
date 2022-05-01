package com.vtmer.microteachingquality.mapper;

import com.vtmer.microteachingquality.model.entity.EvaluationOption;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface EvaluationOptionMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(EvaluationOption record);

    EvaluationOption selectByPrimaryKey(Integer id);

    List<EvaluationOption> selectAll();

    int updateByPrimaryKey(EvaluationOption record);

    /**
     * 根据一级指标查询所有具体内容
     *
     * @param firstTarget
     * @return
     */
    List<EvaluationOption> selectByFirstTarget(String firstTarget);

    /**
     * 根据科目类型获取所有指标信息
     *
     * @param collegeSort
     * @return
     */
    List<EvaluationOption> selectByCollageSort(String collegeSort);
}