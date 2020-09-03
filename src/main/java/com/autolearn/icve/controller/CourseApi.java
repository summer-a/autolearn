package com.autolearn.icve.controller;

import com.autolearn.icve.entity.icve.IcveUser;
import com.autolearn.icve.entity.icve.SubmitWorkPOJO;
import com.autolearn.icve.entity.icve.dto.CourseListDTO;
import com.autolearn.icve.entity.icve.dto.HomeworkListDTO;
import com.autolearn.icve.entity.icve.dto.HomeworkPreviewDTO;
import com.autolearn.icve.service.IcveCourseService;
import com.autolearn.icve.utils.PayUtils;
import com.xiaoleilu.hutool.json.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 刷课api
 *
 * @author 胡江斌
 * @version 1.0
 * @title: CourseApi
 * @projectName autolearn
 * @description: TODO
 * @date 2020/1/15 16:14
 */
@Slf4j
@RestController
@RequestMapping("/api")
public class CourseApi {

    @Autowired
    private IcveCourseService icveCourseService;

    /**
     * 课程列表
     *
     * @return
     */
    @CrossOrigin
    @GetMapping("/course/list")
    public CourseListDTO getCourseList(IcveUser user) throws InterruptedException {
        // 获取当前用户，取消cookie，在拦截器判断，存入用户组
        if (null != user.getForceRefresh() && user.getForceRefresh()) {
            icveCourseService.listCoursePage();
        }
        return icveCourseService.listCourse();
    }

    /**
     * 获取作业列表
     *
     * @param unprocessed
     * @return
     */
    @GetMapping("/listWork")
    public HomeworkListDTO listWork(Integer unprocessed) {
        log.info("作业列表");
        return icveCourseService.listHomework(unprocessed);
    }

    /**
     * 获取作业
     *
     * @param courseOpenId
     * @param openClassId
     * @param homeWorkId
     * @param activityId
     * @param hkTermTimeId
     * @param faceType
     * @return
     */
    @GetMapping("/getWork")
    public HomeworkPreviewDTO getWork(String courseOpenId,
                                      String openClassId,
                                      String homeWorkId,
                                      String activityId,
                                      String hkTermTimeId,
                                      String faceType) {
        log.info("获取作业");
        // 获取作业
        return icveCourseService.getHomework(courseOpenId, openClassId, homeWorkId, activityId, hkTermTimeId, faceType);
    }

    /**
     * 提交答案
     *
     * @param submitObj
     * @return
     */
    @PostMapping("/submitWork")
    public boolean submitWork(@RequestBody SubmitWorkPOJO submitObj) {
        log.info("提交作业");
        JSONObject jsonObject = icveCourseService.submitWork(submitObj);
        if (jsonObject.getInt("code") == 1) {
            return true;
        }
        return false;
    }

    /**
     * 获取当前课程进度
     *
     * @param id id
     * @return
     */
    @GetMapping("/course")
    public CourseListDTO.CourseList nowProgress(String id) throws InterruptedException {
        return icveCourseService.getCurrentCourse(id);
    }

    /**
     * 获取答案
     *
     * @param q
     * @return
     */
    @GetMapping("/answer")
    public String getAnswer(String q) {
        log.info("获取答案");
        return icveCourseService.getAnswer(q);
    }

    @GetMapping("/user/info")
    public String userInfo() {
        log.info("获取用户信息");
        return icveCourseService.getUserInfo();
    }

    @GetMapping("/pay")
    public String submitPay() {
        return new PayUtils().pay("测试", 0.1, 1, "", "", null);
    }

}