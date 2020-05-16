package com.autolearn.icve.service;

import com.autolearn.icve.entity.icve.IcveUser;

/**
 * @author 胡江斌
 * @version 1.0
 * @title: IcveLoginService
 * @projectName autolearn
 * @description: TODO
 * @date 2020/5/5 21:43
 */
public interface IcveLoginService {

    /**
     * ICVE用户登录
     * @param username 用户名
     * @param password 用户密码
     * @param verifyCode 图形验证码
     * @return
     */
    IcveUser login(String username, String password, String verifyCode);

}
