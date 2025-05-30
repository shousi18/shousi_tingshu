package com.shousi.vo;

import lombok.Data;

import java.util.List;

@Data
public class CategoryVo {
    /**
     * 分类id
     */
    private Long categoryId;

    /**
     * 分类名称
     */
    private String categoryName;

    /**
     * 子分类
     */
    private List<CategoryVo> categoryChild;
}