package com.hung.microoauth2auth.exception;

import com.hung.microoauth2commons.commonutils.api.Result;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 全局处理Oauth2抛出的异常
 *
 * @author Hung
 * @date 2021/11/3 0:10
 */
@ControllerAdvice
public class Oauth2ExceptionHandler {
    @ResponseBody
    @ExceptionHandler(value = OAuth2Exception.class)
    public Result<?> handleOauth2(OAuth2Exception e) {
        return Result.failed(e.getMessage());
    }
}
