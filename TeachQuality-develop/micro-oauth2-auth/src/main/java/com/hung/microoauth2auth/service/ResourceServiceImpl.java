package com.hung.microoauth2auth.service;

import cn.hutool.core.collection.CollUtil;
import com.hung.microoauth2auth.constant.RedisConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * 资源与角色匹配关系管理业务类
 *
 * @author Hung
 * @date 2021年11月15日 18:40
 */
@Service
public class ResourceServiceImpl {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @PostConstruct
    public void initData() {
        Map<String, List<String>> resourceRolesMap = new TreeMap<>();
        resourceRolesMap.put("/teaching/clazz", CollUtil.toList("all"));
        resourceRolesMap.put("/teaching/coursereport", CollUtil.toList("all"));
        resourceRolesMap.put("/teaching/ClazzExpertLeader", CollUtil.toList("all"));
        resourceRolesMap.put("/teaching/courseEvaluationExpert", CollUtil.toList("all"));
        resourceRolesMap.put("/teaching/archive", CollUtil.toList("all"));
        resourceRolesMap.put("/teaching/report", CollUtil.toList("all"));
        resourceRolesMap.put("/teaching/user", CollUtil.toList("all"));
        redisTemplate.opsForHash().putAll(RedisConstant.RESOURCE_ROLES_MAP, resourceRolesMap);
    }
}
