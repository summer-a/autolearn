package com.autolearn.icve.utils;

import com.autolearn.icve.entity.icve.dto.CellListDTO;
import com.autolearn.icve.entity.icve.dto.CourseTaskDTO;

import java.util.concurrent.Future;

import static com.autolearn.icve.controller.CourseApi.userQueue;

/**
 * 修改CourseTaskDTO队列专用工具
 *
 * @author 胡江斌
 * @version 1.0
 * @title: UpdateCourseTaskUtil
 * @projectName autolearn
 * @description: TODO
 * @date 2020/6/26 12:02
 */
public class UpdateCourseTaskUtil<T> {

    /**
     * 用户id
     */
    private String userId;
    /**
     * 必要参数传入
     */
    private CourseTaskDTO courseTask;

    public UpdateCourseTaskUtil(String userId) {
        this(userId, false);
    }

    /**
     *
     * @param userId 用户id
     * @param newObject 是否直接创建个新对象而不需要从队列中获取
     */
    public UpdateCourseTaskUtil(String userId, boolean newObject) {

        if (null == userId) {
            throw new NullPointerException("用户ID为空");
        }

        this.userId = userId;

        if (newObject) {
            this.courseTask = new CourseTaskDTO();
        } else {
            CourseTaskDTO courseTaskDTO = userQueue.get(userId);
            if (null == courseTaskDTO) {
                this.courseTask =  new CourseTaskDTO();
            } else {
                this.courseTask = courseTaskDTO;
            }
        }
    }

    public UpdateCourseTaskUtil courseId(String courseId) {
        courseTask.setCourseId(courseId);
        return this;
    }

    public UpdateCourseTaskUtil userAccount(String userAccount) {
        courseTask.setUserAccount(userAccount);
        return this;
    }

    public UpdateCourseTaskUtil userName(String userName) {
        courseTask.setUserName(userName);
        return this;
    }

    public UpdateCourseTaskUtil future(Future<T> future) {
        courseTask.setFuture(future);
        return this;
    }

    public UpdateCourseTaskUtil state(CourseTaskDTO.StateEnum state) {
        courseTask.setState(state);
        return this;
    }

    public UpdateCourseTaskUtil course(CellListDTO.CellList course) {
        courseTask.setCourse(course);
        return this;
    }

    public UpdateCourseTaskUtil percent(Integer percent) {
        courseTask.setPercent(percent);
        return this;
    }

    public UpdateCourseTaskUtil hashCode(Integer hashCode) {
        courseTask.setHashCode(hashCode);
        return this;
    }

    public UpdateCourseTaskUtil courseCount(Integer courseCount) {
        courseTask.setCourseCount(courseCount);
        return this;
    }

    public void put() {
        userQueue.put(userId, courseTask);
    }
}
