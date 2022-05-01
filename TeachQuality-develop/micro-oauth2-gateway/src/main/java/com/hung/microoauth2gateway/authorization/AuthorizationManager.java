package com.hung.microoauth2gateway.authorization;

import cn.hutool.core.convert.Convert;
import com.hung.microoauth2gateway.constant.AuthConstant;
import com.hung.microoauth2gateway.constant.RedisConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.ReactiveAuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.server.authorization.AuthorizationContext;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 鉴权管理器，用于判断是否有资源的访问权限
 * 用于鉴定用户角色权限(user表)
 *
 * @author Hung
 * @date 2021年11月13日 21:52:08
 */
@Component
public class AuthorizationManager implements ReactiveAuthorizationManager<AuthorizationContext> {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    public Mono<AuthorizationDecision> check(Mono<Authentication> mono, AuthorizationContext authorizationContext) {
        //获取访问路径
        URI uri = authorizationContext.getExchange().getRequest().getURI();
        //从Redis中获取当前路径可访问角色列表
        Object obj = redisTemplate.opsForHash().get(RedisConstant.RESOURCE_ROLES_MAP, uri.getPath());
        String path = "/teaching/" + uri.getPath().split("\\/")[2];
        Object objStartWith = redisTemplate.opsForHash().get(RedisConstant.RESOURCE_ROLES_MAP, path);
        List<String> authorities = Convert.toList(String.class, obj);
        authorities.addAll(Convert.toList(String.class, objStartWith));
        authorities = authorities.stream().map(i -> i = AuthConstant.AUTHORITY_PREFIX + i).collect(Collectors.toList());
        //认证通过且角色匹配的用户可访问当前路径
        return mono
                .filter(Authentication::isAuthenticated)
                .flatMapIterable(Authentication::getAuthorities)
                .map(GrantedAuthority::getAuthority)
                .any(authorities::contains)
                .map(AuthorizationDecision::new)
                .defaultIfEmpty(new AuthorizationDecision(false));
    }

}
