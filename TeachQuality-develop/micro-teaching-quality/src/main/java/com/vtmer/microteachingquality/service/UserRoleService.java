package com.vtmer.microteachingquality.service;

import java.util.List;

public interface UserRoleService {

    /**
     * 根据用户名获取角色id
     *
     * @param userName
     * @return
     */
    List<Integer> listRoleId(String userName);

}
