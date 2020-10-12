package com.autolearn.icve.controller;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.autolearn.icve.config.annotation.GetMappingJson;
import com.autolearn.icve.entity.icve.IcveUserAndId;
import com.autolearn.icve.entity.icve.ResultVO;
import com.autolearn.icve.entity.icve.dto.CourseTaskDTO;
import com.autolearn.icve.entity.thread.ThreadPoolInfo;
import com.autolearn.icve.thread.AutoLearnThreadPool;
import com.autolearn.icve.utils.JWTUtil;
import com.autolearn.icve.utils.SystemInfoUtil;
import com.autolearn.icve.utils.UpdateCourseTaskUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author 胡江斌
 * @version 1.0
 * @title: StatusApi
 * @projectName autolearn
 * @description: TODO
 * @date 2020/8/27 17:41
 */
@Slf4j
@RestController
@RequestMapping("/api")
public class WorkApi {

    @Value("${threadpool.queueCapacity}")
    private Integer queueCapacity;

    @Value("${stackinfo.pwd}")
    private String STACK_INFO_PWD;

    /**
     * 当前通知内容
     */
    private static String msg = "";

    @Autowired
    private AutoLearnThreadPool autoLearnThreadPool;

    /**
     * 用户任务队列，单个用户不可重复创建,value=课程id
     */
    public static ConcurrentHashMap<String, CourseTaskDTO> userQueue = new ConcurrentHashMap();

    @Resource
    private ThreadPoolTaskExecutor threadPoolTaskExecutor;

    /**
     * 开始刷课
     *
     * @param user
     * @return
     */
    @GetMapping("/start")
    public ResultVO<String> start(HttpServletRequest request, IcveUserAndId user) {

        // 子线程共享request
        ServletRequestAttributes sra = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        RequestContextHolder.setRequestAttributes(sra, true);

        String userId = user.getUser().getUserId();

        if (StringUtils.isEmpty(userId)) {
            return ResultVO.fail("请求id为空");
        }

        // 判断是否在队列中
        if (userQueue.containsKey(userId)) {
            return ResultVO.fail("任务已在队列中");
        }

        log.info(user.getUser().getDisplayName() + "开始任务:" + user.getCourseId());

        // 更新
        UpdateCourseTaskUtil.bulider(userId, true)
                .courseId(user.getCourseId())
                .state(CourseTaskDTO.StateEnum.QUEUE)
                .userAccount(user.getUser().getUserName())
                .userName(user.getUser().getDisplayName())
                .put();

        try {
            DecodedJWT token = JWTUtil.require(request.getHeader("token"));
            // cookie存入user
            user.setCookie(token.getClaim("cookie").asString());
            Future<String> result = autoLearnThreadPool.brush(user);
            // 存入Future
            UpdateCourseTaskUtil.bulider(userId).future(result).put();
        } catch (RejectedExecutionException e) {
            log.info("队列已满,拒绝加入队列", e);
            return ResultVO.build(403, "当前队列已满", user.getCourseId());
        }

        return ResultVO.ok("任务创建成功", user.getCourseId());
    }

    /**
     * 取消任务
     *
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
     * 根据用户id查询是否有任务在进行
     *
     * @param id 课程id
     * @return
     */
    @GetMapping("/state/task")
    public CourseTaskDTO taskState(String id) {
        CourseTaskDTO courseTask = userQueue.get(id);
        return courseTask;
    }

    /**
     * 获取系统信息
     *
     * @param request
     * @param pwd
     * @return
     */
    @GetMapping(value = "/server/info")
    public SystemInfoUtil getStackInfo(HttpServletRequest request, String pwd) {
        log.info("获取系统信息");
        if (Objects.equals(pwd, STACK_INFO_PWD)) {
            return SystemInfoUtil.getInstance(request);
        }
        return null;
    }

    /**
     * 线程池信息
     *
     * @return
     */
    @GetMapping("/threadpool/info")
    public ThreadPoolInfo threadInfo() {
        log.info("查看线程池信息");
        ThreadPoolExecutor threadPoolExecutor = threadPoolTaskExecutor.getThreadPoolExecutor();

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
     *
     * @return
     */
    @GetMapping("/queue/info")
    public ConcurrentHashMap<String, CourseTaskDTO> queueInfo() {
        log.info("获取用户队列" + userQueue);
        return userQueue;
    }

    @GetMappingJson("/msg")
    public String msg() {
        log.info("获取公告");
        return msg;
    }

    @GetMappingJson("/set/msg")
    public ResultVO setMsg(String pwd, String m) {
        log.info("设置公告");
        if (Objects.equals(pwd, STACK_INFO_PWD)) {
            return ResultVO.ok(msg = m);
        }
        return ResultVO.fail("密码有误");
    }

    @GetMapping("/show")
    public String show() {
        log.info("测试");
        autoLearnThreadPool.show();
        return "ok";
    }
}
