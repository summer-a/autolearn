package com.autolearn.icve.entity.icve;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

/**
 * 标题列表
 * @author h1525
 */
@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ProcessListDTO {
    /** 状态码 */
    private Integer code;
    /** 课程id */
    private String courseOpenId;
    /** 班级id */
    private String openClassId;
    /** 课件总数 */
    private Integer openCourseCellCount;
    /** 当前章节下课件数 */
    private Integer stuStudyCourseOpenCellCount;
    /** 章节信息 */
    private Progress progress;

    @Data
    @ToString
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Progress {
        private String moduleId;
        private List<ModuleList> moduleList;
    }

    @Data
    @ToString
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ModuleList {
        private String id;
        private String name;
        private Integer sortOrder;
        private Integer percent;
    }

}
