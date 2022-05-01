package com.vtmer.microteachingquality.mapper;

import com.vtmer.microteachingquality.model.entity.ReportAnnex;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ReportAnnexMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(ReportAnnex record);

    ReportAnnex selectByPrimaryKey(Integer id);

    List<ReportAnnex> selectAll();

    int updateByPrimaryKey(ReportAnnex record);
}