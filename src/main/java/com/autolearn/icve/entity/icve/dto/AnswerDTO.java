package com.autolearn.icve.entity.icve.dto;

import com.xiaoleilu.hutool.json.JSONArray;
import com.xiaoleilu.hutool.util.CollectionUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

/**
 * 网上答案
 *
 * @author h1525
 */
@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class AnswerDTO {
    /**
     * 状态码
     */
    private Integer code;

    /**
     * 答案
     */
    private AnswerData data;

    @Data
    @ToString
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AnswerData {
        private String answer;
        private String question;
    }
}
