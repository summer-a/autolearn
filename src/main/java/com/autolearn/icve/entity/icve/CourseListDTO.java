package com.autolearn.icve.entity.icve;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

/**
 * 课程信息
 *
 * @author 胡江斌
 */
@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class CourseListDTO {

    private Integer code;
    private List<CourseList> courseList;
    private String termId;
    private List<TermList> termList;
    private String type;

    @Data
    @ToString
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CourseList {
        private String Id;
        private String assistTeacherName;
        private Integer checkStatus;
        private String courseCode;
        private String courseName;
        private String courseOpenId;
        private Integer courseSystemType;
        private String openClassId;
        private Integer openClassState;
        private Integer openClassType;
        private Integer process;
        private String termName;
        private String thumbnail;
        private Integer totalScore;
    }

    @Data
    @ToString
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TermList {
        private String CreatorId;
        private String DateCreated;
        private String Id;
        private Boolean IsCurTerm;
        private Integer IsSynchroUpdate;
        private String TableName;
        private String TermCode;
        private String TermName;
        private Integer TermSeason;
    }
}

