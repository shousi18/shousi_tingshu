package com.shousi.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shousi.entity.UserAccount;
import com.shousi.service.UserAccountService;
import com.shousi.mapper.UserAccountMapper;
import org.springframework.stereotype.Service;

/**
* @author 86172
* @description 针对表【user_account(用户账户)】的数据库操作Service实现
* @createDate 2025-05-29 13:48:01
*/
@Service
public class UserAccountServiceImpl extends ServiceImpl<UserAccountMapper, UserAccount>
    implements UserAccountService{

}




