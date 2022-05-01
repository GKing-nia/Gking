package com.hung.microoauth2auth.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hung.microoauth2auth.dao.UserDao;
import com.hung.microoauth2auth.entity.User;
import com.vtmer.microteachingquality.model.dto.insert.UserDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;


/**
 * @author Hung
 * @date 2022/4/8 21:50
 */
@Service
public class UserServiceImpl  extends ServiceImpl<UserDao, User> implements UserService {
    @Autowired
    private UserDao userDao;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public UserDTO getUserByUsername(String username) {
        return userDao.getUserByUsername(username);
    }

    @Override
    public List<String> findPermissionsByUserId(Integer userId) {
        return userDao.findPermissionsByUserId(userId);
    }


}
