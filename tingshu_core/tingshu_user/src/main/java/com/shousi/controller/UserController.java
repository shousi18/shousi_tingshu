package com.shousi.controller;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.bean.WxMaJscode2SessionResult;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.shousi.constant.RedisConstant;
import com.shousi.entity.UserInfo;
import com.shousi.login.TingShuLogin;
import com.shousi.result.RetVal;
import com.shousi.service.UserInfoService;
import com.shousi.util.AuthContextHolder;
import com.shousi.vo.UserInfoVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import me.chanjar.weixin.common.error.WxErrorException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Tag(name = "微信登录管理")
@RestController
@RequestMapping("/api/user/userInfo")
public class UserController {

    @Autowired
    private UserInfoService userInfoService;

    @Operation(summary = "获取用户个人信息")
    @GetMapping("getUserById/{userId}")
    public RetVal<UserInfoVo> getUserById(@PathVariable Long userId)  {
        UserInfo userInfo = userInfoService.getById(userId);
        UserInfoVo userInfoVo = new UserInfoVo();
        if(userInfo!=null){
            BeanUtils.copyProperties(userInfo,userInfoVo);
        }
        return RetVal.ok(userInfoVo);
    }
}
