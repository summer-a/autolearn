package com.autolearn.icve.service.impl;

import com.autolearn.icve.entity.field.UrlFields;
import com.autolearn.icve.entity.icve.IcveUser;
import com.autolearn.icve.service.IcveLoginService;
import com.autolearn.icve.utils.HttpUtil;
import com.xiaoleilu.hutool.http.HttpResponse;
import com.xiaoleilu.hutool.util.CollectionUtil;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author 胡江斌
 * @version 1.0
 * @title: IcveLoginServiceImpl
 * @projectName autolearn
 * @description: TODO
 * @date 2020/5/5 22:10
 */
@Service
public class IcveLoginServiceImpl implements IcveLoginService {

    /**
     * ICVE用户登录
     *
     * @param username   用户名
     * @param password   用户密码
     * @param verifyCode 图形验证码
     * @return
     */
    @Override
    public IcveUser login(String username, String password, String verifyCode) {

        Map<String, Object> formMap = new HashMap<>(3);
        formMap.put("schoolId", "bxknaesnyyrkzqljk-xhla");
        formMap.put("userName", username);
        formMap.put("userPwd", password);
        formMap.put("verifyCode", verifyCode);

        HttpResponse response = HttpUtil.post(UrlFields.ICVE_LOGIN_REQUEST, formMap);

        IcveUser user = new IcveUser();
        user.setUser(response.body());

        List<String> cookies = response.headerList("Set-Cookie");
        if (CollectionUtil.isNotEmpty(cookies)) {
            Optional<String> cookie = cookies.stream().map(r -> r.split(";")[0]).reduce((before, after) -> before + ";" + after);
            // 作为值返回
            user.setCookie(cookie.get());
        }

        return user;
    }

}
