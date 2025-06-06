package com.shousi.service.impl;

import com.shousi.AlbumFeignClient;
import com.shousi.CategoryFeignClient;
import com.shousi.UserFeignClient;
import com.shousi.entity.*;
import com.shousi.repository.AlbumRepository;
import com.shousi.result.RetVal;
import com.shousi.service.SearchService;
import com.shousi.vo.UserInfoVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Service
@Slf4j
public class SearchServiceImpl implements SearchService {

    @Autowired
    private AlbumFeignClient albumFeignClient;

    @Autowired
    private CategoryFeignClient categoryFeignClient;

    @Autowired
    private UserFeignClient userFeignClient;

    @Autowired
    private AlbumRepository albumRepository;

    @Override
    public void onSaleAlbum(Long albumId) {
        // 获取专辑信息
        AlbumInfo albumInfo = albumFeignClient.getAlbumInfoById(albumId).getData();
        // 获取专辑属性值
        List<AlbumAttributeValue> albumPropertyValue = albumFeignClient.getAlbumPropertyValue(albumId);
        // 获取专辑分类
        BaseCategoryView baseCategoryView = categoryFeignClient.getCategoryView(albumInfo.getCategory3Id());
        // 获取专辑用户
        UserInfoVo userInfoVo = userFeignClient.getUserById(albumInfo.getUserId()).getData();

        // 要进行一系列判空...
        AlbumInfoIndex albumInfoIndex = new AlbumInfoIndex();
        BeanUtils.copyProperties(albumInfo, albumInfoIndex);
        List<AttributeValueIndex> attributeValueIndexList = albumPropertyValue.stream().map(albumAttributeValue -> {
            AttributeValueIndex albumAttributeValueIndex = new AttributeValueIndex();
            BeanUtils.copyProperties(albumAttributeValue, albumAttributeValueIndex);
            return albumAttributeValueIndex;
        }).collect(Collectors.toList());
        albumInfoIndex.setAttributeValueIndexList(attributeValueIndexList);
        albumInfoIndex.setCategory1Id(baseCategoryView.getCategory1Id());
        albumInfoIndex.setCategory2Id(baseCategoryView.getCategory2Id());
        albumInfoIndex.setAnnouncerName(userInfoVo.getNickname());
        albumInfoIndex.setHotScore(Math.random());

        //更新统计量与得分，默认随机，方便测试
        int num1 = new Random().nextInt(1000);
        int num2 = new Random().nextInt(100);
        int num3 = new Random().nextInt(50);
        int num4 = new Random().nextInt(300);
        albumInfoIndex.setPlayStatNum(num1);
        albumInfoIndex.setSubscribeStatNum(num2);
        albumInfoIndex.setBuyStatNum(num3);
        albumInfoIndex.setCommentStatNum(num4);
        //计算公式：未实现
        double hotScore = num1 * 0.2 + num2 * 0.3 + num3 * 0.4 + num4 * 0.1;
        albumInfoIndex.setHotScore(hotScore);

        // 保存到elasticSearch中
        albumRepository.save(albumInfoIndex);
    }

    @Override
    public void offSaleAlbum(Long albumId) {
        albumRepository.deleteById(albumId);
    }
}
