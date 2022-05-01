package com.vtmer.microteachingquality.mapper;

import com.vtmer.microteachingquality.model.entity.MajorArchiveManageInfo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface MajorArchiveManageInfoMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(MajorArchiveManageInfo record);

    int insertSelective(MajorArchiveManageInfo record);

    MajorArchiveManageInfo selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(MajorArchiveManageInfo record);

    int updateByPrimaryKey(MajorArchiveManageInfo record);

    MajorArchiveManageInfo selectByUserIdAndMajorIdAndBatchName(Integer userId, Integer majorId, String batchName);

    MajorArchiveManageInfo selectByMajorIdAndBatchName(Integer majorId, String batchName);

    List<MajorArchiveManageInfo> selectByMajorId(Integer majorId);

    List<MajorArchiveManageInfo> selectByUserId(Integer userId);
}