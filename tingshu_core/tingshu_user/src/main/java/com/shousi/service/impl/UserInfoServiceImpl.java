package com.shousi.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shousi.entity.UserInfo;
import com.shousi.service.UserInfoService;
import com.shousi.mapper.UserInfoMapper;
import org.springframework.stereotype.Service;

/**
* @author 86172
* @description 针对表【user_info(用户)】的数据库操作Service实现
* @createDate 2025-05-29 21:21:08
*/
@Service
public class UserInfoServiceImpl extends ServiceImpl<UserInfoMapper, UserInfo>
    implements UserInfoService {

}




