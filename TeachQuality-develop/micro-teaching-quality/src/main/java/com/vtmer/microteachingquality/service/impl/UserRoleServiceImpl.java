package com.vtmer.microteachingquality.service.impl;

import com.vtmer.microteachingquality.mapper.UserMapper;
import com.vtmer.microteachingquality.mapper.UserRoleMapper;
import com.vtmer.microteachingquality.service.UserRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(rollbackFor = Exception.class)
public class UserRoleServiceImpl implements UserRoleService {
    @Autowired
    private UserRoleMapper userRoleMapper;
    @Autowired
    private UserMapper userMapper;

    @Override
    public List<Integer> listRoleId(String userName) {
        Integer userId = userMapper.selectByUserName(userName).getId();
        List<Integer> roleIdList = userRoleMapper.selectRoleIdByUserId(userId);
        return roleIdList == null ? new ArrayList<>() : roleIdList;
    }

}
