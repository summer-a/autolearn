package com.autolearn.icve.controller;

import com.autolearn.icve.entity.icve.*;
import com.autolearn.icve.entity.thread.ThreadPoolInfo;
import com.autolearn.icve.service.IcveCourseService;
import com.autolearn.icve.service.IcveLoginService;
import com.autolearn.icve.thread.AutoLearnThreadPool;
import com.autolearn.icve.utils.SystemInfoUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicBoolean;

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
    private IcveLoginService icveLoginService;

    @Autowired
    private IcveCourseService icveCourseService;

    @Autowired
    private AutoLearnThreadPool autoLearnThreadPool;

    @Resource
    private ThreadPoolTaskExecutor threadPoolTaskExecutor;

    @Value("${threadpool.queueCapacity}")
    private Integer queueCapacity;

    @Value("${stackinfo.pwd}")
    private String STACK_INFO_PWD;

    /** 当前通知内容 */
    private static String msg = "";

    /** 设置是否可以登录 */
    private static AtomicBoolean canLogin = new AtomicBoolean(true);

    /**
     * 用户任务队列，单个用户不可重复创建,value=课程id
     */
    public static ConcurrentHashMap<String, CourseTaskDTO> userQueue = new ConcurrentHashMap();

    /**
     * 登录请求
     * @param username
     * @param password
     * @param verifyCode
     * @param verifyCodeCookie
     * @return
     */
    @PostMapping("/login")
    public ResultVO<IcveUser> login(
            String username,
            String password,
            String verifyCode,
            String verifyCodeCookie) {
        // 如果登录被禁用
        if (!canLogin.get()) {
            return ResultVO.fail("当前登录功能暂时被禁用");
        }
        log.info("[ " + username + " ] 登录");
        IcveUser user = icveLoginService.login(username, password, verifyCode, verifyCodeCookie);
        if (user != null && user.getUser() != null) {
            if (Objects.equals(user.getUser().getCode(), 1)) {
                // 登录成功
                return ResultVO.ok("success", user);
            }
        }
        return ResultVO.fail("登录失败,请检查账号密码");
    }

    @GetMapping("/verifyCode")
    public Map<String, String> verifyCode() {
        return icveLoginService.verifyCode();
    }

    /**
     * 课程列表
     * @param user
     * @return
     */
    @GetMapping("/course/list")
    public CourseListDTO getCourseList(IcveUser user) throws InterruptedException {
        // 获取当前用户，取消cookie，在拦截器判断，存入用户组
        log.info("课程列表");
        return icveCourseService.listCourse(user.getCookie());
    }

    /**
     * 开始刷课
     * @param user
     * @return
     */
    @GetMapping("/start")
    public ResultVO<String> start(IcveUserAndId user) {

        String userId = user.getUser().getUserId();

        if (StringUtils.isEmpty(userId)) {
            return ResultVO.fail("请求id为空");
        }

        // 判断是否在队列中
        if (userQueue.containsKey(userId)) {
            return ResultVO.fail("任务已在队列中");
        }

        log.info(user.getUser().getDisplayName() + "开始任务:" + user.getCourseId());

        CourseTaskDTO<String> courseTask = new CourseTaskDTO<>();
        courseTask.setCourseId(user.getCourseId());
        courseTask.setState(CourseTaskDTO.StateEnum.QUEUE);
        courseTask.setUserAccount(user.getUser().getUserName());
        courseTask.setUserName(user.getUser().getDisplayName());

        userQueue.put(userId, courseTask);

        try {
            Future<String> result = autoLearnThreadPool.brush(user);
            // 存入Future
            CourseTaskDTO courseTaskDTO = userQueue.get(userId);
            courseTaskDTO.setFuture(result);
            userQueue.put(userId, courseTaskDTO);
        } catch (RejectedExecutionException e) {
            log.info("队列已满,拒绝加入队列", e);
            return ResultVO.build(403, "当前队列已满", user.getCourseId());
        }

        return ResultVO.ok("任务创建成功", user.getCourseId());
    }

    /**
     * 取消任务
     * @param userId 用户
     * @return
     */
    @GetMapping("/cancel")
    public ResultVO cancel(String userId) {
        CourseTaskDTO courseTask = userQueue.get(userId);
        if (!Objects.equals(courseTask, null)) {
            log.info(userId + "取消课程:" + courseTask.getCourseId());

            userQueue.remove(userId);

            Future future = courseTask.getFuture();
            // 如果已经取消或者完成
            if (future.isCancelled() || future.isDone()) {
                return ResultVO.fail("任务已取消或已完成");
            } else {
                boolean cancelStatus = future.cancel(true);
                return ResultVO.ok("取消成功", cancelStatus);
            }
        }
        return ResultVO.fail("任务不存在");
    }

    /**
     * 获取当前课程进度
     *
     * @param cookie
     * @param id     id
     * @return
     */
    @GetMapping("/course")
    public CourseListDTO.CourseList nowProgress(String cookie, String id) throws InterruptedException {
        log.info("获取课程信息");

        return icveCourseService.getCurrentCourse(cookie, id);
    }

    /**
     * 根据id查询是否有任务在进行
     * @param id
     * @return 课程id
     */
    @GetMapping("/state/task")
    public CourseTaskDTO taskState(String id) {
        log.info("从队列获取状态");
        CourseTaskDTO courseTask = userQueue.get(id);
        return courseTask;
    }

    /**
     * 线程池信息
     * @return
     */
    @GetMapping("/threadpool/info")
    public ThreadPoolInfo threadInfo() {
        log.info("查看线程池信息");
        ThreadPoolExecutor threadPoolExecutor = threadPoolTaskExecutor.getThreadPoolExecutor();
        //返回计划执行的任务总数。
//        log.info("taskCount：" + threadPoolExecutor.getTaskCount());
        //返回正在主动执行任务的线程的大概数量。
//        log.info("activeCount：" + threadPoolExecutor.getActiveCount());
        //返回池中的当前线程数。
//        log.info("poolSize：" + threadPoolExecutor.getPoolSize());
        //返回线程的核心数量。
//        log.info("corePoolSize：" + threadPoolExecutor.getCorePoolSize());
        //返回池中曾经同时存在的最大线程数。
//        log.info("largestPoolSize：" + threadPoolExecutor.getLargestPoolSize());
        //返回允许的最大线程数。
//        log.info("maximumPoolSize：" + threadPoolExecutor.getMaximumPoolSize());

        ThreadPoolInfo threadPoolInfo = new ThreadPoolInfo();
        threadPoolInfo.setCorePoolSize(threadPoolExecutor.getCorePoolSize());
        threadPoolInfo.setMaximumPoolSize(threadPoolExecutor.getMaximumPoolSize());
        threadPoolInfo.setWorkThread(threadPoolExecutor.getActiveCount());
        threadPoolInfo.setCompletedTaskCount(threadPoolExecutor.getCompletedTaskCount());
        threadPoolInfo.setWorkQueue(threadPoolExecutor.getQueue().size());
        threadPoolInfo.setMaximunQueueSize(queueCapacity);

        return threadPoolInfo;
    }

    /**
     * 用户队列信息
     * @return
     */
    @GetMapping("/queue/info")
    public ConcurrentHashMap<String, CourseTaskDTO> queueInfo() {
        log.info("获取用户队列" + userQueue);
        return userQueue;
    }

    @GetMapping(value = "/msg", produces = {MediaType.APPLICATION_JSON_UTF8_VALUE})
    public String msg() {
        log.info("获取公告");
        return msg;
    }

    @GetMapping(value = "/set/msg", produces = {MediaType.APPLICATION_JSON_UTF8_VALUE})
    public ResultVO setMsg(String m) {
        log.info("设置公告");
        return ResultVO.ok(msg = m);
    }

    @GetMapping(value = "/login/open")
    public ResultVO openLogin() {
        log.info("允许登录");
        canLogin.set(true);
        return ResultVO.ok();
    }

    @GetMapping(value = "/login/close")
    public ResultVO closeLogin() {
        log.info("禁止登陆");
        canLogin.set(false);
        return ResultVO.ok();
    }

    @GetMapping(value = "/stack/info")
    public SystemInfoUtil getStackInfo(HttpServletRequest request, String pwd) {
        log.info("获取堆栈信息");
        if (Objects.equals(pwd, STACK_INFO_PWD)) {
            return SystemInfoUtil.getInstance(request);
        }
        return null;
    }


    @GetMapping("/show")
    public String show() {
        log.info("测试");
        autoLearnThreadPool.show();
        return "ok";
    }

}