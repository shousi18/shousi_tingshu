package com.shousi.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.shousi.entity.AlbumInfo;
import com.shousi.entity.BaseAttribute;
import com.shousi.login.TingShuLogin;
import com.shousi.result.RetVal;
import com.shousi.service.AlbumInfoService;
import com.shousi.service.BaseAttributeService;
import com.shousi.service.BaseCategory1Service;
import com.shousi.service.BaseCategoryViewService;
import com.shousi.vo.CategoryVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.redisson.api.RBloomFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "并发测试")
@RestController
@RequestMapping("/api/album/con")
public class ConCurrentController {

    @Autowired
    private BaseCategory1Service baseCategory1Service;

    @Autowired
    private AlbumInfoService albumInfoService;

    @Resource
    private RBloomFilter<Long> albumBloomFilter;

    @GetMapping("setNum")
    public String setNum() {
        baseCategory1Service.setNum();
        return "success";
    }

    @Operation(summary = "初始化")
    @GetMapping("init")
    public String initAlbumBloom() {
        LambdaQueryWrapper<AlbumInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.select(AlbumInfo::getId);
        List<AlbumInfo> list = albumInfoService.list(queryWrapper);
        list.forEach(albumInfo -> albumBloomFilter.add(albumInfo.getId()));
        return "success";
    }
}
