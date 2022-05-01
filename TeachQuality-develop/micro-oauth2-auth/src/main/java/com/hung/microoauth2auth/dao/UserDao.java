package com.hung.microoauth2auth.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hung.microoauth2auth.entity.User;
import com.vtmer.microteachingquality.model.dto.insert.UserDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author Hung
 * @date 2021/11/3 23:06
 */
@Mapper
public interface UserDao extends BaseMapper<User> {


    /**
     * 根据账号查询用户信息
     *
     * @param username
     * @return
     */
    @Select("select id,user_name,user_pwd,real_name,user_type,user_belong,is_clazz from user where user_name = #{username} limit 1")
    UserDTO getUserByUsername(String username);


    /**
     * 根据用户id查询用户权限
     *
     * @param userId
     * @return
     */
    @Select("SELECT authority FROM t_permission WHERE id IN(" +
            "SELECT permission_id FROM t_role_permission WHERE role_id IN(" +
            "  SELECT role_id FROM t_user_role WHERE user_id = #{userId} ) )")
    List<String> findPermissionsByUserId(Integer userId);

}
