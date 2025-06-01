package com.shousi.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.shousi.entity.TrackInfo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.shousi.query.TrackInfoQuery;
import com.shousi.vo.TrackTempVo;
import org.apache.ibatis.annotations.Param;

/**
 * @author 86172
 * @description 针对表【track_info(声音信息)】的数据库操作Mapper
 * @createDate 2025-05-30 14:10:49
 * @Entity com.shousi.entity.TrackInfo
 */
public interface TrackInfoMapper extends BaseMapper<TrackInfo> {

    /**
     * 查询当前用户声音分页列表
     *
     * @param pageParam
     * @param trackInfoQuery
     * @return
     */
    IPage<TrackTempVo> findUserTrackPage(@Param("pageParam") IPage<TrackTempVo> pageParam, @Param("trackInfoQuery") TrackInfoQuery trackInfoQuery);
}




