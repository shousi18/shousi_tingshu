package com.shousi.service;

import com.shousi.entity.BaseAttribute;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * @author 86172
 * @description 针对表【base_attribute(属性表)】的数据库操作Service
 * @createDate 2025-05-30 14:10:49
 */
public interface BaseAttributeService extends IService<BaseAttribute> {

    /**
     * 根据一级分类Id查询分类属性信息
     *
     * @param category1Id
     * @return
     */
    List<BaseAttribute> getPropertyByCategory1Id(Long category1Id);
}
