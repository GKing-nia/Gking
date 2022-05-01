package com.vtmer.microteachingquality.mapper;


import com.vtmer.microteachingquality.model.entity.ClazzAnnotation;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ClazzAnnotationMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(ClazzAnnotation record);

    int insertSelective(ClazzAnnotation record);

    ClazzAnnotation selectByPrimaryKey(Integer id);

    List<ClazzAnnotation> selectAll();

    int updateByPrimaryKeySelective(ClazzAnnotation record);

    int updateByPrimaryKey(ClazzAnnotation record);
}