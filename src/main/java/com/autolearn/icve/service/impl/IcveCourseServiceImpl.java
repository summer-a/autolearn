package com.autolearn.icve.service.impl;


import com.autolearn.icve.entity.field.UrlFields;
import com.autolearn.icve.entity.icve.*;
import com.autolearn.icve.entity.icve.dto.*;
import com.autolearn.icve.service.IcveCourseService;
import com.autolearn.icve.utils.HttpUtil;
import com.autolearn.icve.utils.VideoUtil;
import com.xiaoleilu.hutool.http.HttpResponse;
import com.xiaoleilu.hutool.json.JSONArray;
import com.xiaoleilu.hutool.json.JSONObject;
import com.xiaoleilu.hutool.thread.GlobalThreadPool;
import com.xiaoleilu.hutool.util.CollectionUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.*;

import static com.autolearn.icve.controller.CourseApi.userQueue;

/**
 * 职教云刷课工具,多例
 *
 * @author 胡江斌
 * @version 1.0
 * @title: IcveCourseUtils
 * @projectName blog
 * @description: TODO
 * @date 2019/12/17 21:00
 */
@Slf4j
@Service
public class IcveCourseServiceImpl implements IcveCourseService {

    /**
     * 获取课程列表
     *
     * @param cookie
     * @return
     */
    @Override
    public CourseListDTO listCourse(String cookie) throws InterruptedException {
        // 睡眠一秒
        sleep(1 * 1000);
        HashMap<String, Object> param = new HashMap<>(2);
        param.put("type", 1);
        return HttpUtil.postBean(UrlFields.ICVE_GET_LEARNNING_COURSE_LIST, cookie, param, CourseListDTO.class);
    }

    /**
     * 标题列表
     *
     * @param cookie
     * @param param
     * @return
     */
    @Override
    public ProcessListDTO listProcess(String cookie, Map<String, Object> param) throws InterruptedException {
        // 睡眠一秒
        sleep(1 * 1000);
        return HttpUtil.postBean(UrlFields.ICVE_GET_PROCESS_LIST, cookie, param, ProcessListDTO.class);
    }

    /**
     * 简略标题列表
     *
     * @param cookie
     * @param param
     * @return
     */
    @Deprecated
    @Override
    public ClassListDTO listClass(String cookie, Map<String, Object> param) throws InterruptedException {
        // 睡眠一秒
        sleep(1 * 1000);
        return null;
    }

    /**
     * 获取子标题列表
     *
     * @param cookie
     * @param param
     * @return
     */
    @Override
    public TopicListDTO listTopic(String cookie, Map<String, Object> param) throws InterruptedException {
        // 睡眠一秒
        sleep(2 * 1000);
        return HttpUtil.postBean(UrlFields.ICVE_GET_TOPIC_BY_MODULE_ID, cookie, param, TopicListDTO.class);
    }

    /**
     * 单元列表
     *
     * @param cookie
     * @param param
     * @return
     */
    @Override
    public CellListDTO listCell(String cookie, Map<String, Object> param) throws InterruptedException {
        // 睡眠一秒
        sleep(2 * 1000);
        return HttpUtil.postBean(UrlFields.ICVE_GET_CELL_BY_TOPIC_ID, cookie, param, CellListDTO.class);
    }

    /**
     * 单个课件信息
     *
     * @param cookie
     * @param param
     * @return
     */
    @Override
    public ViewDirectoryDTO listViewDirectory(String cookie, Map<String, Object> param) throws InterruptedException {
        // 睡眠1秒
        sleep(1 * 1000);

        JSONObject viewJson = HttpUtil.postJson(UrlFields.ICVE_VIEW_DIRECTORY, cookie, param);
        if (Objects.equals(viewJson.getInt("code"), -100)) {

            param.put("moduleId", viewJson.getStr("currModuleId"));
            param.put("cellId", viewJson.getStr("curCellId"));
            param.put("cellName", viewJson.getStr("currCellName"));

            // 睡眠1秒
            sleep(2 * 1000);

            JSONObject changeNodeJson = HttpUtil.postJson(UrlFields.ICVE_CHANGE_STU_STUDY_PROCESS_CELL_DATA, cookie, param);

            if (Objects.equals(changeNodeJson.getInt("code"), 1)) {
                // 休眠半秒
                sleep(1000);
                // 重新获取页面
                viewJson = HttpUtil.postJson(UrlFields.ICVE_VIEW_DIRECTORY, cookie, param);
                return viewJson.toBean(ViewDirectoryDTO.class, true);
            }
        } else if (Objects.equals(viewJson.getInt("code"), 1)) {
            return viewJson.toBean(ViewDirectoryDTO.class, true);
        }
        return null;
    }


    /**
     * 刷课视频
     *
     * @param user          用户信息
     * @param viewDirectory 课件信息
     * @return 0：失败，1：成功，2获取视频时长失败
     */
    @Override
    public int brushVideo(IcveUserAndId user, ViewDirectoryDTO viewDirectory) throws InterruptedException {
        Map<String, Object> form = new HashMap<>(8);

        form.put("picNum", 0);
        form.put("studyNewlyPicNum", 0);

        Double audioVideoLong = viewDirectory.getAudioVideoLong();
        // 获取视频长度失败
        if (audioVideoLong == 0) {
            // 从流获取
            int retryCount = 1;
            for (int i = 0; i < retryCount; i++) {
                double videoLong = VideoUtil.getHttpsVideoLong(viewDirectory.getDownLoadUrl());
                if (videoLong > 0) {
                    audioVideoLong = videoLong / 1000.0;
                    break;
                }
            }
            if (audioVideoLong == 0) {
                return 2;
            }
        }
        // 当前进度
        Double studyNewlyTime = viewDirectory.getStuStudyNewlyTime();

        while (studyNewlyTime < audioVideoLong) {

            // 更新进度
            updatePercent(user.getUser().getUserId(), (int) (studyNewlyTime / audioVideoLong * 100.0));

            double randomDouble = new Random().nextDouble() / 1000.0 + 10.0;
            BigDecimal bigDecimal = new BigDecimal(randomDouble);
            // 每次请求时间需要隔10秒,取后6位
            studyNewlyTime += bigDecimal.setScale(6, RoundingMode.DOWN).stripTrailingZeros().doubleValue();
            // 超过视频时长则设置为视频时长
            studyNewlyTime = studyNewlyTime > audioVideoLong ? audioVideoLong + 1 : studyNewlyTime;

            // 这里10秒一次请求
            form.put("studyNewlyTime", studyNewlyTime);
            // 休眠10秒,如果是最后一段则根据时间休眠
            double sleepTime = (audioVideoLong - studyNewlyTime) < 10 ? ((audioVideoLong - studyNewlyTime) * 1000) : (10 * 1000);
            sleep((long) (sleepTime < 1 ? 1 : sleepTime));

            if (!brush(user, viewDirectory, form)) {
                return 0;
            }
        }
        return 1;
    }

    /**
     * 刷课-office
     *
     * @param user          用户信息
     * @param viewDirectory 课件信息
     * @return
     */
    @Override
    public void brushOffice(IcveUserAndId user, ViewDirectoryDTO viewDirectory) throws InterruptedException {
        Map<String, Object> form = new HashMap<>(8);

        form.put("studyNewlyTime", 0);

        // 页数获取
        Integer pageCount = viewDirectory.getPageCount();

        long begin = System.currentTimeMillis();

        // 开始访问刷课
        // 判断页数，每页访问两秒，文档至少访问12秒
        for (int i = 1; i <= pageCount; ) {

            // 更新进度
            updatePercent(user.getUser().getUserId(), (int) ((double) i / pageCount * 100.0));

            form.put("picNum", i);
            form.put("studyNewlyPicNum", i);
            // 休眠2秒
            sleep(2 * 1000);

            if (!brush(user, viewDirectory, form)) {
                return ;
            }

            // 如果时间小于10秒并且翻页翻完了就等待
            if ((System.currentTimeMillis() - begin) < (10 * 1000) && (i == pageCount)) {
                continue;
            } else {
                i++;
            }
        }
    }

    /**
     * 刷课-图片
     *
     * @param user
     * @param viewDirectory
     */
    @Override
    public void brushImage(IcveUserAndId user, ViewDirectoryDTO viewDirectory) throws InterruptedException {
        Map<String, Object> form = new HashMap<>(8);

        form.put("studyNewlyTime", 0);
        form.put("picNum", 1);
        form.put("studyNewlyPicNum", 1);

        sleep(5 * 1000);

        brush(user, viewDirectory, form);

        // 更新进度
        updatePercent(user.getUser().getUserId(), 100);
    }

    @Override
    public void brushOther(IcveUserAndId user, ViewDirectoryDTO viewDirectory) throws InterruptedException {
        Map<String, Object> form = new HashMap<>(8);

        form.put("studyNewlyTime", 0);
        form.put("picNum", 0);
        form.put("studyNewlyPicNum", 0);

        sleep(5 * 1000);

        brush(user, viewDirectory, form);
    }

    /**
     * 刷课通用部分
     *
     * @param user
     * @param viewDirectory
     * @param formParam
     * @throws InterruptedException
     */
    private boolean brush(IcveUserAndId user, ViewDirectoryDTO viewDirectory, Map<String, Object> formParam) throws InterruptedException {

        String newCookie = brushParam(formParam, user, viewDirectory);

        // 开始访问刷课
        JSONObject resp = HttpUtil.postJson(UrlFields.ICVE_STU_PROCESS_CELL_LOG, newCookie, formParam);
        // 判断进度，根据进度停止运行
        if (resp != null) {
            if (Objects.equals(resp.getInt("code"), 1)) {
                log.debug(viewDirectory.getCategoryName() + ": " + resp.getStr("msg"));
                return true;
            } else {
                log.error(viewDirectory.getCategoryName() + "进度请求失败:" + resp.toString());
            }
        }
        return false;
    }

    /**
     * 获取当前课程信息
     *
     * @param cookie 用户信息
     * @param id     课程id
     * @return
     */
    @Override
    public CourseListDTO.CourseList getCurrentCourse(String cookie, String id) throws InterruptedException {
        // 睡眠1秒
        sleep(1 * 1000);

        CourseListDTO.CourseList nullCourseList = new CourseListDTO.CourseList();

        CourseListDTO courseListDTO = listCourse(cookie);

        if (courseListDTO != null) {
            List<CourseListDTO.CourseList> courseList = courseListDTO.getCourseList();
            if (CollectionUtil.isNotEmpty(courseList)) {
                Optional<CourseListDTO.CourseList> course = courseList.stream().filter(r -> Objects.equals(r.getId(), id)).findFirst();
                return course.orElse(nullCourseList);
            }
        }

        return nullCourseList;
    }

    /**
     * 获取作业列表
     *
     * @param cookie
     * @param unprocessed
     * @return
     */
    @Override
    public HomeworkListDTO listHomework(String cookie, Integer unprocessed) {
        Map<String, Object> param = new HashMap<>(2);
        param.put("unprocessed", unprocessed);
        return HttpUtil.postBean(UrlFields.ICVE_GET_HOMEWORK_LIST, cookie, param, HomeworkListDTO.class);
    }

    /**
     * 获取作业详情
     *
     * @param cookie
     * @param courseOpenId
     * @param openClassId
     * @param homeWorkId
     * @param activityId
     * @param hkTermTimeId
     * @param faceType
     * @return
     */
    @Override
    public HomeworkPreviewDTO getHomework(String cookie, String courseOpenId, String openClassId, String homeWorkId, String activityId, String hkTermTimeId, String faceType) {
        Map<String, Object> param = new HashMap<>(16);
        param.put("courseOpenId", courseOpenId);
        param.put("openClassId", openClassId);
        param.put("homeWorkId", homeWorkId);
        param.put("activityId", activityId);
        param.put("hkTermTimeId", hkTermTimeId);
        param.put("faceType", faceType);

        HomeworkPreviewDTO homeworkPreviewDTO = HttpUtil.postBean(UrlFields.ICVE_GET_HOMEWORK_PREVIEW, cookie, param, HomeworkPreviewDTO.class);

        HomeworkPreviewDTO.Param hParam = homeworkPreviewDTO.getParam();
        // 存入redis操作
        GlobalThreadPool.execute(() -> {
            JSONObject jsonObject = new JSONObject(homeworkPreviewDTO.getRedisData());
            JSONArray questions = jsonObject.getJSONArray("questions");

            Map<String, Object> param2 = new HashMap<>(16);
            String[] ids = new String[questions.size()];
            String[] questionIds = new String[questions.size()];

            param2.put("uniqueId", hParam.getUniqueId());
            param2.put("homeworkId", hParam.getHomeworkId());
            param2.put("courseOpenId", hParam.getCourseOpenId());
            param2.put("termId", hParam.getTermId());
            param2.put("termCode", hParam.getTermCode());
            param2.put("hkResId", hParam.getHkResId());

            for (int i = 0; i < questions.size(); i++) {
                JSONObject obj = questions.getJSONObject(i);
                ids[i] = String.format("%s;%s_%s;%d", obj.getStr("questionId"), hParam.getUniqueId(), i, obj.getInt("totalScore"));
                questionIds[i] = obj.getStr("questionId");
            }
            param2.put("ids", ids);
            param2.put("questionIds", questionIds);

            HttpUtil.postJson(UrlFields.ICVE_ADD_STU_REDIS_RECORD, cookie, param2);

        });

        return homeworkPreviewDTO;
    }

    /**
     * 获取答案
     * @param q
     * @return
     */
    @Override
    public String getAnswer(String q) {
        try {
            // 控制搜索频率
            Thread.sleep(100);
        } catch (InterruptedException e) {
        }
        Map<String, Object> param = new HashMap<>(2);
        param.put("q", q);
        HttpResponse response = HttpUtil.post(UrlFields.ICVE_GET_ANSWER, param);
        return response.body();
    }

    /**
     * 提交作业
     * @param submitWorkPOJO
     */
    @Override
    public JSONObject submitWork(SubmitWorkPOJO submitWorkPOJO) {
        int randomTime = new Random().nextInt(10000);
        Map<String, Object> param = new HashMap<>(16);
        param.put("uniqueId", submitWorkPOJO.getUniqueId());
        param.put("homeworkId", submitWorkPOJO.getHomeworkId());
        param.put("openClassId", submitWorkPOJO.getOpenClassId());
        param.put("homeworkTermTimeId", submitWorkPOJO.getHomeworkTermTimeId());
        param.put("sourceType", 1);
        param.put("isDraft", 0);
        param.put("useTime", randomTime);
        param.put("timestamp", Instant.now().toEpochMilli() + randomTime);
        param.put("data", submitWorkPOJO.getData());

        return HttpUtil.postJson(UrlFields.ICVE_SUBMIT_HOMEWORK, submitWorkPOJO.getCookie(), param);
    }

    /**
     * 通用刷课参数，添加通用的方法
     *
     * @param form          请求参数，只需要填充关键参数
     * @param user          用户信息
     * @param viewDirectory 课件信息
     * @return
     */
    private String brushParam(Map<String, Object> form, IcveUserAndId user, ViewDirectoryDTO viewDirectory) {
        // 补充通用参数
        form.put("courseOpenId", user.getCourseOpenId());
        form.put("openClassId", user.getOpenClassId());
        form.put("cellId", viewDirectory.getCellId());
        form.put("cellLogId", viewDirectory.getCellLogId());
        form.put("token", viewDirectory.getGuIdToken());

        // 添加token到cookie
        return user.getCookie() + ";token=" + user.getUser().getToken();
    }

    /**
     * 更新进度
     *
     * @param userId     用户id
     * @param newPercent 新进度
     */
    private void updatePercent(String userId, Integer newPercent) {
        CourseTaskDTO courseTask = userQueue.get(userId);
        courseTask.setPercent(newPercent);
        userQueue.put(userId, courseTask);
    }

    /**
     * 休眠
     *
     * @param time
     */
    private void sleep(long time) throws InterruptedException {
        Thread.sleep(time);
    }
}
