package com.autolearn.icve.entity.icve.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.concurrent.Future;

/**
 * 用户课程任务信息
 * @author 胡江斌
 * @version 1.0
 * @title: CourseTaskDTO
 * @projectName autolearn
 * @description: TODO
 * @date 2020/5/14 17:04
 */
@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class  CourseTaskDTO {
    /** 课程id */
    private String courseId;
    /** 用户账户 */
    private String userAccount;
    /** 用户姓名 */
    private String userName;
    /** 任务返回结果 */
    private Future<String> future;
    /** 当前运行状态 */
    private StateEnum state;
    /** 当前刷到的课程 */
    private CellListDTO.CellList course;
    /** 当前课件进度 */
    private Integer percent;
    /** 当前任务hashCode */
    private Integer hashCode;

    /** 课程课件数 */
    private Integer courseCount;

    public enum StateEnum {
        /** 已经开始运行 */
        START,
        /** 课件加时 */
        ADD,
        /** 正在队列中 */
        QUEUE,
        /** 已停止 */
        STOP
    }

}

