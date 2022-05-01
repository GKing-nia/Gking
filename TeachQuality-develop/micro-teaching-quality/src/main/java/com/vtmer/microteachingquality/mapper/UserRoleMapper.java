package com.vtmer.microteachingquality.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.vtmer.microteachingquality.model.entity.UserRole;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface UserRoleMapper extends BaseMapper<UserRole> {
    @Delete("delete from t_user_role where user_id= #{id};")
    int deleteByPrimaryKey(Integer id);

    @Insert("insert into t_user_role(user_id,role_id) values (#{userId},#{roleId});")
    int insertRecord(UserRole record);

    @Select("select id,user_id,role_id from t_user_role where id = #{id}")
    UserRole selectByPrimaryKey(Integer id);

    @Select("select id,user_id,role_id from t_user_role;")
    List<UserRole> selectAll();

    @Update("update t_user_role set user_id=#{userId},role_id=#{roleId} where user_id =#{id}")
    int updateByPrimaryKey(UserRole record);

    /**
     * 根据用户id查询角色id
     *
     * @param userId
     * @return
     */
    @Select("select role_id from t_user_role where user_id=#{userId};")
    List<Integer> selectRoleIdByUserId(Integer userId);
}