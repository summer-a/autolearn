package com.autolearn.icve.entity.icve.dto;

import com.xiaoleilu.hutool.json.JSONArray;
import com.xiaoleilu.hutool.json.JSONObject;
import com.xiaoleilu.hutool.util.CollectionUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

/**
 * 单元/组列表
 *
 * @author h1525
 */
@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class HomeworkPreviewDTO {
    private Integer code;
    private Homework homework;
    private Param param;
    private String questionData;
    private String redisData;
    private Signature signature;

    private String msg;

    @Data
    @ToString
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Homework {
        private String Id;
        private Integer LimitTime;
        private String Remark;
        private String Title;
        private Integer ZtWay;
    }

    @Data
    @ToString
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Param {
        private String activityId;
        private String courseOpenId;
        private String faceType;
        private String fileSystemUrl;
        private String hkResId;
        private String hkTermTimeId;
        private String homeworkId;
        private String openClassId;
        private String paperStructUnique;
        private String stuEndDate;
        private String termCode;
        private String termId;
        private String uniqueId;
    }

    @Data
    @ToString
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Signature {
        private String accessid;
        private String callback;
        private String dir;
        private String expire;
        private String host;
        private String identity;
        private String policy;
        private String random;
        private String signature;
        private String spaceName;
    }
}
