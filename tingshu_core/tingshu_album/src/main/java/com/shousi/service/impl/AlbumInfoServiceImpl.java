package com.shousi.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shousi.constant.RedisConstant;
import com.shousi.constant.SystemConstant;
import com.shousi.entity.AlbumAttributeValue;
import com.shousi.entity.AlbumInfo;
import com.shousi.entity.AlbumStat;
import com.shousi.execption.GuiguException;
import com.shousi.mapper.AlbumInfoMapper;
import com.shousi.query.AlbumInfoQuery;
import com.shousi.result.ResultCodeEnum;
import com.shousi.service.AlbumAttributeValueService;
import com.shousi.service.AlbumInfoService;
import com.shousi.service.AlbumStatService;
import com.shousi.util.AuthContextHolder;
import com.shousi.util.SleepUtils;
import com.shousi.vo.AlbumTempVo;
import org.jetbrains.annotations.NotNull;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @author 86172
 * @description 针对表【album_info(专辑信息)】的数据库操作Service实现
 * @createDate 2025-05-30 14:10:49
 */
@Service
public class AlbumInfoServiceImpl extends ServiceImpl<AlbumInfoMapper, AlbumInfo>
        implements AlbumInfoService {

    @Autowired
    private AlbumStatService albumStatService;

    @Autowired
    private AlbumAttributeValueService albumAttributeValueService;

    @Autowired
    private AlbumInfoMapper albumInfoMapper;

    @Autowired
    private RedisTemplate<Object, Object> redisTemplate;

    private final ThreadLocal<String> threadMap = new ThreadLocal<>();

    @Autowired
    private RedissonClient redissonClient;

    @Override
    @Transactional
    public void saveAlbumInfo(AlbumInfo albumInfo) {
        Long userId = AuthContextHolder.getUserId();
        albumInfo.setUserId(userId);
        albumInfo.setStatus(SystemConstant.ALBUM_STATUS_NO_PASS);
        if (!SystemConstant.FREE_ALBUM.equals(albumInfo.getPayType())) {
            // 不是免费的专辑，就只免费开放前五章
            albumInfo.setTracksForFree(5);
        }
        this.save(albumInfo);
        // 保存专辑属性值
        List<AlbumAttributeValue> albumPropertyValueList = albumInfo.getAlbumPropertyValueList();
        if (!CollectionUtils.isEmpty(albumPropertyValueList)) {
            albumPropertyValueList.forEach(albumAttributeValue -> {
                albumAttributeValue.setAlbumId(albumInfo.getId());
            });
            albumAttributeValueService.saveBatch(albumPropertyValueList);
        }
        // 保存专辑的数据信息
        List<AlbumStat> albumStats = buildAlbumStatData(albumInfo.getId());
        albumStatService.saveBatch(albumStats);
        // todo 后面还要保存的信息
    }

    @Override
    public IPage<AlbumTempVo> getUserAlbumByPage(IPage<AlbumTempVo> pageParam, AlbumInfoQuery albumInfoQuery) {
        return albumInfoMapper.getUserAlbumByPage(pageParam, albumInfoQuery);
    }

    @Override
    public AlbumInfo getAlbumInfoById(Long id) {
//        AlbumInfo albumInfo = getAlbumInfoDB(id);
//        AlbumInfo albumInfo = getAlbumInfoRedis(id);
//        AlbumInfo albumInfo = getAlbumInfoFromRedisWithThreadLocal(id);
        AlbumInfo albumInfo = getAlbumInfoByRedisson(id);
        return albumInfo;
    }

    private AlbumInfo getAlbumInfoByRedisson(Long id) {
        String cacheKey = RedisConstant.ALBUM_INFO_PREFIX + id;
        AlbumInfo albumInfoRedis = (AlbumInfo) redisTemplate.opsForValue().get(cacheKey);
        if (Objects.isNull(albumInfoRedis)) {
            RLock lock = redissonClient.getLock("lock-" + id);
            try {
                lock.lock();
                AlbumInfo albumInfoDB = getAlbumInfoDB(id);
                redisTemplate.opsForValue().set(cacheKey, albumInfoDB);
                return albumInfoDB;
            } catch (Exception e) {
                throw new GuiguException(ResultCodeEnum.SERVICE_ERROR);
            } finally {
                lock.unlock();
            }
        }
        return albumInfoRedis;
    }

    private AlbumInfo getAlbumInfoFromRedisWithThreadLocal(Long id) {
        // 缓存中获取
        String cacheKey = RedisConstant.ALBUM_INFO_PREFIX + id;
        AlbumInfo albumInfoRedis = (AlbumInfo) redisTemplate.opsForValue().get(cacheKey);
        // 减小锁的粒度
        String lockKey = "lock-" + id;
        if (albumInfoRedis == null) {
            String token = threadMap.get();
            Boolean acquireLock;
            if (StringUtils.hasText(token)) {
                // 说明是可重入锁
                acquireLock = true;
            } else {
                // 说明是另一把锁
                // 增加唯一标识
                token = UUID.randomUUID().toString();
                // 利用 redis 的 nx操作
                acquireLock = redisTemplate.opsForValue().setIfAbsent(lockKey, token, 3, TimeUnit.SECONDS);
            }
            if (Boolean.TRUE.equals(acquireLock)) {
                // 缓存中没有，则从数据库中获取
                albumInfoRedis = getAlbumInfoDB(id);
                // 缓存中保存
                redisTemplate.opsForValue().set(cacheKey, albumInfoRedis);
                // lua脚本
                String luaScript = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
                // 执行lua脚本
                // 创建 RedisScript 对象，使用构造函数初始化要执行的lua脚本和返回对象类型
                DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>(luaScript, Long.class);
                redisTemplate.execute(redisScript, Arrays.asList(lockKey), token);
                // 移除掉锁
                threadMap.remove();
                return albumInfoRedis;
            } else {
                // 没有拿到锁继续获取锁
                while (true) {
                    SleepUtils.millis(50);
                    // 获取到锁，终止循环
                    if (Boolean.TRUE.equals(redisTemplate.opsForValue().setIfAbsent(lockKey, token, 30, TimeUnit.SECONDS))) {
                        threadMap.set(token);
                        break;
                    }
                }
                return getAlbumInfoFromRedisWithThreadLocal(id);
            }
        }
        return albumInfoRedis;
    }

    private AlbumInfo getAlbumInfoRedis(Long id) {
        String cacheKey = RedisConstant.ALBUM_INFO_PREFIX + id;
        AlbumInfo albumInfoRedis = (AlbumInfo) redisTemplate.opsForValue().get(cacheKey);
        if (albumInfoRedis == null) {
            // 缓存中没有，则从数据库中获取
            albumInfoRedis = getAlbumInfoDB(id);
            // 缓存中保存
            redisTemplate.opsForValue().set(cacheKey, albumInfoRedis);
        }
        return albumInfoRedis;
    }

    @NotNull
    private AlbumInfo getAlbumInfoDB(Long id) {
        AlbumInfo albumInfo = this.getById(id);
        LambdaQueryWrapper<AlbumAttributeValue> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AlbumAttributeValue::getAlbumId, id);
        List<AlbumAttributeValue> albumAttributeValueList = albumAttributeValueService.list(queryWrapper);
        albumInfo.setAlbumPropertyValueList(albumAttributeValueList);
        return albumInfo;
    }

    @Override
    @Transactional
    public void updateAlbumInfo(AlbumInfo albumInfo) {
        this.updateById(albumInfo);
        // 删除专辑属性值
        LambdaQueryWrapper<AlbumAttributeValue> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AlbumAttributeValue::getAlbumId, albumInfo.getId());
        albumAttributeValueService.remove(queryWrapper);
        // 保存专辑属性值
        List<AlbumAttributeValue> albumPropertyValueList = albumInfo.getAlbumPropertyValueList();
        if (!CollectionUtils.isEmpty(albumPropertyValueList)) {
            albumPropertyValueList.forEach(albumAttributeValue -> {
                albumAttributeValue.setAlbumId(albumInfo.getId());
            });
            albumAttributeValueService.saveBatch(albumPropertyValueList);
        }
        // todo 还有其他操作待做
    }

    @Override
    @Transactional
    public void deleteAlbumInfo(Long albumId) {
        // 删除专辑信息
        this.removeById(albumId);
        // 删除专辑属性值
        LambdaQueryWrapper<AlbumAttributeValue> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AlbumAttributeValue::getAlbumId, albumId);
        albumAttributeValueService.remove(queryWrapper);
        // 删除专辑数据信息
        LambdaQueryWrapper<AlbumStat> statLambdaQueryWrapper = new LambdaQueryWrapper<>();
        statLambdaQueryWrapper.eq(AlbumStat::getAlbumId, albumId);
        albumStatService.remove(statLambdaQueryWrapper);
        // todo 删除专辑其他信息
    }

    private List<AlbumStat> buildAlbumStatData(Long albumId) {
        ArrayList<AlbumStat> albumStatList = new ArrayList<>();
        initAlbumStat(albumId, albumStatList, SystemConstant.PLAY_NUM_ALBUM);
        initAlbumStat(albumId, albumStatList, SystemConstant.SUBSCRIBE_NUM_ALBUM);
        initAlbumStat(albumId, albumStatList, SystemConstant.BUY_NUM_ALBUM);
        initAlbumStat(albumId, albumStatList, SystemConstant.COMMENT_NUM_ALBUM);
        return albumStatList;
    }

    private static void initAlbumStat(Long albumId, ArrayList<AlbumStat> albumStatList, String statType) {
        AlbumStat albumStat = new AlbumStat();
        albumStat.setAlbumId(albumId);
        albumStat.setStatType(statType);
        albumStat.setStatNum(0);
        albumStatList.add(albumStat);
    }
}




