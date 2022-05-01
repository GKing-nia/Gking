package com.hung.microoauth2auth.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.hung.microoauth2auth.entity.*;
import com.hung.microoauth2auth.service.UserService;
import com.hung.microoauth2commons.commonutils.api.Result;
import com.hung.microoauth2commons.commonutils.utils.RegexUtils;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.endpoint.TokenEndpoint;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.security.Principal;
import java.util.Map;
import java.util.Objects;

import static com.hung.microoauth2commons.commonutils.utils.RedisConstants.FORGET_CODE_KEY;
import static com.hung.microoauth2commons.commonutils.utils.RedisConstants.USER_CODE_KEY;

/**
 * 自定义Oauth2获取令牌接口
 *
 * @author Hung
 * @date 2021年11月23日 10:42:10
 */
@RestController
@RequestMapping("/oauth")
public class AuthController {

    @Autowired
    private TokenEndpoint tokenEndpoint;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private UserService userService;

    /**
     * Oauth2登录认证
     */
    @ApiOperation("登录认证，获取token")
    @RequestMapping(value = "/token", method = RequestMethod.POST)
    public Result<Oauth2TokenDTO> postAccessToken(Principal principal, @RequestParam Map<String, String> parameters) throws HttpRequestMethodNotSupportedException {
        OAuth2AccessToken oAuth2AccessToken = tokenEndpoint.postAccessToken(principal, parameters).getBody();
        Oauth2TokenDTO oauth2TokenDto = Oauth2TokenDTO.builder()
                .token(Objects.requireNonNull(oAuth2AccessToken).getValue())
                .refreshToken(oAuth2AccessToken.getRefreshToken().getValue())
                .expiresIn(oAuth2AccessToken.getExpiresIn())
                .tokenHead("Bearer ").build();
        return Result.success(oauth2TokenDto);
    }




}
