package com.vtmer.microteachingquality.mapper;

import com.vtmer.microteachingquality.model.entity.LeaderInfo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface LeaderInfoMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(LeaderInfo record);

    int insertSelective(LeaderInfo record);

    LeaderInfo selectByPrimaryKey(Integer id);

    List<LeaderInfo> selectByLeaderId(Integer leaderId);

    int updateByPrimaryKeySelective(LeaderInfo record);

    int updateByPrimaryKey(LeaderInfo record);
}