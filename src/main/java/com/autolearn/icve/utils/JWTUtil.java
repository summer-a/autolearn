package com.autolearn.icve.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.autolearn.icve.entity.icve.IcveUser;

import java.util.Calendar;
import java.util.Map;

/**
 * @author 胡江斌
 * @version 1.0
 * @title: JWTUtil
 * @projectName autolearn
 * @description: TODO
 * @date 2020/8/27 17:05
 */
public class JWTUtil {

    /**
     * 盐
     */
    private static final String SALT = "hujiangbing88619973";

    /**
     * 超时时间(小时)
     */
    private static final int TIME_OUT = 24;

    /**
     * 创建token
     * @param map
     * @return
     */
    public static String createToken(Map<String, String> map) {
        JWTCreator.Builder builder = JWT.create();
        map.forEach((k, v) -> builder.withClaim(k, v));

        Calendar instance = Calendar.getInstance();
        instance.add(Calendar.HOUR, TIME_OUT);
        // 设置超时时间
        builder.withExpiresAt(instance.getTime());
        return builder.sign(Algorithm.HMAC256(SALT));
    }

    /**
     * 验证并返回token
     * @param token
     * @return
     */
    public static DecodedJWT require(String token) {
        if (null == token) {
            throw new JWTDecodeException("token为空");
        }
        JWTVerifier build = JWT.require(Algorithm.HMAC256(SALT)).build();
        try {
            return build.verify(token);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 获取当前用户
     * @param jwt
     * @return
     */
    public static IcveUser.User jwtToUser(DecodedJWT jwt) {
        if (null == jwt) {
            throw new JWTDecodeException("token为空");
        }
        IcveUser.User user = new IcveUser.User();
        user.setUserId(jwt.getClaim("id").asString());
        user.setDisplayName(jwt.getClaim("displayName").asString());
        user.setUserName(jwt.getClaim("username").asString());
        user.setAvator(jwt.getClaim("avator").asString());
        user.setToken(jwt.getClaim("cookie").asString());
        return user;
    }
}
