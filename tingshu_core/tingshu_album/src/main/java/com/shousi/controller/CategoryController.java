package com.shousi.controller;

import com.shousi.entity.BaseAttribute;
import com.shousi.login.TingShuLogin;
import com.shousi.result.RetVal;
import com.shousi.service.BaseAttributeService;
import com.shousi.service.BaseCategoryViewService;
import com.shousi.vo.CategoryVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "分类管理")
@RestController
@RequestMapping("/api/album/category")
public class CategoryController {

    @Autowired
    private BaseCategoryViewService baseCategoryViewService;

    @Autowired
    private BaseAttributeService baseAttributeService;

    @TingShuLogin
    @Operation(summary = "获取全部分类信息")
    @GetMapping("/getAllCategoryList")
    public RetVal<?> getAllCategoryList() {
        List<CategoryVo> categoryVoList = baseCategoryViewService.getAllCategoryList();
        return RetVal.ok(categoryVoList);
    }

    @Operation(summary = "根据一级分类Id查询分类属性信息")
    @GetMapping("getPropertyByCategory1Id/{category1Id}")
    public RetVal<List<BaseAttribute>> getPropertyByCategory1Id(@PathVariable("category1Id") Long category1Id) {
        List<BaseAttribute> baseAttributeList = baseAttributeService.getPropertyByCategory1Id(category1Id);
        return RetVal.ok(baseAttributeList);
    }
}
