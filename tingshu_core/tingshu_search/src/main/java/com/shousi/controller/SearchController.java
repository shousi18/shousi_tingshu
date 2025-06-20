package com.shousi.controller;

import com.shousi.login.TingShuLogin;
import com.shousi.query.AlbumIndexQuery;
import com.shousi.result.RetVal;
import com.shousi.service.SearchService;
import com.shousi.vo.AlbumSearchResponseVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Tag(name = "搜索专辑管理")
@RestController
@RequestMapping("api/search/albumInfo")
public class SearchController {

    @Autowired
    private SearchService searchService;

    @Operation(summary = "上架专辑")
    @GetMapping("onSaleAlbum/{albumId}")
    public void onSaleAlbum(@PathVariable Long albumId) {
        searchService.onSaleAlbum(albumId);
    }

    @Operation(summary = "批量上架专辑")
    @GetMapping("batchOnSaleAlbum")
    public String batchOnSaleAlbum() {
        for (long i = 1; i < 1585; i++) {
            searchService.onSaleAlbum(i);
        }
        return "success";
    }

    @Operation(summary = "下架专辑")
    @GetMapping("offSaleAlbum/{albumId}")
    public void offSaleAlbum(@PathVariable Long albumId) {
        searchService.offSaleAlbum(albumId);
    }

    @Operation(summary = "获取主页频道数据")
    @GetMapping("getChannelData/{category1Id}")
    public RetVal<List<Map<String, Object>>> getChannelData(@PathVariable Long category1Id) {
        List<Map<String, Object>> retData = searchService.getChannelData(category1Id);
        return RetVal.ok(retData);
    }

    @Operation(summary = "专辑搜索")
    @PostMapping
    public RetVal<AlbumSearchResponseVo> search(@RequestBody AlbumIndexQuery albumIndexQuery) {
        AlbumSearchResponseVo searchResponseVo = searchService.search(albumIndexQuery);
        return RetVal.ok(searchResponseVo);
    }

    @Operation(summary = "关键字自动补全")
    @GetMapping("autoCompleteSuggest/{keyword}")
    public RetVal<Set<String>> autoCompleteSuggest(@PathVariable String keyword) {
        Set<String> suggestTitleList = searchService.autoCompleteSuggest(keyword);
        return RetVal.ok(suggestTitleList);
    }

    @TingShuLogin
    @Operation(summary = "专辑详情")
    @GetMapping("getAlbumDetail/{albumId}")
    public RetVal<Map<String,Object>> getAlbumDetail(@PathVariable Long albumId){
        Map<String,Object> result = searchService.getAlbumDetail(albumId);
        return RetVal.ok(result);
    }
}
