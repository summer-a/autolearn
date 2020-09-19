package com.autolearn.icve.service;

import com.autolearn.icve.entity.icve.IcveUserAndId;
import com.autolearn.icve.entity.icve.SubmitWorkPOJO;
import com.autolearn.icve.entity.icve.dto.*;
import com.xiaoleilu.hutool.json.JSONObject;

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
    void listCoursePage() throws InterruptedException;

    /**
     * 获取课程列表
     *
     * @return
     */
    CourseListDTO listCourse() throws InterruptedException;

    /**
     * 标题列表
     *
     * @param cookie
     * @param param
     * @return
     */
    ProcessListDTO listProcess(Map<String, Object> param, String cookie) throws InterruptedException;

    /**
     * 获取子标题列表
     *
     * @param cookie
     * @param param
     * @return
     */
    TopicListDTO listTopic(Map<String, Object> param, String cookie) throws InterruptedException;

    /**
     * 单元列表
     *
     * @param cookie
     * @param param
     * @return
     */
    CellListDTO listCell(Map<String, Object> param, String cookie) throws InterruptedException;

    /**
     * 单个课件信息
     *
     * @param cookie
     * @param param
     * @return
     */
    ViewDirectoryDTO listViewDirectory(Map<String, Object> param, String cookie) throws InterruptedException;

    /**
     * 刷课
     *
     * @param user          用户信息
     * @param viewDirectory 课件信息
     * @param brushVideo    是否从视频上次播放位置刷
     * @return
     */
    int brushVideo(IcveUserAndId user, ViewDirectoryDTO viewDirectory, boolean brushVideo) throws InterruptedException;

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
     * 刷课-其他
     *
     * @param user
     * @param viewDirectory
     */
    void brushOther(IcveUserAndId user, ViewDirectoryDTO viewDirectory) throws InterruptedException;

    /**
     * 加时-文档
     *
     * @param user
     * @param viewDirectory
     */
    void overtimeOffice(IcveUserAndId user, ViewDirectoryDTO viewDirectory) throws InterruptedException;

    /**
     * 获取当前课程信息
     *
     * @param id     课程id
     * @return
     */
    CourseListDTO.CourseList getCurrentCourse(String id) throws InterruptedException;

    /**
     * 获取作业列表
     *
     * @param unprocessed
     * @return
     */
    HomeworkListDTO listHomework(Integer unprocessed);

    /**
     * 获取作业详情
     *
     * @param courseOpenId
     * @param openClassId
     * @param homeWorkId
     * @param activityId
     * @param hkTermTimeId
     * @param faceType
     * @return
     */
    HomeworkPreviewDTO getHomework(String courseOpenId, String openClassId, String homeWorkId, String activityId, String hkTermTimeId, String faceType);

    /**
     * 获取答案
     *
     * @param q
     * @return
     */
    String getAnswer(String q);

    /**
     * @param submitWorkPOJO 提交的对象
     */
    JSONObject submitWork(SubmitWorkPOJO submitWorkPOJO);

    /**
     * 获取用户信息
     * @return
     */
    String getUserInfo();
}
