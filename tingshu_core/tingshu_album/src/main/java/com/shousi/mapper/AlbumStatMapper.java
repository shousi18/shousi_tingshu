package com.shousi.mapper;

import com.shousi.entity.AlbumStat;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.shousi.vo.AlbumStatVo;
import org.apache.ibatis.annotations.Param;

/**
* @author 86172
* @description 针对表【album_stat(专辑统计)】的数据库操作Mapper
* @createDate 2025-05-30 14:10:49
* @Entity com.shousi.entity.AlbumStat
*/
public interface AlbumStatMapper extends BaseMapper<AlbumStat> {

    /**
     * 获取专辑统计信息
     * @param albumId
     * @return
     */
    AlbumStatVo getAlbumStatInfo(@Param("albumId") Long albumId);
}




