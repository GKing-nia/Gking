package com.vtmer.microteachingquality.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.vtmer.microteachingquality.model.entity.User;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface UserMapper extends BaseMapper<User> {
    int deleteByPrimaryKey(Integer id);

    User selectByPrimaryKey(Integer id);

    List<User> selectAll();

    /**
     * 根据用户名查询用户信息
     *
     * @param userName
     * @return
     */
    User selectByUserName(String userName);

    /**
     * 插入用户信息
     *
     * @param user
     * @return
     */
    int insertUser(User user);

    /**
     * 根据用户id修改密码
     *
     * @param newPwd
     * @param userId
     * @return
     */
    int updatePwdByUserId(String newPwd, Integer userId);

    /**
     * 根据账号所属部门/指定专业查询账号信息
     *
     * @param userBelong
     * @return
     */
    List<User> selectByUserBelong(String userBelong);

    /**
     * 根据账号类型和所属部门/指定专业查询账号信息
     *
     * @param userType
     * @param userBelong
     * @return
     */
    List<User> selectByUserTypeAndUserBelong(String userType, String userBelong);

    /**
     * 根据参数初始化密码
     *
     * @param userName   帐号
     * @param realName   真实姓名
     * @param userBelong 用户所属部门
     * @param newPwd     新密码
     * @return
     */
    Integer updatePwdByUserNameAndRealNameAndUserBelong(String userName, String realName, String userBelong, String newPwd);

    String selectRealNameById(Integer id);

    List<User> selectByUserType(String userType);


}
