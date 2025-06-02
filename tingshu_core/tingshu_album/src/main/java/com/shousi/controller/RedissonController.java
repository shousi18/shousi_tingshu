package com.shousi.controller;

import com.shousi.util.SleepUtils;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.redisson.api.RCountDownLatch;
import org.redisson.api.RLock;
import org.redisson.api.RSemaphore;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@Tag(name = "redisson功能测试")
@RestController
@RequestMapping("/api/album/redisson")
public class RedissonController {

    @Autowired
    private RedissonClient redissonClient;

    @GetMapping("lock")
    public String lock() {
        RLock lock = redissonClient.getLock("lock");
        String uuid = UUID.randomUUID().toString();
        try {
            lock.lock();
            SleepUtils.millis(50);
            System.out.println(Thread.currentThread().getName() + "执行业务" + uuid);
        } finally {
            lock.unlock();
        }
        return Thread.currentThread().getName() + "执行业务" + uuid;
    }

    // 读写锁
    String uuid = "";

    @GetMapping("read")
    public String read() {
        RLock lock = redissonClient.getReadWriteLock("rwlock").readLock();
        try {
            lock.lock();
            return uuid;
        } finally {
            lock.unlock();
        }
    }

    @GetMapping("write")
    public void write() {
        RLock lock = redissonClient.getReadWriteLock("rwlock").writeLock();
        try {
            lock.lock();
            SleepUtils.sleep(5);
            uuid = UUID.randomUUID().toString();
            System.out.println(Thread.currentThread().getName() + "执行业务" + uuid);
        } finally {
            lock.unlock();
        }
    }

    // 信号量
    @GetMapping("park")
    public String park() {
        RSemaphore lock = redissonClient.getSemaphore("park");
        try {
            lock.acquire(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return Thread.currentThread().getName() + "找到车位";
    }

    @GetMapping("unpark")
    public String unpark() {
        RSemaphore lock = redissonClient.getSemaphore("park");
        lock.release(1);
        return Thread.currentThread().getName() + "释放车位";
    }

    // 闭锁 CountDownLatch
    @GetMapping("leftClassRoom")
    public String leftClassRoom() {
        RCountDownLatch lock = redissonClient.getCountDownLatch("left_room");
        lock.countDown();
        System.out.println(Thread.currentThread().getName() + "离开教室");

        return Thread.currentThread().getName() + "学员离开";
    }

    @GetMapping("lockRoom")
    public String lockRoom() throws InterruptedException {
        RCountDownLatch lock = redissonClient.getCountDownLatch("left_room");
        lock.trySetCount(6);
        lock.await();
        System.out.println(Thread.currentThread().getName() + "锁门");
        return Thread.currentThread().getName() + "锁门";
    }

    // 公平锁
    @GetMapping("fairLock/{id}")
    public String fairLock(@PathVariable Long id) {
        RLock lock = redissonClient.getFairLock("fairLock");
        lock.lock();
        SleepUtils.sleep(5);
        System.out.println("公平锁" + id);
        lock.unlock();
        return "公平锁" + id;
    }

    //非公平锁
    @GetMapping("unFairLock/{id}")
    public String unFairLock(@PathVariable Long id) {
        RLock unFairLock = redissonClient.getLock("unfire-lock");
        unFairLock.lock();
        SleepUtils.sleep(5);
        System.out.println("非公平锁-" + id);
        unFairLock.unlock();
        return "非公平锁success" + id;
    }
}
