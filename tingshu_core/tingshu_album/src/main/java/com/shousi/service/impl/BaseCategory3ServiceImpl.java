package com.shousi.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shousi.entity.BaseCategory2;
import com.shousi.service.BaseCategory2Service;
import com.shousi.service.BaseCategory3Service;
import com.shousi.entity.BaseCategory3;
import com.shousi.mapper.BaseCategory3Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author 86172
 * @description 针对表【base_category3(三级分类表)】的数据库操作Service实现
 * @createDate 2025-05-30 14:10:49
 */
@Service
public class BaseCategory3ServiceImpl extends ServiceImpl<BaseCategory3Mapper, BaseCategory3>
        implements BaseCategory3Service {

    @Autowired
    private BaseCategory2Service baseCategory2Service;

    @Override
    public List<BaseCategory3> getCategory3ListByCategory1Id(Long category1Id) {
        LambdaQueryWrapper<BaseCategory2> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(BaseCategory2::getCategory1Id, category1Id);
        queryWrapper.select(BaseCategory2::getId);
        queryWrapper.orderByAsc(BaseCategory2::getOrderNum);
        List<BaseCategory2> baseCategory2List = baseCategory2Service.list(queryWrapper);
        List<Long> baseCategory2IdList = baseCategory2List.stream().map(BaseCategory2::getId).collect(Collectors.toList());
        LambdaQueryWrapper<BaseCategory3> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(BaseCategory3::getCategory2Id, baseCategory2IdList);
        wrapper.eq(BaseCategory3::getIsTop, 1);
        wrapper.last("limit 7");
        return this.list(wrapper);
    }
}




