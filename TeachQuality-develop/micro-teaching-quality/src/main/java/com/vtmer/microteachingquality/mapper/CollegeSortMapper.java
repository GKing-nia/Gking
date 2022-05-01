package com.vtmer.microteachingquality.mapper;

import com.vtmer.microteachingquality.model.entity.CollegeSort;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CollegeSortMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(CollegeSort record);

    CollegeSort selectByPrimaryKey(Integer id);

    List<CollegeSort> selectAll();

    int updateByPrimaryKey(CollegeSort record);
}