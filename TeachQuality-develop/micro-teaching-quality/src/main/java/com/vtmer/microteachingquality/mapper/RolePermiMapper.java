package com.vtmer.microteachingquality.mapper;

import com.vtmer.microteachingquality.model.entity.RolePermi;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface RolePermiMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(RolePermi record);

    RolePermi selectByPrimaryKey(Integer id);

    List<RolePermi> selectAll();

    int updateByPrimaryKey(RolePermi record);

    /**
     * 根据角色id查询权限id
     *
     * @param roleId
     * @return
     */
    List<Integer> selectPermiIdByRoleId(Integer roleId);
}