package com.autolearn.icve.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author 胡江斌
 * @version 1.0
 * @title: ThreadPoolProperties
 * @projectName autolearn
 * @description: TODO
 * @date 2020/5/13 16:08
 */
@Data
@ConfigurationProperties(prefix = "threadpool")
public class ThreadPoolProperties {
    /** 核心线程池数量 */
    private int corePoolSize;
    /** 最大线程池数量 */
    private int maxPoolSize;
    /** 等待队列数量 */
    private int queueCapacity;
    /** 线程名前缀 */
    private String threadNamePrefix;

    public ThreadPoolProperties() {
        this.corePoolSize = 3;
        this.maxPoolSize = 10;
        this.queueCapacity = 50;
        this.threadNamePrefix = "thread-";
    }
}
