package com.vtmer.microteachingquality.mapper;

import com.vtmer.microteachingquality.model.entity.MajorArchiveOpinion;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MajorArchiveOpinionMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(MajorArchiveOpinion record);

    int insertSelective(MajorArchiveOpinion record);

    MajorArchiveOpinion selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(MajorArchiveOpinion record);

    int updateByPrimaryKey(MajorArchiveOpinion record);

    MajorArchiveOpinion selectByBatchNameAndMajorIdAndUserId(String batchName, Integer majorId, Integer userId);
}