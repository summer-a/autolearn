package com.autolearn.icve.controller;

import com.autolearn.icve.entity.icve.IcveUser;
import com.autolearn.icve.entity.icve.ResultVO;
import com.autolearn.icve.service.IcveLoginService;
import com.autolearn.icve.utils.JWTUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author 胡江斌
 * @version 1.0
 * @title: LoginApi
 * @projectName autolearn
 * @description: TODO
 * @date 2020/8/27 17:32
 */
@Slf4j
@RequestMapping("/api")
@RestController
public class LoginApi {

    /**
     * 设置是否可以登录
     */
    private static AtomicBoolean canLogin = new AtomicBoolean(true);

    @Resource
    private IcveLoginService icveLoginService;

    /**
     * 登录请求
     *
     * @param username
     * @param password
     * @param verifyCode
     * @param verifyCodeCookie
     * @return
     */
    @PostMapping("/login")
    public ResultVO<IcveUser> login(
            String username,
            String password,
            String verifyCode,
            String verifyCodeCookie) {
        // 如果登录被禁用
        if (!canLogin.get()) {
            return ResultVO.fail("当前登录功能暂时被禁用");
        }
        log.info("[ " + username + " ] 登录");
        IcveUser user = icveLoginService.login(username, password, verifyCode, verifyCodeCookie);
        if (user != null && user.getUser() != null) {
            if (Objects.equals(user.getUser().getCode(), 1)) {
                // 登录成功
                Map<String, String> map = new HashMap<>();
                IcveUser.User innerUser = user.getUser();

                map.put("id", innerUser.getUserId());
                map.put("displayName", innerUser.getDisplayName());
                map.put("username", innerUser.getUserName());
                map.put("avator", innerUser.getAvator());
                map.put("cookie", user.getCookie());

                String token = JWTUtil.createToken(map);
                user.setToken(token);
                return ResultVO.ok("success", user);
            }
        }
        return ResultVO.fail("登录失败,请检查账号密码和验证码");
    }

    /**
     * 获取验证码
     *
     * @return
     */
    @GetMapping("/verifyCode")
    public Map<String, String> verifyCode() {
        return icveLoginService.verifyCode();
    }

    /**
     * 允许登录
     *
     * @return
     */
    @GetMapping(value = "/login/open")
    public ResultVO openLogin() {
        log.info("允许登录");
        canLogin.set(true);
        return ResultVO.ok();
    }

    /**
     * 禁止登录
     *
     * @return
     */
    @GetMapping(value = "/login/close")
    public ResultVO closeLogin() {
        log.info("禁止登陆");
        canLogin.set(false);
        return ResultVO.ok();
    }
}
