package com.autolearn.icve.service.impl;

import com.autolearn.icve.entity.field.UrlFields;
import com.autolearn.icve.entity.icve.IcveUser;
import com.autolearn.icve.service.IcveLoginService;
import com.autolearn.icve.utils.HttpUtil;
import com.xiaoleilu.hutool.http.HttpException;
import com.xiaoleilu.hutool.http.HttpResponse;
import com.xiaoleilu.hutool.util.CollectionUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.Base64Utils;
import sun.misc.BASE64Encoder;

import java.util.*;

/**
 * @author 胡江斌
 * @version 1.0
 * @title: IcveLoginServiceImpl
 * @projectName autolearn
 * @description: TODO
 * @date 2020/5/5 22:10
 */
@Slf4j
@Service
public class IcveLoginServiceImpl implements IcveLoginService {

    /**
     * ICVE用户登录
     *
     * @param username   用户名
     * @param password   用户密码
     * @param verifyCode 图形验证码
     * @param verifyCodeCookie 验证码cookie
     * @return
     */
    @Override
    public IcveUser login(String username, String password, String verifyCode, String verifyCodeCookie) {

        IcveUser user = null;
        try {
            Optional<String> verifyCodeCookieStr = Arrays.stream(verifyCodeCookie.split(",")).map(cookie -> cookie.split(";")[0]).reduce((before, after) -> before + ";" + after);

            Map<String, Object> formMap = new HashMap<>(3);
            formMap.put("userName", username);
            formMap.put("userPwd", password);
            formMap.put("verifyCode", verifyCode);

            HttpResponse response = HttpUtil.post(UrlFields.ICVE_LOGIN_REQUEST, verifyCodeCookieStr.get(), formMap);

            user = new IcveUser();
            user.setUser(response.body());

            List<String> cookies = response.headerList("Set-Cookie");
            if (CollectionUtil.isNotEmpty(cookies)) {
                Optional<String> cookie = cookies.stream().map(r -> r.split(";")[0]).reduce((before, after) -> before + ";" + after);
                // 作为值返回
                user.setCookie(cookie.get() + ";token=" + user.getUser().getToken());
            }
        } catch (HttpException e) {
            log.error("登录失败", e);
        }

        return user;
    }

    /**
     * 获取验证码
     * @return
     */
    @Override
    public Map<String, String> verifyCode() {

        Map<String, String> map = new HashMap<>(2);
        try {

            HttpResponse response = HttpUtil.get(UrlFields.ICVE_VERIFY_CODE + "?t=" + Math.random());

            String result = Base64Utils.encodeToString(response.bodyBytes());

            map.put("base64", "data:image/gif;base64," + result);

            List<String> cookies = response.headerList("Set-Cookie");
            Optional<String> verifyCodeCookieStr = cookies.stream().map(cookie -> cookie.split(";")[0]).reduce((before, after) -> before + ";" + after);

            map.put("cookie", verifyCodeCookieStr.get());
            map.put("code", "200");
        } catch (Exception e){
            log.error("获取验证码失败", e);
            map.put("code", "500");
            map.put("msg", "获取验证码失败");
        }

        return map;
    }

}
