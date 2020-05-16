package com.autolearn.icve.config;

import com.autolearn.icve.config.properties.ThreadPoolProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * 刷课线程池
 * @author 胡江斌
 * @version 1.0
 * @title: ThreadPoolConfig
 * @projectName autolearn
 * @description: TODO
 * @date 2020/5/12 23:21
 */
@Configuration
@EnableAsync
@EnableConfigurationProperties({ThreadPoolProperties.class})
public class ExecutorConfig {

    @Autowired
    private ThreadPoolProperties threadPoolProperties;

    @Bean
    public ThreadPoolTaskExecutor threadPoolTaskExecutor() {
        ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
        // 核心线程数量
        threadPoolTaskExecutor.setCorePoolSize(threadPoolProperties.getCorePoolSize());
        threadPoolTaskExecutor.setAllowCoreThreadTimeOut(true);
        // 最大线程数
        threadPoolTaskExecutor.setMaxPoolSize(threadPoolProperties.getMaxPoolSize());
        // 队列大小
        threadPoolTaskExecutor.setQueueCapacity(threadPoolProperties.getQueueCapacity());
        // 线程池前缀
        threadPoolTaskExecutor.setThreadNamePrefix(threadPoolProperties.getThreadNamePrefix());
        // 设置拒绝策略
        threadPoolTaskExecutor.setRejectedExecutionHandler(new ThreadPoolExecutor.AbortPolicy());
        // 初始化
        threadPoolTaskExecutor.initialize();
        return threadPoolTaskExecutor;
    }
}
