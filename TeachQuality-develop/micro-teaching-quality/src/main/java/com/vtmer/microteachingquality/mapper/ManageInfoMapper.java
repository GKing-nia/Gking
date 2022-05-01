package com.vtmer.microteachingquality.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.vtmer.microteachingquality.model.entity.ManageInfo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ManageInfoMapper extends BaseMapper<ManageInfo> {
    int deleteByPrimaryKey(Integer id);

    int insert(ManageInfo record);

    int insertSelective(ManageInfo record);

    ManageInfo selectByPrimaryKey(Integer id);

    List<ManageInfo> selectByUserId(Integer userId);

    int updateByPrimaryKeySelective(ManageInfo record);

    int updateByPrimaryKey(ManageInfo record);

    int selectCountsByUserId(Integer userId);
}