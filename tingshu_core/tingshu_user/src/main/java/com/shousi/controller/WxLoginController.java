package com.shousi.controller;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.bean.WxMaJscode2SessionResult;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.shousi.constant.RedisConstant;
import com.shousi.entity.UserInfo;
import com.shousi.result.RetVal;
import com.shousi.service.UserInfoService;
import me.chanjar.weixin.common.error.WxErrorException;
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

@RestController
@RequestMapping("/api/user/wxLogin")
public class WxLoginController {

    @Autowired
    private WxMaService wxMaService;

    @Autowired
    private UserInfoService userInfoService;

    @Autowired
    private RedisTemplate<Object, Object> redisTemplate;

    @GetMapping("/wxLogin/{code}")
    public RetVal<?> wxLogin(@PathVariable String code) throws WxErrorException {
        // 获取微信登录信息
        WxMaJscode2SessionResult sessionInfo = wxMaService.getUserService().getSessionInfo(code);
        // 获取openid
        String openid = sessionInfo.getOpenid();

        // 根据openid判断数据库中是否有该用户
        LambdaQueryWrapper<UserInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserInfo::getWxOpenId, openid);
        UserInfo userInfo = userInfoService.getOne(queryWrapper);
        if (userInfo == null) {
            userInfo = new UserInfo();
            userInfo.setWxOpenId(openid);
            userInfo.setNickname("听书" + System.currentTimeMillis());
            userInfo.setAvatarUrl("https://shousi.oss-cn-beijing.aliyuncs.com/shousi2.png");
            userInfo.setIsVip(0);
            userInfoService.save(userInfo);
        }
        String uuid = UUID.randomUUID().toString().replaceAll("-", "");
        String key = RedisConstant.USER_LOGIN_KEY_PREFIX + uuid;
        redisTemplate.opsForValue().set(key, userInfo, RedisConstant.USER_LOGIN_KEY_TIMEOUT, TimeUnit.SECONDS);
        Map<String, Object> map = new HashMap<>();
        map.put("token", uuid);
        return RetVal.ok(map);
    }
}
