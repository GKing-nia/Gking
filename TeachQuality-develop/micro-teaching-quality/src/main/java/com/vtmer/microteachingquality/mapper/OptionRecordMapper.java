package com.vtmer.microteachingquality.mapper;

import com.vtmer.microteachingquality.model.entity.OptionRecord;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface OptionRecordMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(OptionRecord record);

    OptionRecord selectByPrimaryKey(Integer id);

    List<OptionRecord> selectAll();

    int updateByPrimaryKey(OptionRecord record);

    /**
     * 根据用户id查询评审选项记录
     *
     * @param userId
     * @return
     */
    List<OptionRecord> selectByUserId(Integer userId);

    /**
     * 根据用户id和专业id查询评审选项记录
     *
     * @param majorId
     * @param userId
     * @return
     */
    List<OptionRecord> selectByUserIdAndMajorId(Integer userId, Integer majorId);
}