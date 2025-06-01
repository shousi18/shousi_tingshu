package com.shousi.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shousi.constant.SystemConstant;
import com.shousi.entity.AlbumInfo;
import com.shousi.entity.TrackInfo;
import com.shousi.entity.TrackStat;
import com.shousi.mapper.TrackInfoMapper;
import com.shousi.query.TrackInfoQuery;
import com.shousi.service.AlbumInfoService;
import com.shousi.service.TrackInfoService;
import com.shousi.service.TrackStatService;
import com.shousi.service.VodService;
import com.shousi.util.AuthContextHolder;
import com.shousi.vo.TrackTempVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 86172
 * @description 针对表【track_info(声音信息)】的数据库操作Service实现
 * @createDate 2025-05-30 14:10:49
 */
@Service
public class TrackInfoServiceImpl extends ServiceImpl<TrackInfoMapper, TrackInfo>
        implements TrackInfoService {

    @Autowired
    private AlbumInfoService albumInfoService;

    @Autowired
    private VodService vodService;

    @Autowired
    private TrackStatService trackStatService;

    @Autowired
    private TrackInfoMapper trackInfoMapper;

    @Override
    @Transactional
    public void saveTrackInfo(TrackInfo trackInfo) {
        trackInfo.setUserId(AuthContextHolder.getUserId());
        trackInfo.setStatus(SystemConstant.TRACK_APPROVED);
        // 保存与音频本身相关的信息
        vodService.getTrackMediaInfo(trackInfo);
        // 获取到专辑中音频编号的最大值
        LambdaQueryWrapper<TrackInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.select(TrackInfo::getOrderNum)
                .eq(TrackInfo::getAlbumId, trackInfo.getAlbumId())
                .orderByDesc(TrackInfo::getId)
                .last("limit 1");
        TrackInfo maxOrderNumTrackInfo = this.getOne(wrapper);
        int orderNum = 1;
        if (maxOrderNumTrackInfo != null) {
            orderNum = maxOrderNumTrackInfo.getOrderNum() + 1;
        }
        trackInfo.setOrderNum(orderNum);
        // 保存音频
        this.save(trackInfo);
        // 更新专辑的音频个数
        AlbumInfo albumInfo = albumInfoService.getById(trackInfo.getAlbumId());
        LambdaUpdateWrapper<AlbumInfo> albumInfoLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        albumInfoLambdaUpdateWrapper.eq(AlbumInfo::getId, albumInfo.getId())
                .set(AlbumInfo::getIncludeTrackCount, albumInfo.getIncludeTrackCount() + 1);
        albumInfoService.update(albumInfoLambdaUpdateWrapper);
        //初始化声音的统计数据
        List<TrackStat> trackStatList = buildTrackStatData(trackInfo.getId());
        trackStatService.saveBatch(trackStatList);
    }

    @Override
    public IPage<TrackTempVo> findUserTrackPage(IPage<TrackTempVo> pageParam, TrackInfoQuery trackInfoQuery) {
        return trackInfoMapper.findUserTrackPage(pageParam, trackInfoQuery);
    }

    private List<TrackStat> buildTrackStatData(Long trackId) {
        List<TrackStat> trackStatList = new ArrayList<>();
        initTrackStat(trackId, trackStatList, SystemConstant.PLAY_NUM_TRACK);
        initTrackStat(trackId, trackStatList, SystemConstant.COLLECT_NUM_TRACK);
        initTrackStat(trackId, trackStatList, SystemConstant.PRAISE_NUM_TRACK);
        initTrackStat(trackId, trackStatList, SystemConstant.COMMENT_NUM_TRACK);
        return trackStatList;
    }

    private static void initTrackStat(Long trackId, List<TrackStat> trackStatList, String statType) {
        TrackStat trackStat = new TrackStat();
        trackStat.setTrackId(trackId);
        trackStat.setStatType(statType);
        trackStat.setStatNum(0);
        trackStatList.add(trackStat);
    }
}




