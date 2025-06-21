package com.shousi.threadpool;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.*;

@EnableConfigurationProperties(MyThreadPoolProperties.class)
@Configuration
public class MyThreadPool {

    @Autowired
    private MyThreadPoolProperties threadPoolProperties;

    @Bean
    public ThreadPoolExecutor myThreadPoolExecutor() {
        return new ThreadPoolExecutor(
                threadPoolProperties.getCorePoolSize(),
                threadPoolProperties.getMaximumPoolSize(),
                threadPoolProperties.getKeepAliveTime(),
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(threadPoolProperties.getQueueLength()),
                Executors.defaultThreadFactory(),
                new ThreadPoolExecutor.CallerRunsPolicy());
    }
}
