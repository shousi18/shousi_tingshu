package com.shousi.controller;

import com.shousi.entity.AlbumInfo;
import com.shousi.login.TingShuLogin;
import com.shousi.result.RetVal;
import com.shousi.service.AlbumInfoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "专辑管理")
@RestController
@RequestMapping("/api/album/albumInfo")
public class AlbumController {

    @Autowired
    private AlbumInfoService albumInfoService;

    @TingShuLogin
    @Operation(summary = "新增专辑")
    @PostMapping("/saveAlbumInfo")
    public RetVal<?> saveAlbumInfo(@RequestBody AlbumInfo albumInfo) {
        albumInfoService.saveAlbumInfo(albumInfo);
        return RetVal.ok();
    }
}
