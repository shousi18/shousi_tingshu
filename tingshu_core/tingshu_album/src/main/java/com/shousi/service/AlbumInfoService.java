package com.shousi.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.shousi.entity.AlbumInfo;
import com.baomidou.mybatisplus.extension.service.IService;
import com.shousi.query.AlbumInfoQuery;
import com.shousi.vo.AlbumTempVo;

/**
* @author 86172
* @description 针对表【album_info(专辑信息)】的数据库操作Service
* @createDate 2025-05-30 14:10:49
*/
public interface AlbumInfoService extends IService<AlbumInfo> {

    /**
     * 保存专辑信息
     * @param albumInfo
     */
    void saveAlbumInfo(AlbumInfo albumInfo);

    /**
     * 分页查询用户专辑
     * @param pageParam
     * @param albumInfoQuery
     * @return
     */
    IPage<AlbumTempVo> getUserAlbumByPage(IPage<AlbumTempVo> pageParam, AlbumInfoQuery albumInfoQuery);
}
