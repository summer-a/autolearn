package com.autolearn.icve;

import com.autolearn.icve.utils.SystemInfoUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 项目入口
 * @author 胡江兵
 */
@Slf4j
@SpringBootApplication
public class AutolearnApplication {

    public static void main(String[] args) {
        // 创建项目销毁钩子
        Runtime.getRuntime().addShutdownHook(new ShutdownHookClass());
        try {
            SpringApplication.run(AutolearnApplication.class, args);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("项目main方法异常退出", e);
        }
    }
}

/**
 * 项目关闭钩子
 */
class ShutdownHookClass extends Thread {
    @Override
    public void run() {
        SystemInfoUtil instance = SystemInfoUtil.getInstance();
        System.err.println("项目被强制关闭");
        System.err.println(instance.toString());
    }
}