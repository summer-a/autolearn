package com.autolearn.icve.service.impl;


import com.autolearn.icve.entity.field.UrlFields;
import com.autolearn.icve.entity.icve.IcveUserAndId;
import com.autolearn.icve.entity.icve.SubmitWorkPOJO;
import com.autolearn.icve.entity.icve.dto.*;
import com.autolearn.icve.service.IcveCourseService;
import com.autolearn.icve.utils.HttpUtil;
import com.autolearn.icve.utils.SpringUtil;
import com.autolearn.icve.utils.UpdateCourseTaskUtil;
import com.autolearn.icve.utils.VideoUtil;
import com.xiaoleilu.hutool.http.HttpResponse;
import com.xiaoleilu.hutool.json.JSONArray;
import com.xiaoleilu.hutool.json.JSONObject;
import com.xiaoleilu.hutool.util.CollectionUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.*;

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
     * @return
     */
    @Override
    public void listCoursePage() {
        HttpUtil.get(UrlFields.ICVE_GET_COURSE_LIST_PAGE, SpringUtil.getCurrentToken());
    }

    /**
     * 获取课程列表
     *
     * @return
     */
    @Override
    public CourseListDTO listCourse() throws InterruptedException {
        // 睡眠一秒
        sleep(1 * 1000);
        HashMap<String, Object> param = new HashMap<>(2);
        param.put("type", 1);
        return HttpUtil.postBean(UrlFields.ICVE_GET_LEARNNING_COURSE_LIST, param, CourseListDTO.class);
    }

    /**
     * 标题列表
     *
     * @param param
     * @return
     */
    @Override
    public ProcessListDTO listProcess(Map<String, Object> param, String cookie) throws InterruptedException {
        // 睡眠一秒
        sleep(1 * 1000);
        return HttpUtil.postBean(UrlFields.ICVE_GET_PROCESS_LIST, param, cookie, ProcessListDTO.class);
    }

    /**
     * 获取子标题列表
     *
     * @param param
     * @return
     */
    @Override
    public TopicListDTO listTopic(Map<String, Object> param, String cookie) throws InterruptedException {
        // 睡眠一秒
        sleep(2 * 1000);
        return HttpUtil.postBean(UrlFields.ICVE_GET_TOPIC_BY_MODULE_ID, param, cookie, TopicListDTO.class);
    }

    /**
     * 单元列表
     *
     * @param param
     * @return
     */
    @Override
    public CellListDTO listCell(Map<String, Object> param, String cookie) throws InterruptedException {
        // 睡眠一秒
        sleep(2 * 1000);
        return HttpUtil.postBean(UrlFields.ICVE_GET_CELL_BY_TOPIC_ID, param, cookie, CellListDTO.class);
    }

    /**
     * 单个课件信息
     *
     * @param param
     * @return
     */
    @Override
    public ViewDirectoryDTO listViewDirectory(Map<String, Object> param, String cookie) throws InterruptedException {
        // 睡眠1秒
        sleep(1 * 1000);

        ViewDirectoryDTO result = getCourseInfo(param, cookie);

        String categoryName = result.getCategoryName();
        if (Objects.nonNull(result) && Objects.nonNull(categoryName) && !categoryName.contains("视频") && !categoryName.contains("音频")) {
            // 判断是否需要更新课件
            Integer pageCount = result.getPageCount();
            Integer cellPercent = result.getCellPercent();
            // 页数0则更新,没刷完的也更新一遍
            boolean needUpdate = Objects.equals(pageCount, 0) || (cellPercent > 0 && cellPercent < 100);
            if (needUpdate) {
                Map<String, Object> updateParam = new HashMap<>(16);
                param.put("cellId", result.getCellId());
                JSONObject resUrl = new JSONObject(Objects.isNull(result.getResUrl()) ? "{}" : result.getResUrl());
                Integer pCount = null;
                try {
                    pCount = resUrl.getJSONObject("args").getInt("page_count");
                    if (Objects.isNull(pCount)) {
                        throw new NullPointerException("获取页数失败");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                param.put("cellData", "zjy,0," + pCount);
                JSONObject jsonObject = HttpUtil.postJson(UrlFields.ICVE_UPDATE_CELL_DATA, updateParam, cookie);
                log.info(jsonObject.toStringPretty());
                result = getCourseInfo(param, cookie);
            }
        }

        return result;
    }


    /**
     * 刷课视频
     *
     * @param user          用户信息
     * @param viewDirectory 课件信息
     * @param restart       是否从视频上次播放位置刷
     * @return 0：失败，1：成功，2获取视频时长失败
     */
    @Override
    public int brushVideo(IcveUserAndId user, ViewDirectoryDTO viewDirectory, boolean restart) throws InterruptedException {
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

        if (!restart) {
            studyNewlyTime = 0D;
        }

        while (studyNewlyTime < audioVideoLong) {

            double randomDouble = new Random().nextDouble() / 1000.0 + 10.0;
            BigDecimal bigDecimal = new BigDecimal(randomDouble);
            // 每次请求时间需要隔10秒,取后6位
            studyNewlyTime += bigDecimal.setScale(6, RoundingMode.DOWN).stripTrailingZeros().doubleValue();
            // 超过视频时长则设置为视频时长
            studyNewlyTime = studyNewlyTime + randomDouble > audioVideoLong ? audioVideoLong + 1 : studyNewlyTime;

            // 这里10秒一次请求
            form.put("studyNewlyTime", studyNewlyTime);
            // 休眠10秒,如果是最后一段则根据时间休眠
            double sleepTime = (audioVideoLong - studyNewlyTime) < 10 ? ((audioVideoLong - studyNewlyTime) * 1000) : (10 * 1000);
            sleep((long) (sleepTime < 2000 ? 2000 : sleepTime));

            if (!brush(user, viewDirectory, form)) {
                return 0;
            }

            // 更新进度
            UpdateCourseTaskUtil.bulider(user.getUser().getUserId()).percent((int) (studyNewlyTime / audioVideoLong * 100.0)).put();
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

        // 开始访问刷课
        int i = 1;
        boolean isOver = false;
        while (!isOver) {
            if (i >= pageCount) {
                i = pageCount;
                isOver = true;
            }
            form.put("picNum", i);
            form.put("studyNewlyPicNum", i);
            // 休眠
            sleep(10 * 1000);

            if (!brush(user, viewDirectory, form)) {
                return;
            }

            // 更新进度
            UpdateCourseTaskUtil.bulider(user.getUser().getUserId()).percent((int) ((double) i / pageCount * 100.0)).put();
            i += 10;
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
        UpdateCourseTaskUtil.bulider(user.getUser().getUserId()).percent(100).put();
    }

    @Override
    public void brushOther(IcveUserAndId user, ViewDirectoryDTO viewDirectory) throws InterruptedException {
        Map<String, Object> form = new HashMap<>(8);

        form.put("studyNewlyTime", 0);
        form.put("picNum", 0);
        form.put("studyNewlyPicNum", 0);

        sleep(5 * 1000);

        brush(user, viewDirectory, form);

        // 更新进度
        UpdateCourseTaskUtil.bulider(user.getUser().getUserId()).percent(100).put();
    }

    /**
     * 加时-文档
     *
     * @param user
     * @param viewDirectory
     */
    @Override
    public void overtimeOffice(IcveUserAndId user, ViewDirectoryDTO viewDirectory) throws InterruptedException {
        Map<String, Object> form = new HashMap<>(8);

        form.put("studyNewlyTime", 0);

        // 页数获取
        Integer pageCount = viewDirectory.getPageCount();

        long begin = System.currentTimeMillis();

        form.put("picNum", pageCount);
        form.put("studyNewlyPicNum", pageCount);
        do {
            // 休眠3秒
            sleep(7 * 1000);

            if (!brush(user, viewDirectory, form)) {
                return;
            }
        } while ((System.currentTimeMillis() - begin) < (10 * 60 * 60 * 1000));
        // 默认加时10小时
    }

    /**
     * 获取当前课程信息
     * 用户信息
     *
     * @param id 课程id
     * @return
     */
    @Override
    public CourseListDTO.CourseList getCurrentCourse(String id) throws InterruptedException {
        // 睡眠1秒
        sleep(1 * 1000);

        CourseListDTO.CourseList nullCourseList = new CourseListDTO.CourseList();

        CourseListDTO courseListDTO = listCourse();

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
     * @param unprocessed
     * @return
     */
    @Override
    public HomeworkListDTO listHomework(Integer unprocessed) {
        Map<String, Object> param = new HashMap<>(2);
        param.put("unprocessed", unprocessed);
        return HttpUtil.postBean(UrlFields.ICVE_GET_HOMEWORK_LIST, param, HomeworkListDTO.class);
    }

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
    @Override
    public HomeworkPreviewDTO getHomework(String courseOpenId, String openClassId, String homeWorkId, String activityId, String hkTermTimeId, String faceType) {
        Map<String, Object> param = new HashMap<>(16);
        param.put("courseOpenId", courseOpenId);
        param.put("openClassId", openClassId);
        param.put("homeWorkId", homeWorkId);
        param.put("activityId", activityId);
        param.put("hkTermTimeId", hkTermTimeId);
        param.put("faceType", faceType);

        HomeworkPreviewDTO homeworkPreviewDTO = HttpUtil.postBean(UrlFields.ICVE_GET_HOMEWORK_PREVIEW, param, HomeworkPreviewDTO.class);

        HomeworkPreviewDTO.Param hParam = homeworkPreviewDTO.getParam();
        // 存入redis操作
        try {
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

            JSONObject result = HttpUtil.postJson(UrlFields.ICVE_ADD_STU_REDIS_RECORD, param2, null);
            Integer code = result.getInt("code");
            if (null == code || code != 1) {
                throw new Exception();
            }
        } catch (Exception e) {
            log.error("作业存储redis异常", e);
            HomeworkPreviewDTO hp = new HomeworkPreviewDTO();
            hp.setCode(500);
            hp.setMsg("获取失败，请刷新重试");
            return hp;
        }

        return homeworkPreviewDTO;
    }

    @Override
    public String getUserInfo() {
        HttpResponse post = HttpUtil.post(UrlFields.ICVE_GET_USER_INFO);
        return post.body();
    }

    /**
     * 获取答案
     *
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
     *
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

        return HttpUtil.postJson(UrlFields.ICVE_SUBMIT_HOMEWORK, param, null);
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
        JSONObject resp = HttpUtil.postJson(UrlFields.ICVE_STU_PROCESS_CELL_LOG, formParam, newCookie);
        // 判断进度，根据进度停止运行
        if (resp != null) {
            if (Objects.equals(resp.getInt("code"), 1)) {
                log.debug(viewDirectory.getCategoryName() + ": " + resp.getStr("msg"));
                return true;
            } else {
                log.info("cookie:" + newCookie);
                log.info(user.toString());
                log.info(viewDirectory.toString());
                log.info(formParam.toString());
                log.error(viewDirectory.getCategoryName() + "进度请求失败:" + resp.toString());
            }
        }
        return false;
    }

    /**
     * 获取课程信息
     *
     * @param param
     * @param cookie
     * @throws InterruptedException
     */
    private ViewDirectoryDTO getCourseInfo(Map<String, Object> param, String cookie) throws InterruptedException {
        ViewDirectoryDTO result = null;

        JSONObject viewJson = HttpUtil.postJson(UrlFields.ICVE_VIEW_DIRECTORY, param, cookie);
        if (Objects.equals(viewJson.getInt("code"), -100)) {

            param.put("moduleId", viewJson.getStr("currModuleId"));
            param.put("cellId", viewJson.getStr("curCellId"));
            param.put("cellName", viewJson.getStr("currCellName"));

            // 睡眠2秒
            sleep(2 * 1000);

            JSONObject changeNodeJson = HttpUtil.postJson(UrlFields.ICVE_CHANGE_STU_STUDY_PROCESS_CELL_DATA, param, cookie);

            if (Objects.equals(changeNodeJson.getInt("code"), 1)) {
                // 休眠半秒
                sleep(1000);
                // 重新获取页面
                viewJson = HttpUtil.postJson(UrlFields.ICVE_VIEW_DIRECTORY, param, cookie);
                result = viewJson.toBean(ViewDirectoryDTO.class, true);
            }
        } else if (Objects.equals(viewJson.getInt("code"), 1)) {
            result = viewJson.toBean(ViewDirectoryDTO.class, true);
        }
        return result;
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
     * 休眠
     *
     * @param time
     */
    private void sleep(long time) throws InterruptedException {
        Thread.sleep(time);
    }
}
