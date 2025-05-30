package com.shousi.controller;

import com.shousi.login.TingShuLogin;
import com.shousi.result.RetVal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "分类管理")
@RestController
@RequestMapping("/api/album/category")
public class CategoryController {

    @TingShuLogin
    @Operation(summary = "获取全部分类信息")
    @GetMapping("/getAllCategoryList")
    public RetVal<?> getAllCategoryList() {
        return RetVal.ok();
    }
}
