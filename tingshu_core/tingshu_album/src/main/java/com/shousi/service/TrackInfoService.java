package com.shousi.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.shousi.entity.TrackInfo;
import com.baomidou.mybatisplus.extension.service.IService;
import com.shousi.query.TrackInfoQuery;
import com.shousi.vo.TrackTempVo;

/**
 * @author 86172
 * @description 针对表【track_info(声音信息)】的数据库操作Service
 * @createDate 2025-05-30 14:10:49
 */
public interface TrackInfoService extends IService<TrackInfo> {

    /**
     * 保存声音信息
     *
     * @param trackInfo
     */
    void saveTrackInfo(TrackInfo trackInfo);

    /**
     * 查询当前用户声音分页列表
     *
     * @param pageParam
     * @param trackInfoQuery
     * @return
     */
    IPage<TrackTempVo> findUserTrackPage(IPage<TrackTempVo> pageParam, TrackInfoQuery trackInfoQuery);
}
