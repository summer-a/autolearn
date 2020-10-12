package com.autolearn.icve.entity.field;

/**
 * 常用链接
 *
 * @author 胡江斌
 * @version 1.0
 * @title: UrlFields
 * @projectName blog
 * @description: TODO
 * @date 2019/12/16 18:24
 */
public final class UrlFields {

    /**
     * 职教云(ICVE)链接
     */
    /** 登录url */
    public static final String ICVE_LOGIN_REQUEST = "https://zjy2.icve.com.cn/api/common/login/login";
    /** 验证码url */
    public static final String ICVE_VERIFY_CODE = "https://zjy2.icve.com.cn/api/common/VerifyCode/index";
    /** 获取课程列表 */
    public static final String ICVE_GET_LEARNNING_COURSE_LIST = "https://zjy2.icve.com.cn/student/learning/getLearnningCourseList";
    /** 课程列表页面 */
    public static final String ICVE_GET_COURSE_LIST_PAGE = "https://zjy2.icve.com.cn/student/learning/courseList.html?type=1";
    /** 获取处理列表 */
    public static final String ICVE_GET_PROCESS_LIST = "https://zjy2.icve.com.cn/api/study/process/getProcessList";
    /** 获取作业列表 */
    public static final String ICVE_GET_HOMEWORK_LIST = "https://zjy2.icve.com.cn/api/student/myHomework/getMyHomeworkList";
    /** 获取作业详情 */
    public static final String ICVE_GET_HOMEWORK_PREVIEW = "https://security.zjy2.icve.com.cn/api/study/homework/preview";
    /** 获取主题 */
    public static final String ICVE_GET_TOPIC_BY_MODULE_ID = "https://zjy2.icve.com.cn/api/study/process/getTopicByModuleId";
    /** 根据主题id获取单元 */
    public static final String ICVE_GET_CELL_BY_TOPIC_ID = "https://zjy2.icve.com.cn/api/study/process/getCellByTopicId";
    /** 获取视图夹 */
    public static final String ICVE_VIEW_DIRECTORY = "https://zjy2.icve.com.cn/api/common/Directory/viewDirectory";
    /** 学生处理单元日志 */
    public static final String ICVE_STU_PROCESS_CELL_LOG = "https://zjy2.icve.com.cn/api/common/Directory/stuProcessCellLog";
    /** 更改不同进度课件 */
    public static final String ICVE_CHANGE_STU_STUDY_PROCESS_CELL_DATA = "https://zjy2.icve.com.cn/api/common/Directory/changeStuStudyProcessCellData";
    /** 获取用户信息 */
    public static final String ICVE_GET_USER_INFO = "https://zjy2.icve.com.cn/api/student/stuInfo/getStuInfo";
    /**
     * 更新课件
     */
    public static final String ICVE_UPDATE_CELL_DATA = "https://zjy2.icve.com.cn/api/common/Directory/updateCellData";



    /** 添加redis 学生记录  */
    public static final String ICVE_ADD_STU_REDIS_RECORD = "https://security.zjy2.icve.com.cn/api/study/homework/addStuRedisRecord";
    /** 获取答案 */
    public static final String ICVE_GET_ANSWER = "http://p.52dw.net:81/chati";
    /** 提交作业 */
    public static final String ICVE_SUBMIT_HOMEWORK = "https://security.zjy2.icve.com.cn/api/study/homework/newStuSubmitHomework";

    private UrlFields() {

    }
}
