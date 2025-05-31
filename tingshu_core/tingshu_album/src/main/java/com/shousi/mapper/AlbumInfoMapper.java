package com.shousi.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.shousi.entity.AlbumInfo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.shousi.query.AlbumInfoQuery;
import com.shousi.vo.AlbumTempVo;
import org.apache.ibatis.annotations.Param;

/**
* @author 86172
* @description 针对表【album_info(专辑信息)】的数据库操作Mapper
* @createDate 2025-05-30 14:10:49
* @Entity com.shousi.entity.AlbumInfo
*/
public interface AlbumInfoMapper extends BaseMapper<AlbumInfo> {

    /**
     * 分页查询用户专辑
     * @param pageParam
     * @param albumInfoQuery
     * @return
     */
    IPage<AlbumTempVo> getUserAlbumByPage(@Param("pageParam") IPage<AlbumTempVo> pageParam, @Param("albumInfoQuery") AlbumInfoQuery albumInfoQuery);
}




