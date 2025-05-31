package com.shousi.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.shousi.entity.AlbumInfo;
import com.shousi.login.TingShuLogin;
import com.shousi.result.RetVal;
import com.shousi.service.AlbumInfoService;
import com.shousi.service.VodService;
import com.shousi.util.AuthContextHolder;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@Tag(name = "声音管理")
@RestController
@RequestMapping("/api/album/trackInfo")
public class TrackController {

    @Autowired
    private AlbumInfoService albumInfoService;

    @Autowired
    private VodService vodService;

    @TingShuLogin
    @Operation(summary = "根据用户ID查询用户的专辑信息")
    @GetMapping("/findAlbumByUserId")
    public RetVal<List<AlbumInfo>> findAlbumByUserId() {
        Long userId = AuthContextHolder.getUserId();
        LambdaQueryWrapper<AlbumInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AlbumInfo::getUserId, userId);
        queryWrapper.select(AlbumInfo::getId, AlbumInfo::getAlbumTitle);
        List<AlbumInfo> albumInfoList = albumInfoService.list(queryWrapper);
        return RetVal.ok(albumInfoList);
    }

    @Operation(summary = "上传声音")
    @PostMapping("/uploadTrack")
    public RetVal<Map<String, Object>> uploadTrack(MultipartFile file) {
        Map<String, Object> resultMap = vodService.uploadTrack(file);
        return RetVal.ok(resultMap);
    }
}
