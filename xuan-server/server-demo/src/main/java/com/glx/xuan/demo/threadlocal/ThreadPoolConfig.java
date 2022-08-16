package com.glx.xuan.demo.threadlocal;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

@Configuration
@Slf4j
public class ThreadPoolConfig {


    @Bean(name = "bpmTaskExecutor")
//    @Primary
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(10);
        executor.setMaxPoolSize(50);
        executor.setQueueCapacity(100);
        executor.setKeepAliveSeconds(10);
        executor.setThreadNamePrefix("bpmThreadPool-");
        executor.setTaskDecorator(runnable -> () -> {
            try {
                runnable.run();
            } finally {
                // 防止内存泄漏
//                TokenManager.clear();
            }
        });
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        return executor;
    }

}