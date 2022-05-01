package com.vtmer.microteachingquality.common.interceptor;

import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.vtmer.microteachingquality.common.ResponseMessage;
import com.vtmer.microteachingquality.common.constant.enums.UserType;
import com.vtmer.microteachingquality.model.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 拦截非学校用户账号的请求
 */
@Component
@Slf4j
public class SchoolAccountInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        log.info("uri是:{}", request.getRequestURI());

        User user = JSON.parseObject(JSONUtil.toJsonStr(SecurityContextHolder.getContext().getAuthentication().getPrincipal()), User.class);
        String userType = user.getUserType();
        if (userType.equals(UserType.SCHOOL.getType())) {
            return true;
        }
        response.setHeader("Access-Control-Allow-Origin", request.getHeader("Origin"));
        response.setHeader("Access-Control-Allow-Credentials", "true");
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Content-type", "application/json; charset=utf-8");

        try {
            response.getWriter().print(JSONUtil.toJsonStr(ResponseMessage.newErrorInstance("非学校账户")));
            response.flushBuffer();
        } catch (Exception e) {
        }
        return false;
    }
}
