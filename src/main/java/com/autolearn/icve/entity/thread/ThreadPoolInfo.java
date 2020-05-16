package com.autolearn.icve.entity.thread;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;

/**
 * 线程池信息
 * @author 胡江斌
 * @version 1.0
 * @title: ThreadInfo
 * @projectName autolearn
 * @description: TODO
 * @date 2020/5/13 18:24
 */
@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ThreadPoolInfo implements Serializable {
    /** 当前运行线程数 */
    private Integer workThread;
    /** 当前队列线程数 */
    private Integer workQueue;
    /** 当前运行成功的任务 */
    private Long completedTaskCount;
    /** 核心线程池数量 */
    private Integer corePoolSize;
    /** 最大线程池数量 */
    private Integer maximumPoolSize;
    /** 最大队列数 */
    private Integer maximunQueueSize;
}
