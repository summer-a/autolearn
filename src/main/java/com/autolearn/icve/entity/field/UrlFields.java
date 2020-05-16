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
//    public static final String ICVE_LOGIN_REQUEST = "https://zjy2.icve.com.cn/dzx/portalApi/portallogin/login";
    /** 登录url */
    public static final String ICVE_LOGIN_REQUEST = "https://zjy2.icve.com.cn/dzx/portalApi/portallogin/login";
    /** 新版本登录url,也用于获取用户信息 */
    public static final String ICVE_GET_USER_INFO = "https://zjy2.icve.com.cn/student/Studio/index";
    /** 验证码url */
    public static final String ICVE_VERIFY_CODE = "https://www.icve.com.cn/portal/VerifyCode/index";
    /** 获取课程列表 */
    public static final String ICVE_GET_LEARNNING_COURSE_LIST = "https://zjy2.icve.com.cn/student/learning/getLearnningCourseList";
    /** 获取处理列表 */
    public static final String ICVE_GET_PROCESS_LIST = "https://zjy2.icve.com.cn/api/study/process/getProcessList";
    /** 获取学生班级信息 */
    public static final String ICVE_GET_STU_STUDY_CLASS_LIST = "https://zjy2.icve.com.cn/common/courseLoad/getStuStudyClassList";
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

    private UrlFields() {

    }
}
