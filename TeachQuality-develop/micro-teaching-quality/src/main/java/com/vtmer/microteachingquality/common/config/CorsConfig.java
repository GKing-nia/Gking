package com.vtmer.microteachingquality.common.config;

import com.vtmer.microteachingquality.common.interceptor.SchoolAccountInterceptor;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.config.annotation.*;

/**
 * 提供跨域支持
 */

public class CorsConfig extends WebMvcConfigurationSupport {

    private CorsConfiguration buildConfig() {
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        // 开启发送Cookie和HTTP认证信息
        corsConfiguration.setAllowCredentials(true);
        // 开启发送Cookie后只能设置请求网页的域名
        corsConfiguration.addAllowedOrigin("http://www.vtmer2021.com");
        //设置特殊域名的跨域
        corsConfiguration.addAllowedOrigin("http://www.vtmer2021.com:90");
        corsConfiguration.addAllowedOrigin("http://vtmer2021.com");
        corsConfiguration.addAllowedOrigin("http://vtmer2021.com:90");
        corsConfiguration.addAllowedOrigin("http://1.14.166.144:90");
        corsConfiguration.addAllowedOrigin("http://1.14.166.144:8080");
        corsConfiguration.addAllowedOrigin("http://1.14.166.144");
        corsConfiguration.addAllowedOrigin("http://evaluation.moxiaoxiao.net");
        // 允许任何头
        corsConfiguration.addAllowedHeader("*");
        // 允许任何方法
        corsConfiguration.addAllowedMethod("*");
        return corsConfiguration;
    }


    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        // 注册
        source.registerCorsConfiguration("/**", buildConfig());
        return new CorsFilter(source);
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // 设置允许跨域的路径
        registry.addMapping("/**")
                // 设置允许跨域请求的域名
                .allowedOrigins("http://www.vtmer2021.com")
                // 是否允许证书 不再默认开启
                .allowCredentials(true)
                // 设置允许的方法
                .allowedMethods("*")
                // 跨域允许时间
                .maxAge(3600);

        //设置一些特殊域名的跨域
        registry.addMapping("/**")
                .allowedOrigins("http://vtmer2021.com")
                .allowCredentials(true)
                .allowedMethods("*")
                .maxAge(3600);
        registry.addMapping("/**")
                .allowedOrigins("http://www.vtmer2021.com:90")
                .allowCredentials(true)
                .allowedMethods("*")
                .maxAge(3600);
        registry.addMapping("/**")
                .allowedOrigins("http://vtmer2021.com:90")
                .allowCredentials(true)
                .allowedMethods("*")
                .maxAge(3600);
        registry.addMapping("/**")
                .allowedOrigins("http://1.14.166.144:90")
                .allowCredentials(true)
                .allowedMethods("*")
                .maxAge(3600);
        registry.addMapping("/**")
                .allowedOrigins("http://1.14.166.144:8080")
                .allowCredentials(true)
                .allowedMethods("*")
                .maxAge(3600);
        registry.addMapping("/**")
                .allowedOrigins("http://1.14.166.144")
                .allowCredentials(true)
                .allowedMethods("*")
                .maxAge(3600);
        registry.addMapping("/**")
                .allowedOrigins("http://evaluation.moxiaoxiao.net")
                .allowCredentials(true)
                .allowedMethods("*")
                .maxAge(3600);
    }

    /**
     * 跨域配置后swagger-ui可能不能访问，需要增加如下配置
     *
     * @param registry
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.
                addResourceHandler("/swagger-ui/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/springfox-swagger-ui/")
                .resourceChain(false);
        registry
                .addResourceHandler("/doc.html")
                .addResourceLocations("classpath:/META-INF/resources/")
                .resourceChain(false);
        registry
                .addResourceHandler("/doc.html#/**")
                .addResourceLocations("classpath:/META-INF/resources/")
                .resourceChain(false);
        registry.addResourceHandler("/webjars/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/")
                .resourceChain(false);
    }

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/swagger-ui")
                .setViewName("forward:/swagger-ui/index.html");
    }


    /**
     * 添加拦截器，对请求课程自评报告相关的接口进行身份校验
     *
     * @param registry
     */
    @Override
    protected void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new SchoolAccountInterceptor()).addPathPatterns("/coursereport/schoolAccount/**");
    }
}