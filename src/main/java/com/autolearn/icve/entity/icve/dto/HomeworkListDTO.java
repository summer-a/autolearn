package com.autolearn.icve.entity.icve.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

/**
 * 作业列表
 * @author 胡江斌
 * @version 1.0
 * @title: HomeworkListTO
 * @projectName autolearn
 * @description: TODO
 * @date 2020/6/11 19:24
 */
@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class HomeworkListDTO {

    private Integer code;
    private Integer unprocessed;
    private List<Course> list;

    @Data
    @ToString
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Course {
        private String courseName;
        private String courseOpenId;
        private List<Homework> homeworkList;
        private String oepnClassId;
        private String openClassCode;
        private String openClassName;
        private Integer openClassType;
    }

    @Data
    @ToString
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Homework {
        private Integer IsDisplayAnswer;
        private String Remark;
        private Integer State;
        private String Title;
        private String courseOpenId;
        private Integer dataState;
        private Integer getScore;
        private String hkTermTimeId;
        private String homeworkId;
        private Integer homeworkType;
        private Integer isLateSubmit;
        private String openClassId;
        private Integer paperType;
        private String replyCount;
        private String stuEndTime;
        private Integer stuHomeworkCount;
        private String stuHomeworkId;
        private String stuStartTime;
        private Integer ztWay;
    }

}
