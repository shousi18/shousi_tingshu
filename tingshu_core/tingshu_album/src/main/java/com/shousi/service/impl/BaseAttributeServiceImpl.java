package com.shousi.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shousi.service.BaseAttributeService;
import com.shousi.entity.BaseAttribute;
import com.shousi.mapper.BaseAttributeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author 86172
 * @description 针对表【base_attribute(属性表)】的数据库操作Service实现
 * @createDate 2025-05-30 14:10:49
 */
@Service
public class BaseAttributeServiceImpl extends ServiceImpl<BaseAttributeMapper, BaseAttribute>
        implements BaseAttributeService {

    @Autowired
    private BaseAttributeMapper baseAttributeMapper;

    @Override
    public List<BaseAttribute> getPropertyByCategory1Id(Long category1Id) {
        return baseAttributeMapper.getPropertyByCategory1Id(category1Id);
    }
}




