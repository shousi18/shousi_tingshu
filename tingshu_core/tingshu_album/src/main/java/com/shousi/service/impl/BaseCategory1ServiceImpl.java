package com.shousi.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shousi.service.BaseCategory1Service;
import com.shousi.entity.BaseCategory1;
import com.shousi.mapper.BaseCategory1Mapper;
import com.shousi.util.SleepUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @author 86172
 * @description 针对表【base_category1(一级分类表)】的数据库操作Service实现
 * @createDate 2025-05-30 14:10:49
 */
@Service
public class BaseCategory1ServiceImpl extends ServiceImpl<BaseCategory1Mapper, BaseCategory1>
        implements BaseCategory1Service {

    @Autowired
    private RedisTemplate<Object, Object> redisTemplate;

    //     @Override
    public void setNum01() {
        // 测试并发
        doBusiness();
    }

    // 加锁解决单机多线程并发问题
//    @Override
    public synchronized void setNum02() {
        // 测试并发
        doBusiness();
    }

    // 解决分布式多线程并发问题1
    // @Override
    public void setNum03() {
        // 利用 redis 的 nx操作
        Boolean acquireLock = redisTemplate.opsForValue().setIfAbsent("lock", "ok");
        if (Boolean.TRUE.equals(acquireLock)) {
            doBusiness();
            // 释放锁
            redisTemplate.delete("lock");
        } else {
            // 没有拿到锁继续获取锁
            setNum03();
        }
    }

    // 解决分布式多线程并发问题2
    // 解决锁一直占用问题
    // @Override
    public void setNum04() {
        // 利用 redis 的 nx操作
        Boolean acquireLock = redisTemplate.opsForValue().setIfAbsent("lock", "ok", 3, TimeUnit.SECONDS);
        if (Boolean.TRUE.equals(acquireLock)) {
            doBusiness();
            // 释放锁
            redisTemplate.delete("lock");
        } else {
            // 没有拿到锁继续获取锁
            setNum04();
        }
    }

    // 解决分布式多线程并发问题3
    // 问题：锁不是本人释放问题
    // doBusiness执行 31s，锁过期时间为30s
    // 线程1 先进来执行 结果执行到30s的时候，锁被释放；
    // 线程2 获取到锁 执行业务 执行了1s，锁被线程1 释放掉了
    // 这样子一直循环往复 也会造成锁失效的问题
    // @Override
    public void setNum05() {
        // 增加唯一标识
        String token = UUID.randomUUID().toString();
        // 利用 redis 的 nx操作
        Boolean acquireLock = redisTemplate.opsForValue().setIfAbsent("lock", token, 3, TimeUnit.SECONDS);
        if (Boolean.TRUE.equals(acquireLock)) {
            doBusiness();
            String redisToken = (String) redisTemplate.opsForValue().get("lock");
            // 释放锁
            if (token.equals(redisToken)) {
                redisTemplate.delete("lock");
            }
        } else {
            // 没有拿到锁继续获取锁
            setNum05();
        }
    }

    // 解决分布式多线程并发问题4
    // 问题：对比redisToken和删除分布式锁不是一个原子操作
    // 29.999999s的时候，线程一执行完doBusiness操作，紧接着获取到redisToken，并且判断token.equals(redisToken)成功
    // 锁过期了，线程二进来，获取到了锁
    // 但是这个时候线程一执行delete操作删除掉了分布式锁
    // 线程二刚获取到的锁又被别人释放了
    // @Override
    public void setNum06() {
        // 增加唯一标识
        String token = UUID.randomUUID().toString();
        // 利用 redis 的 nx操作
        Boolean acquireLock = redisTemplate.opsForValue().setIfAbsent("lock", token, 3, TimeUnit.SECONDS);
        if (Boolean.TRUE.equals(acquireLock)) {
            doBusiness();
            String redisToken = (String) redisTemplate.opsForValue().get("lock");
            // lua脚本
            String luaScript = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
            // 执行lua脚本
            // 创建 RedisScript 对象，使用构造函数初始化要执行的lua脚本和返回对象类型
            DefaultRedisScript<Integer> redisScript = new DefaultRedisScript<>(luaScript, Integer.class);
            redisTemplate.execute(redisScript, List.of("lock"), redisToken);
        } else {
            // 没有拿到锁继续获取锁
            setNum06();
        }
    }

    // 解决分布式多线程并发问题5
    // 问题：解决自旋获取锁的时候反复执行前面繁琐的业务代码逻辑
    // @Override
    public void setNum07() {
        // 假设这里有很耗时的业务操作...
        // 增加唯一标识
        String token = UUID.randomUUID().toString();
        // 利用 redis 的 nx操作
        Boolean acquireLock = redisTemplate.opsForValue().setIfAbsent("lock", token, 3, TimeUnit.SECONDS);
        if (Boolean.TRUE.equals(acquireLock)) {
            doBusiness();
            String redisToken = (String) redisTemplate.opsForValue().get("lock");
            // lua脚本
            String luaScript = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
            // 执行lua脚本
            // 创建 RedisScript 对象，使用构造函数初始化要执行的lua脚本和返回对象类型
            DefaultRedisScript<Integer> redisScript = new DefaultRedisScript<>(luaScript, Integer.class);
            redisTemplate.execute(redisScript, List.of("lock"), redisToken);
        } else {
            // 没有拿到锁继续获取锁
            while (true) {
                SleepUtils.millis(50);
                // 获取到锁，终止循环
                if (Boolean.TRUE.equals(redisTemplate.opsForValue().setIfAbsent("lock", token, 30, TimeUnit.SECONDS))) {
                    break;
                }
            }
            setNum07();
        }
    }

    // 解决分布式多线程并发问题6
    // 问题：解决无法可重入锁
    Map<Thread, String> threadMap = new HashMap<>();

    @Override
    public void setNum() {
        // 假设这里有很耗时的业务操作...
        String token = threadMap.get(Thread.currentThread());
        Boolean acquireLock;
        if (StringUtils.hasText(token)) {
            // 说明是可重入锁
            acquireLock = true;
        } else {
            // 说明是另一把锁
            // 增加唯一标识
            token = UUID.randomUUID().toString();
            // 利用 redis 的 nx操作
            acquireLock = redisTemplate.opsForValue().setIfAbsent("lock", token, 3, TimeUnit.SECONDS);
        }
        if (Boolean.TRUE.equals(acquireLock)) {
            doBusiness();
            // lua脚本
            String luaScript = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
            // 执行lua脚本
            // 创建 RedisScript 对象，使用构造函数初始化要执行的lua脚本和返回对象类型
            DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>(luaScript, Long.class);
            redisTemplate.execute(redisScript, List.of("lock"), token);
            // 移除掉锁
            threadMap.remove(Thread.currentThread());
        } else {
            // 没有拿到锁继续获取锁
            while (true) {
                SleepUtils.millis(50);
                // 获取到锁，终止循环
                if (Boolean.TRUE.equals(redisTemplate.opsForValue().setIfAbsent("lock", token, 30, TimeUnit.SECONDS))) {
                    threadMap.put(Thread.currentThread(), token);
                    break;
                }
            }
            setNum();
        }
    }

    private void doBusiness() {
        String num = (String) redisTemplate.opsForValue().get("num");
        if (!StringUtils.hasText(num)) {
            redisTemplate.opsForValue().set("num", "1");
        } else {
            redisTemplate.opsForValue().set("num", String.valueOf(Integer.parseInt(num) + 1));
        }
    }
}




