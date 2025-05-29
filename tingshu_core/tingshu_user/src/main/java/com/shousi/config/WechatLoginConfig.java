package com.shousi.config;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.api.impl.WxMaServiceImpl;
import cn.binarywang.wx.miniapp.config.impl.WxMaDefaultConfigImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WechatLoginConfig {

    @Autowired
    private WechatProperties wechatProperties;

    @Bean
    public WxMaService wxMaService() {
        // 设置微信小程序的相关配置
        WxMaDefaultConfigImpl wxMaConfig = new WxMaDefaultConfigImpl();
        wxMaConfig.setAppid(wechatProperties.getAppId());
        wxMaConfig.setSecret(wechatProperties.getAppSecret());
        wxMaConfig.setMsgDataFormat("JSON");

        WxMaServiceImpl wxMaService = new WxMaServiceImpl();
        wxMaService.setWxMaConfig(wxMaConfig);
        return wxMaService;
    }
}
