package com.autolearn.icve.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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

    /**
     * 拦截器
     * @param registry
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new LoginInterceptor())
                .addPathPatterns("/**")
                .excludePathPatterns("/api/login");
    }

    /**
     * 登录请求拦截器
     */
    class LoginInterceptor implements HandlerInterceptor {

        /**
         * 前置处理器
         * @param request
         * @param response
         * @param handler
         * @return
         * @throws Exception
         */
        @Override
        public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
//            String cookie = request.getHeader("Cookie");
//            if (StringUtils.isEmpty(cookie)) {
//                return false;
//            }
            // 会话过期处理

            return true;
        }


        /**
         * 后置处理器
         * @param request
         * @param response
         * @param handler
         * @param modelAndView
         */
        @Override
        public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) {

        }
    }
}
