package com.shousi.controller;

import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.redisson.api.RBloomFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ConCurrentControllerTest {

    @Resource
    private RBloomFilter<Long> albumBloomFilter;

    @Test
    void initAlbumBloom() {
        boolean contains = albumBloomFilter.contains(1600L);
        System.out.println(contains);
        boolean contains1 = albumBloomFilter.contains(1900L);
        System.out.println(contains1);
    }
}