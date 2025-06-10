package com.shousi.service;

import com.shousi.entity.BaseCategory3;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
* @author 86172
* @description 针对表【base_category3(三级分类表)】的数据库操作Service
* @createDate 2025-05-30 14:10:49
*/
public interface BaseCategory3Service extends IService<BaseCategory3> {

    List<BaseCategory3> getCategory3ListByCategory1Id(Long category1Id);
}
