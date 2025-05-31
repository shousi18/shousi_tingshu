package com.shousi.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shousi.constant.SystemConstant;
import com.shousi.entity.AlbumAttributeValue;
import com.shousi.entity.AlbumInfo;
import com.shousi.entity.AlbumStat;
import com.shousi.mapper.AlbumInfoMapper;
import com.shousi.query.AlbumInfoQuery;
import com.shousi.service.AlbumAttributeValueService;
import com.shousi.service.AlbumInfoService;
import com.shousi.service.AlbumStatService;
import com.shousi.util.AuthContextHolder;
import com.shousi.vo.AlbumTempVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

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

    @Override
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
        AlbumInfo albumInfo = this.getById(id);
        LambdaQueryWrapper<AlbumAttributeValue> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AlbumAttributeValue::getAlbumId, id);
        List<AlbumAttributeValue> albumAttributeValueList = albumAttributeValueService.list(queryWrapper);
        albumInfo.setAlbumPropertyValueList(albumAttributeValueList);
        return albumInfo;
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




