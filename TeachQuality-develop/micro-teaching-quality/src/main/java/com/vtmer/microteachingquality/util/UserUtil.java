package com.vtmer.microteachingquality.util;

import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.vtmer.microteachingquality.model.entity.User;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Collections;
import java.util.List;

/**
 * @author Hung
 * @date 2022/4/20 18:47
 */
public class UserUtil {

    public static User getCurrentUser() {
        //获取当前user信息
        return JSON.parseObject(JSONUtil.toJsonStr(SecurityContextHolder.getContext().getAuthentication().getPrincipal()), User.class);
    }

    public static List<String> getUserType(User user) {
        //根据学号获取用户的角色类型，因现在表结构未更改，先使用user表中的type代替
        return Collections.singletonList(user.getUserBelong());
    }


}
