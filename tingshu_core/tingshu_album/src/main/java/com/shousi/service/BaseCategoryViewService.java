package com.shousi.service;

import com.shousi.entity.BaseCategoryView;
import com.baomidou.mybatisplus.extension.service.IService;
import com.shousi.vo.CategoryVo;

import java.util.List;

/**
 * @author 86172
 * @description 针对表【base_category_view】的数据库操作Service
 * @createDate 2025-05-30 16:33:20
 */
public interface BaseCategoryViewService extends IService<BaseCategoryView> {

    /**
     * 获取所有分类信息
     *
     * @return
     */
    List<CategoryVo> getAllCategoryList(Long category1Id);
}
