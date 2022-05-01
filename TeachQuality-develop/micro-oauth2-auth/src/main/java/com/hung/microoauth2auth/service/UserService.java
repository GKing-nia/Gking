package com.hung.microoauth2auth.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hung.microoauth2auth.entity.User;
import com.vtmer.microteachingquality.model.dto.insert.UserDTO;

import java.util.List;

/**
 * @author Hung
 * @date 2022/4/8 21:50
 */
public interface UserService extends IService<User> {
    /**
     * 根据账号查询用户信息
     *
     * @param username
     * @return
     */
    UserDTO getUserByUsername(String username);


    /**
     * 根据用户id查询用户权限
     *
     * @param userId
     * @return
     */
    List<String> findPermissionsByUserId(Integer userId);


}
