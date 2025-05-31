package com.shousi.mapper;

import com.shousi.entity.BaseAttribute;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
 * @author 86172
 * @description 针对表【base_attribute(属性表)】的数据库操作Mapper
 * @createDate 2025-05-30 14:10:49
 * @Entity com.shousi.entity.BaseAttribute
 */
public interface BaseAttributeMapper extends BaseMapper<BaseAttribute> {

    /**
     * 根据一级分类Id查询分类属性信息
     *
     * @param category1Id
     * @return
     */
    List<BaseAttribute> getPropertyByCategory1Id(Long category1Id);
}




