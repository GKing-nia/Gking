package com.vtmer.microteachingquality.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.vtmer.microteachingquality.model.entity.ClazzLeaderInfo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ClazzLeaderInfoMapper extends BaseMapper<ClazzLeaderInfo> {
    int deleteByPrimaryKey(Integer id);

    int insert(ClazzLeaderInfo record);

    int insertSelective(ClazzLeaderInfo record);

    ClazzLeaderInfo selectByPrimaryKey(Integer id);

    List<ClazzLeaderInfo> selectByLeaderId(Integer leaderId);

    int updateByPrimaryKeySelective(ClazzLeaderInfo record);

    int updateByPrimaryKey(ClazzLeaderInfo record);
}