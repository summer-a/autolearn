package com.autolearn.icve.entity.icve;

import com.xiaoleilu.hutool.json.JSONObject;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;

/**
 * 用户信息
 *
 * @author 胡江斌
 * @version 1.0
 * @title: IcveUser
 * @projectName autolearn
 * @description: TODO
 * @date 2020/1/15 18:36
 */
@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class IcveUser implements Serializable {

    private User user;

    private String cookie;

    public void setUser(String user) {
        JSONObject jsonObject = new JSONObject(user == null ? "{}" : user);
        this.user = jsonObject.toBean(User.class, true);
    }

    @Data
    @ToString
    @NoArgsConstructor
    @AllArgsConstructor
    public static class User implements Serializable {
        private String avator;
        private Integer code;
        private Integer lateCode;
        private String displayName;
        private Integer isEmail;
        private Integer isForceUpdatePwdToSecurity;
        private Integer isGameTea;
        private Integer isInitialPwd;
        private Integer isSecurityLowPwd;
        private Integer isValid;
        private Integer schoolCategory;
        private String schoolId;
        private String schoolLogo;
        private String schoolName;
        private String schoolCode;
        private String schoolUrl;
        private String token;
        private String userId;
        private String userName;
        private String firstUserName;
        private String secondUserName;
        private Integer userType;
        private Integer versionMode;
        private String versionType;
        private Integer mgdCount;
        private Integer isNeedConfirmUserName;
    }
}
