package com.vtmer.microteachingquality.service;

import java.util.List;

public interface PermissionService {

    /**
     * 根据角色id获取权限url集合
     *
     * @param roleId
     * @return
     */
    List<String> listPermiUrl(Integer roleId);

}
