package com.autolearn.icve.thread;

import com.autolearn.icve.entity.icve.IcveUser;
import com.autolearn.icve.entity.icve.IcveUserAndId;
import com.autolearn.icve.entity.icve.dto.*;
import com.autolearn.icve.service.IcveCourseService;
import com.autolearn.icve.utils.UpdateCourseTaskUtil;
import com.xiaoleilu.hutool.util.CollectionUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.Future;

import static com.autolearn.icve.controller.WorkApi.userQueue;

/**
 * @author 胡江斌
 * @version 1.0
 * @title: AutoLearnThreadPool
 * @projectName autolearn
 * @description: TODO
 * @date 2020/5/12 22:28
 */
@Component
@Slf4j
public class AutoLearnThreadPool {

    @Autowired
    private IcveCourseService icveCourseService;

    /**
     * 显示当前用户名和账户
     */
    private String displayName;

    @Async
    public Future show() {
        try {
            printLog("0");
            Thread.sleep(10 * 1000);
            printLog("10");
            Thread.sleep(10 * 1000);
            printLog("20");
            Thread.sleep(10 * 1000);
            printLog("30");
        } catch (InterruptedException e) {
            e.printStackTrace();
            log.error("睡眠取消", e);
            return new AsyncResult("fail");
        }
        return new AsyncResult("ok");
    }

    /**
     * 刷课主方法
     *
     * @param user
     * @return
     */
    @Async
    public Future<String> brush(IcveUserAndId user) {

        IcveUser.User userInfo = user.getUser();

        UpdateCourseTaskUtil.bulider(userInfo.getUserId())
                .state(null == user.getOverTime() || !user.getOverTime() ? CourseTaskDTO.StateEnum.START : CourseTaskDTO.StateEnum.ADD)
                .put();

        this.displayName = userInfo.getUserName() + "|" + userInfo.getDisplayName();

        printLog("开始刷课");

        // 整个课程模块
        Map<String, Object> formMap = initParam(user);

        // 子列表参数
        Map<String, Object> formModelMap = initParam(user);

        // 单元列表参数
        Map<String, Object> formTopicMap = initParam(user);

        // 节点列表参数
        Map<String, Object> formCellMap = initParam(user);
        formCellMap.put("flag", "s");

        try {
            ProcessListDTO processList = icveCourseService.listProcess(formMap, user.getCookie());

            if (processList != null && Objects.equals(processList.getCode(), 1)) {

                Integer courseCellCount = processList.getOpenCourseCellCount();

                // 更新课件数
                UpdateCourseTaskUtil.bulider(userInfo.getUserId()).courseCount(courseCellCount).put();

                printLog("课件数:" + courseCellCount);

                List<ProcessListDTO.ModuleList> moduleList = processList.getProgress().getModuleList();
                if (CollectionUtil.isEmpty(moduleList)) {
                    return new AsyncResult<>("无课件");
                }
                for (ProcessListDTO.ModuleList module : moduleList) {

                    // 当前模块名
                    printLog(module.getName());

                    if (Objects.equals(module.getPercent(), 100) && !user.getOverTime()) {
                        printLog("该模组已完成");
                        continue;
                    }

                    formModelMap.put("moduleId", module.getId());

                    // 子列表
                    TopicListDTO listTopic = icveCourseService.listTopic(formModelMap, user.getCookie());
                    if (listTopic != null && Objects.equals(listTopic.getCode(), 1)) {
                        List<TopicListDTO.TopicList> topics = listTopic.getTopicList();

                        if (CollectionUtil.isEmpty(topics)) {
                            continue;
                        }

                        for (TopicListDTO.TopicList topic : topics) {
                            printLog("----" + topic.getName());

                            formTopicMap.put("topicId", topic.getId());

                            CellListDTO cells = icveCourseService.listCell(formTopicMap, user.getCookie());

                            if (cells != null && Objects.equals(cells.getCode(), 1)) {

                                List<CellListDTO.CellList> cellList = cells.getCellList();

                                // 判断是否有子节点/或者说判断该节点是否为组
                                if (CollectionUtil.isNotEmpty(cellList)) {
                                    for (CellListDTO.CellList cell : cellList) {
                                        // 当前单元名
                                        printLog("--------" + cell.getCellName());

                                        if (Objects.equals(cell.getStuCellPercent(), 100) && !user.getOverTime()) {
                                            printLog("该课件已完成");
                                            continue;
                                        }

                                        // 单元节点集合(实际课件/组)
                                        List<CellListDTO.CellList> cellDataList = new ArrayList<>();

                                        // 根据childNodeList是否为空判断是否有子节点
                                        List<CellListDTO.CellList> childNodeList = cell.getChildNodeList();

                                        // 判断是否有子节点/或者说判断该节点是否为组
                                        if (CollectionUtil.isNotEmpty(childNodeList)) {
                                            // 为组则将下面的课件添加到列表
                                            for (CellListDTO.CellList childNode : childNodeList) {

                                                // 当前单元名
                                                printLog("------------" + childNode.getCellName());

                                                cellDataList.add(childNode);
                                            }
                                        } else {
                                            cellDataList.add(cell);
                                        }
                                        // 遍历子列表
                                        for (CellListDTO.CellList cellData : cellDataList) {
                                            formCellMap.put("moduleId", module.getId());
                                            formCellMap.put("cellId", cellData.getId());

                                            // 获取课件信息
                                            ViewDirectoryDTO viewDirectory = icveCourseService.listViewDirectory(formCellMap, user.getCookie());

                                            if (viewDirectory == null || cellData.getCategoryName() == null) {continue;}

                                            String categoryName = cellData.getCategoryName();

                                            Integer cellPercent = viewDirectory.getCellPercent();

                                            // 更新课程信息到队列
                                            UpdateCourseTaskUtil.bulider(userInfo.getUserId()).course(cellData).put();

                                            // 已完成的不用继续
                                            if (Objects.equals(cellPercent, 100) && !user.getOverTime()) {
                                                // 即使完成也要等3秒
                                                Thread.sleep(3 * 1000);
                                                continue;
                                            }

                                            if (user.getOverTime()) {
                                                // 加时暂只支持文档
                                                switch (categoryName) {
                                                    case "文档":
                                                    case "ppt":
                                                    case "pdf":
                                                    case "swf":
                                                    case "文本":
                                                    case "简单文本":
                                                        icveCourseService.overtimeOffice(user, viewDirectory);
                                                        break;
                                                    default:
                                                        break;
                                                }
                                            } else {
                                                // 根据分类制定不同刷课方案
                                                switch (categoryName) {
                                                    case "视频":
                                                    case "音频":
                                                        icveCourseService.brushVideo(user, viewDirectory, false);
                                                        break;
                                                    case "文档":
                                                    case "ppt":
                                                    case "pdf":
                                                    case "swf":
                                                    case "文本":
                                                    case "简单文本":
                                                        icveCourseService.brushOffice(user, viewDirectory);
                                                        break;
                                                    case "图片":
                                                        icveCourseService.brushImage(user, viewDirectory);
                                                        break;
                                                    default:
                                                        icveCourseService.brushOther(user, viewDirectory);
                                                        break;
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            return new AsyncResult("success");
        } catch (InterruptedException e) {
            printErrorLog("线程中断/取消", e);
            return new AsyncResult<>("cancel");
        } catch (Exception e) {
            for (StackTraceElement element : e.getStackTrace()) {
                printErrorLog("刷课异常\r\n" + element.getClassName() + "." + element.getMethodName() + ";行号:" + element.getLineNumber(), e);
            }
//            Thread.currentThread().interrupt();
            return new AsyncResult<>("fail");
        } finally {
            if (userQueue.get(userInfo.getUserId()) != null) {
                userQueue.remove(userInfo.getUserId());
            }
        }
    }

    /**
     * 初始化参数
     * @param user
     * @return
     */
    private HashMap<String, Object> initParam(IcveUserAndId user) {
        HashMap<String, Object> initMap = new HashMap<>(16);
        initMap.put("courseOpenId", user.getCourseOpenId());
        initMap.put("openClassId", user.getOpenClassId());
        return initMap;
    }

    /**
     * 打印日志
     * @param logger
     */
    private void printLog(String logger) {
        log.info(String.format("[ %s ] %s", this.displayName, logger));
    }

    /**
     * 打印错误日志
     * @param logger
     * @param e
     */
    private void printErrorLog(String logger, Throwable e) {
        log.error(String.format("[ %s ] %s", this.displayName, logger), e);
    }
}
