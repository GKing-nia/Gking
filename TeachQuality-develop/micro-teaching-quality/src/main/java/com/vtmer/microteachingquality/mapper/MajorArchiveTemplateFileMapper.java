package com.vtmer.microteachingquality.mapper;

import com.vtmer.microteachingquality.model.entity.MajorArchiveTemplateFile;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface MajorArchiveTemplateFileMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(MajorArchiveTemplateFile record);

    int insertSelective(MajorArchiveTemplateFile record);

    MajorArchiveTemplateFile selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(MajorArchiveTemplateFile record);

    int updateByPrimaryKey(MajorArchiveTemplateFile record);

    List<MajorArchiveTemplateFile> selectAll();

    int deleteByFileNameAndPath(String fileName, String path);
}