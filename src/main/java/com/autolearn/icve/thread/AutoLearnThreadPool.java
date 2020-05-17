package com.autolearn.icve.thread;

import com.autolearn.icve.entity.icve.*;
import com.autolearn.icve.service.IcveCourseService;
import com.xiaoleilu.hutool.util.CollectionUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.Future;

import static com.autolearn.icve.controller.CourseApi.userQueue;

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
     * @param user
     * @return
     */
    @Async
    public Future<String> brush(IcveUserAndId user) {

        IcveUser.User userInfo = user.getUser();

        CourseTaskDTO courseTaskDTO = userQueue.get(userInfo.getUserId());
        courseTaskDTO.setState(CourseTaskDTO.StateEnum.START);
        userQueue.put(userInfo.getUserId(), courseTaskDTO);

        this.displayName = userInfo.getUserName() + "|" + userInfo.getDisplayName();

        printLog("开始刷课");

        // 整个课程模块
        Map<String, Object> formMap = new HashMap<>(8);
        formMap.put("courseOpenId", user.getCourseOpenId());
        formMap.put("openClassId", user.getOpenClassId());

        // 子列表参数
        Map<String, Object> formModelMap = new HashMap<>(8);
        formModelMap.put("courseOpenId", user.getCourseOpenId());

        // 单元列表参数
        Map<String, Object> formTopicMap = new HashMap<>(8);
        formTopicMap.put("courseOpenId", user.getCourseOpenId());
        formTopicMap.put("openClassId", user.getOpenClassId());


        // 节点列表参数
        Map<String, Object> formCellMap = new HashMap<>(8);
        formCellMap.put("courseOpenId", user.getCourseOpenId());
        formCellMap.put("openClassId", user.getOpenClassId());
        formCellMap.put("flag", "s");

        try {
            ProcessListDTO processList = icveCourseService.listProcess(user.getCookie(), formMap);

            if (processList != null && Objects.equals(processList.getCode(), 1)) {

                printLog("课件数:" + processList.getOpenCourseCellCount());

                List<ProcessListDTO.ModuleList> moduleList = processList.getProgress().getModuleList();
                for (ProcessListDTO.ModuleList module : moduleList) {

                    // 当前模块名
                    printLog(module.getName());

                    if (Objects.equals(module.getPercent(), 100)) {
                        printLog("该模组已完成");
                        continue;
                    }

                    formModelMap.put("moduleId", module.getId());

                    // 子列表
                    TopicListDTO listTopic = icveCourseService.listTopic(user.getCookie(), formModelMap);
                    if (listTopic != null && Objects.equals(listTopic.getCode(), 1)) {
                        List<TopicListDTO.TopicList> topics = listTopic.getTopicList();

                        for (TopicListDTO.TopicList topic : topics) {
                            printLog("----" + topic.getName());

                            formTopicMap.put("topicId", topic.getId());

                            CellListDTO cells = icveCourseService.listCell(user.getCookie(), formTopicMap);

                            if (cells != null && Objects.equals(cells.getCode(), 1)) {

                                List<CellListDTO.CellList> cellList = cells.getCellList();

                                // 判断是否有子节点/或者说判断该节点是否为组
                                if (CollectionUtil.isNotEmpty(cellList)) {
                                    for (CellListDTO.CellList cell : cellList) {
                                        // 当前单元名
                                        printLog("--------" + cell.getCellName());

                                        if (Objects.equals(cell.getStuCellPercent(), 100)) {
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

                                            // 重试次数
                                            int count = 3;
                                            viewDirectory:{
                                                // 获取课件信息
                                                ViewDirectoryDTO viewDirectory = icveCourseService.listViewDirectory(user.getCookie(), formCellMap);

                                                String categoryName = cellData.getCategoryName();

                                                Integer cellPercent = viewDirectory.getCellPercent();

                                                // 更新课程信息到队列
                                                CourseTaskDTO courseTask = userQueue.get(userInfo.getUserId());
                                                courseTask.setCourse(cellData);
                                                userQueue.put(userInfo.getUserId(), courseTask);

                                                // 已完成的不用继续
                                                if (Objects.equals(cellPercent, 100)) {
                                                    // 即使完成也要等5秒
                                                    Thread.sleep(5 * 1000);
                                                    continue;
                                                }
                                                // 根据分类制定不同刷课方案
                                                switch (categoryName) {
                                                    case "视频":
                                                    case "音频":
                                                        int result = icveCourseService.brushVideo(user, viewDirectory);
                                                        if (result == 2 && --count <= 1) {
                                                            break viewDirectory;
                                                        }
                                                        break;
                                                    case "文档":
                                                    case "ppt":
                                                    case "swf":
                                                    case "文本":
                                                    case "简单文本":
                                                        icveCourseService.brushOffice(user, viewDirectory);
                                                        break;
                                                    case "图片":
                                                        icveCourseService.brushImage(user, viewDirectory);
                                                        break;
                                                    default:
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
            printErrorLog("刷课异常", e);
            Thread.currentThread().interrupt();
            return new AsyncResult<>("fail");
        } finally {
            if (userQueue.get(userInfo.getUserId()) != null) {
                userQueue.remove(userInfo.getUserId());
            }
        }
    }
    
    private void printLog(String logger) {
        log.info(String.format("[ %s ] %s", this.displayName, logger));
    }

    private void printErrorLog(String logger, Throwable e) {
        log.error(String.format("[ %s ] %s", this.displayName, logger), e);
    }
}
