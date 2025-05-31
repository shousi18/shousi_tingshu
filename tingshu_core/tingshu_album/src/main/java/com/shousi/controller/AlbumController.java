package com.shousi.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.shousi.entity.AlbumInfo;
import com.shousi.login.TingShuLogin;
import com.shousi.query.AlbumInfoQuery;
import com.shousi.result.RetVal;
import com.shousi.service.AlbumInfoService;
import com.shousi.util.AuthContextHolder;
import com.shousi.vo.AlbumTempVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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

    @TingShuLogin
    @Operation(summary = "分页查询专辑")
    @PostMapping("/getUserAlbumByPage/{pageNum}/{pageSize}")
    public RetVal<IPage<AlbumTempVo>> getUserAlbumByPage(
            @PathVariable("pageNum") Long pageNum,
            @PathVariable("pageSize") Long pageSize,
            @RequestBody AlbumInfoQuery albumInfoQuery) {
        albumInfoQuery.setUserId(AuthContextHolder.getUserId());
        IPage<AlbumTempVo> pageParam = new Page<>(pageNum, pageSize);
        pageParam = albumInfoService.getUserAlbumByPage(pageParam, albumInfoQuery);
        return RetVal.ok(pageParam);
    }

    @TingShuLogin
    @Operation(summary = "根据id查询专辑")
    @GetMapping("getAlbumInfoById/{id}")
    public RetVal<AlbumInfo> getAlbumInfoById(@PathVariable Long id) {
        AlbumInfo albumInfo = albumInfoService.getAlbumInfoById(id);
        return RetVal.ok(albumInfo);
    }

    @TingShuLogin
    @Operation(summary = "修改专辑")
    @PutMapping("/updateAlbumInfo")
    public RetVal<?> updateAlbumInfo(@RequestBody AlbumInfo albumInfo) {
        albumInfoService.updateAlbumInfo(albumInfo);
        return RetVal.ok();
    }

    @TingShuLogin
    @Operation(summary = "删除专辑")
    @DeleteMapping("/deleteAlbumInfo/{albumId}")
    public RetVal<?> deleteAlbumInfo(@PathVariable Long albumId) {
        albumInfoService.deleteAlbumInfo(albumId);
        return RetVal.ok();
    }
}
