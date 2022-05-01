package com.vtmer.microteachingquality.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.vtmer.microteachingquality.model.entity.ClazzFile;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ClazzFileMapper extends BaseMapper<ClazzFile> {
    int deleteByPrimaryKey(Integer id);

    int insert(ClazzFile record);

    int insertSelective(ClazzFile record);

    ClazzFile selectByPrimaryKey(Integer id);

    ClazzFile selectByClazzNameAndUserId(Integer userId, String clazzName);

    Integer selectUserIdByClazzName(String clazzName);

    int updateByPrimaryKeySelective(ClazzFile record);

    int updateByPrimaryKey(ClazzFile record);

    List<ClazzFile> selectByClazzName(String clazzName);

    List<ClazzFile> selectByUserId(Integer userId);

    @Delete("delete from clazz_file where path=#{path};")
    Integer deleteByPath(String path);
}