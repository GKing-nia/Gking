package com.vtmer.microteachingquality.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.vtmer.microteachingquality.model.entity.Clazz;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ClazzMapper extends BaseMapper<Clazz> {
    int deleteByPrimaryKey(Integer id);

    int insert(Clazz record);

    int insertSelective(Clazz record);

    Clazz selectByPrimaryKey(Integer id);

    Clazz selectByName(String name);

    Clazz selectByKeyAndStatus(Integer id, Integer status);

    @Select("select 'id','college','major','name','userId','clazzSerialNumber','type','createTime','updateTime' from clazz; ")
    List<Clazz> selectAll();

    int updateByPrimaryKeySelective(Clazz record);

    int updateByPrimaryKey(Clazz record);

    int insertEmptyData(String tableName);

    @Select("select id from clazz where college= #{college} and major=#{major} and type=#{clazzType} and name=#{clazzName};")
    Integer selectClazzId(String college, String major, String clazzType, String clazzName);
}