package com.autolearn.icve.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.List;

/**
 * 安全配置
 * @author 胡江斌
 * @version 1.0
 * @title: SecurityConfig
 * @projectName autolearn
 * @description: TODO
 * @date 2020/5/6 17:46
 */
@Configuration
public class SecurityConfig implements WebMvcConfigurer {

    @Resource
    private LoginInterceptor loginInterceptor;

    /**
     * 拦截器
     * @param registry
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        List<String> noLoginUrl = Arrays.asList(
                "/api/threadpool/info",
                "/api/verifyCode",
                "/api/msg",
                "/api/set/msg",
                "/api/state/task",
                "/api/login",
                "/api/pay"
        );
        registry.addInterceptor(loginInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns(noLoginUrl);
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowCredentials(true)
                .allowedHeaders("*")
                .allowedMethods("POST", "GET", "OPTIONS")
                .allowedOrigins("*")
                .maxAge(3600);
    }

}
