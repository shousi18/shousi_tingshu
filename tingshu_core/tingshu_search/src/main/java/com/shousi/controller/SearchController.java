package com.shousi.controller;

import com.shousi.AlbumFeignClient;
import com.shousi.entity.AlbumInfo;
import com.shousi.service.SearchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
