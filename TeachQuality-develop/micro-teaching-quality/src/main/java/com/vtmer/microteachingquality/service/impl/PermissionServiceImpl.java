package com.vtmer.microteachingquality.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.vtmer.microteachingquality.mapper.PermissionMapper;
import com.vtmer.microteachingquality.mapper.RolePermiMapper;
import com.vtmer.microteachingquality.model.entity.Permission;
import com.vtmer.microteachingquality.service.PermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(rollbackFor = Exception.class)
public class PermissionServiceImpl implements PermissionService {

    @Autowired
    private PermissionMapper permissionMapper;
    @Autowired
    private RolePermiMapper rolePermiMapper;

    @Override
    public List<String> listPermiUrl(Integer roleId) {
        List<String> permiUrlList = new ArrayList<>();
        List<Integer> permiIdList = rolePermiMapper.selectPermiIdByRoleId(roleId);
        for (Integer permiId : permiIdList) {
            Permission permission = permissionMapper.selectByPrimaryKey(permiId);
            if (ObjectUtil.isNotNull(permission)) {
                permiUrlList.add(permission.getUrl());
            }
        }
        return permiUrlList;
    }

}
