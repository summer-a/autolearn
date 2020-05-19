package com.autolearn.icve.service;

import com.autolearn.icve.entity.icve.*;

import java.util.Map;

/**
 * ICVE课程服务
 *
 * @author 胡江斌
 * @version 1.0
 * @title: IcveCourseService
 * @projectName autolearn
 * @description: TODO
 * @date 2020/1/15 17:14
 */
public interface IcveCourseService {

    /**
     * 获取课程列表
     *
     * @return
     */
    CourseListDTO listCourse(String cookie) throws InterruptedException;

    /**
     * 标题列表
     *
     * @param cookie
     * @param param
     * @return
     */
    ProcessListDTO listProcess(String cookie, Map<String, Object> param) throws InterruptedException;

    /**
     * 简略标题列表
     *
     * @param cookie
     * @param param
     * @return
     */
    ClassListDTO listClass(String cookie, Map<String, Object> param) throws InterruptedException;

    /**
     * 获取子标题列表
     *
     * @param cookie
     * @param param
     * @return
     */
    TopicListDTO listTopic(String cookie, Map<String, Object> param) throws InterruptedException;

    /**
     * 单元列表
     *
     * @param cookie
     * @param param
     * @return
     */
    CellListDTO listCell(String cookie, Map<String, Object> param) throws InterruptedException;

    /**
     * 单个课件信息
     *
     * @param cookie
     * @param param
     * @return
     */
    ViewDirectoryDTO listViewDirectory(String cookie, Map<String, Object> param) throws InterruptedException;

    /**
     * 刷课
     *
     * @param user          用户信息
     * @param viewDirectory 课件信息
     * @return
     */
    int brushVideo(IcveUserAndId user, ViewDirectoryDTO viewDirectory) throws InterruptedException;

    /**
     * 刷课-office
     *
     * @param user          用户信息
     * @param viewDirectory 课件信息
     * @return
     */
    void brushOffice(IcveUserAndId user, ViewDirectoryDTO viewDirectory) throws InterruptedException;

    /**
     * 刷课-图片
     *
     * @param user
     * @param viewDirectory
     */
    void brushImage(IcveUserAndId user, ViewDirectoryDTO viewDirectory) throws InterruptedException;

    /**
     * 获取当前课程信息
     *
     * @param cookie cookie
     * @param id     课程id
     * @return
     */
    CourseListDTO.CourseList getCurrentCourse(String cookie, String id) throws InterruptedException;
}
