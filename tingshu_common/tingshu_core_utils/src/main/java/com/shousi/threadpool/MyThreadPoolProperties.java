package com.shousi.threadpool;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "thread.pool")
public class MyThreadPoolProperties {
    //初始化需要赋值 不然会报空指针异常
    public int corePoolSize = 16;
    public int maximumPoolSize = 32;
    public Long keepAliveTime = 50L;
    public int queueLength = 100;
}