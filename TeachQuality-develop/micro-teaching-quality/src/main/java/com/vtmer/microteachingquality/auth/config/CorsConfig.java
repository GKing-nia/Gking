package com.vtmer.microteachingquality.auth.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

/**
 * @author Hung
 * @date 2022/4/12 9:52
 */
@Configuration
public class CorsConfig {
    @Bean
    public CorsWebFilter corsWebFilter() {

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration corsConfiguration = new CorsConfiguration();

        //配置跨域
        corsConfiguration.addAllowedHeader("*");//允许所有带请求头的请求访问
        corsConfiguration.addAllowedMethod("*");//允许所有的请求方式访问
        corsConfiguration.addAllowedOrigin("*");//允许所有的请求位置访问
        corsConfiguration.setAllowCredentials(true);//允许侵权带coke

        source.registerCorsConfiguration("/**", corsConfiguration);

        return new CorsWebFilter(source);
    }

}
