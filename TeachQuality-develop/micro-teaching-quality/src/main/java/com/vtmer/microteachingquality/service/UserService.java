package com.vtmer.microteachingquality.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.vtmer.microteachingquality.model.dto.insert.UserDTO;
import com.vtmer.microteachingquality.model.dto.result.UserInfoResult;
import com.vtmer.microteachingquality.model.entity.User;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface UserService extends IService<User> {

   /**
     * 创建账号
     *
     * @param user
     * @return
     */
    int saveUser(User user);

    /**
     * 修改密码
     *
     * @param loginUser
     * @param newPwd
     * @return
     */
    int updatePwd(User loginUser, String newPwd);

    /**
     * 获取全部账号信息
     *
     * @return
     */
    List<UserInfoResult> listUserInfo();

    /**
     * 根据账号所属部门/指定专业获取账号信息
     *
     * @param userBelong
     * @return
     */
    List<UserInfoResult> listUserInfoByUserBelong(String userBelong);

    /**
     * 根据账号类型和所属部门/指定专业获取账号信息
     *
     * @param userType
     * @param userBelong
     * @return
     */
    List<UserInfoResult> listUserInfoByUserTypeAndUserBelong(String userType, String userBelong);

    /**
     * 根据账号id注销(删除)账号
     *
     * @param userId
     * @return
     */
    int cancelAccount(Integer userId);

    /**
     * 根据用户id重置账号密码
     *
     * @param userId
     * @return
     */
    int updatePwdByUserId(Integer userId);

/*    *//**
     * 登录界面根据用户信息重置密码
     *//*
    Integer updatePwdByUserNameAndRealNameAndUserBelong(String userName, String realName, String userBelong);*/

    Boolean sendCode(String email);

    @Transactional(rollbackFor = Exception.class)
    int register(UserDTO user);

    Boolean putRole(Integer userId, String role);

    Boolean sendCodeWithForgetting(String email);
}
