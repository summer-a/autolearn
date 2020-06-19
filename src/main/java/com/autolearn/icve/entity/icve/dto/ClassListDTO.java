package com.autolearn.icve.entity.icve.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

/**
 * 简略章节列表
 * @author 胡江斌
 * @version 1.0
 * @title: ClassListDTO
 * @projectName autolearn
 * @description: TODO
 * @date 2020/5/9 12:19
 */
@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ClassListDTO {

    /** 状态码 */
    private Integer code;
    /** 课程列表 */
    private List<Course> studyCourseList;

    @Data
    @ToString
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Course{
        private String courseName;
        private String courseOpenId;
        private String openClassId;
    }
}
