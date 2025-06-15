package com.shousi.mapper;

import com.shousi.entity.BaseCategoryView;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.shousi.vo.CategoryVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author 86172
 * @description 针对表【base_category_view】的数据库操作Mapper
 * @createDate 2025-05-30 16:33:20
 * @Entity com.shousi.entity.BaseCategoryView
 */
public interface BaseCategoryViewMapper extends BaseMapper<BaseCategoryView> {

    /**
     * 获取所有分类信息
     *
     * @return
     */
    List<CategoryVo> getAllCategoryList(@Param("category1Id") Long category1Id);
}




