package com.autolearn.icve.entity.icve;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

/**
 * 子标题列表
 *
 * @author h1525
 */
@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class TopicListDTO {
    /**
     * 状态码
     */
    private Integer code;
    /**
     *
     */
    private List<TopicList> topicList;

    @Data
    @ToString
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TopicList {
        private String id;
        private String name;
        private String sortOrder;
        private String upTopicId;
    }

}
