package com.vtmer.microteachingquality.mapper;

import com.vtmer.microteachingquality.model.entity.MajorArchiveFile;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface MajorArchiveFileMapper {
    int deleteByPrimaryKey(Integer id);

    int deleteByUserIdAndPath(Integer userId, String path);

    int insert(MajorArchiveFile record);

    int insertSelective(MajorArchiveFile record);

    MajorArchiveFile selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(MajorArchiveFile record);

    int updateByPrimaryKey(MajorArchiveFile record);

    List<MajorArchiveFile> selectByUserId(Integer userId);

    List<MajorArchiveFile> selectByBatchNameAndMajorId(String batchName, Integer majorId);
}