package com.autolearn.icve.utils;

import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.autolearn.icve.entity.icve.IcveUser;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * @author 胡江斌
 * @version 1.0
 * @title: SpringUtil
 * @projectName autolearn
 * @description: TODO
 * @date 2020/8/22 18:44
 */
public class SpringUtil {

    public static HttpServletRequest getCurrentRequest() {
        return ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
    }

    public static String getCurrentToken() {
        return getCurrentRequest().getHeader("token");
    }

    public static IcveUser.User getCurrentUser() {
        String currentToken = getCurrentToken();
        if (null == currentToken) {
            throw new JWTDecodeException("Token无效");
        }
        try {
            DecodedJWT require = JWTUtil.require(currentToken);
            return JWTUtil.jwtToUser(require);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new IcveUser.User();
    }
}
