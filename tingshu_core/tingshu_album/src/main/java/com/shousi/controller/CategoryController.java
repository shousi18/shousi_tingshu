package com.shousi.controller;

import com.shousi.entity.BaseAttribute;
import com.shousi.entity.BaseCategory3;
import com.shousi.entity.BaseCategoryView;
import com.shousi.login.TingShuLogin;
import com.shousi.result.RetVal;
import com.shousi.service.BaseAttributeService;
import com.shousi.service.BaseCategory3Service;
import com.shousi.service.BaseCategoryViewService;
import com.shousi.vo.CategoryVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@Tag(name = "分类管理")
@RestController
@RequestMapping("/api/album/category")
public class CategoryController {

    @Autowired
    private BaseCategoryViewService baseCategoryViewService;

    @Autowired
    private BaseAttributeService baseAttributeService;

    @Autowired
    private BaseCategory3Service category3Service;

    @Autowired
    private BaseCategoryViewService categoryViewService;

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

    @Operation(summary = "通过三级分类id查询分类信息")
    @GetMapping("getCategoryView/{category3Id}")
    public BaseCategoryView getCategoryView(@PathVariable Long category3Id) {
        return baseCategoryViewService.getById(category3Id);
    }

    @Operation(summary = "获取一级分类下置顶到频道页的三级分类列表")
    @GetMapping("getCategory3ListByCategory1Id/{category1Id}")
    public RetVal<List<BaseCategory3>> getCategory3TopByCategory1Id(@PathVariable Long category1Id) {
        List<BaseCategory3> category3TopByCategory1Id = category3Service.getCategory3ListByCategory1Id(category1Id);
        return RetVal.ok(category3TopByCategory1Id);
    }
}
