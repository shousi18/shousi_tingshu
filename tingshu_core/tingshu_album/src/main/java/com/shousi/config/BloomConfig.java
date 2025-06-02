package com.shousi.config;

import com.shousi.constant.RedisConstant;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BloomConfig {
    @Autowired
    private RedissonClient redissonClient;

    @Bean
    public RBloomFilter<Long> albumBloomFilter() {
        RBloomFilter<Long> bloomFilter = redissonClient.getBloomFilter(RedisConstant.ALBUM_BLOOM_FILTER);
        bloomFilter.tryInit(10000, 0.001);
        return bloomFilter;
    }
}
