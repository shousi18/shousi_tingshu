package com.shousi.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shousi.service.BaseCategoryViewService;
import com.shousi.entity.BaseCategoryView;
import com.shousi.mapper.BaseCategoryViewMapper;
import com.shousi.vo.CategoryVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author 86172
 * @description 针对表【base_category_view】的数据库操作Service实现
 * @createDate 2025-05-30 16:33:20
 */
@Service
public class BaseCategoryViewServiceImpl extends ServiceImpl<BaseCategoryViewMapper, BaseCategoryView>
        implements BaseCategoryViewService {

    @Autowired
    private BaseCategoryViewMapper baseCategoryViewMapper;

//    @Override
    @Deprecated
    public List<CategoryVo> getAllCategoryList1() {
        // 获取所有分类
        List<BaseCategoryView> allCategoryView = this.list();
        if (CollectionUtils.isEmpty(allCategoryView)) {
            return null;
        }
        // 查询一级分类
        Map<Long, List<BaseCategoryView>> category1Map = allCategoryView.stream()
                .collect(Collectors.groupingBy(BaseCategoryView::getCategory1Id));
        return category1Map.entrySet().stream()
                .map(category1Entry -> {
                    List<BaseCategoryView> category1List = category1Entry.getValue();
                    CategoryVo category1Vo = new CategoryVo();
                    category1Vo.setCategoryId(category1Entry.getKey());
                    category1Vo.setCategoryName(category1List.get(0).getCategory1Name());
                    // 查询二级分类
                    Map<Long, List<BaseCategoryView>> category2Map = category1List.stream()
                            .collect(Collectors.groupingBy(BaseCategoryView::getCategory2Id));
                    List<CategoryVo> category1Children = category2Map.entrySet().stream()
                            .map(category2Entry -> {
                                List<BaseCategoryView> category2List = category2Entry.getValue();
                                CategoryVo category2Vo = new CategoryVo();
                                category2Vo.setCategoryId(category2Entry.getKey());
                                category2Vo.setCategoryName(category2List.get(0).getCategory2Name());
                                // 查询三级分类
                                Map<Long, List<BaseCategoryView>> category3Map = category2List.stream()
                                        .collect(Collectors.groupingBy(BaseCategoryView::getCategory3Id));
                                List<CategoryVo> category2Children = category3Map.entrySet().stream()
                                        .map(category3Entry -> {
                                            List<BaseCategoryView> category3List = category3Entry.getValue();
                                            CategoryVo category3Vo = new CategoryVo();
                                            category3Vo.setCategoryId(category3Entry.getKey());
                                            category3Vo.setCategoryName(category3List.get(0).getCategory3Name());
                                            return category3Vo;
                                        }).collect(Collectors.toList());
                                category2Vo.setCategoryChild(category2Children);
                                return category2Vo;
                            }).collect(Collectors.toList());
                    category1Vo.setCategoryChild(category1Children);
                    return category1Vo;
                }).collect(Collectors.toList());
    }


    @Override
    public List<CategoryVo> getAllCategoryList() {
        return baseCategoryViewMapper.getAllCategoryList();
    }
}




