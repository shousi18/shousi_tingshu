package com.shousi.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "wechat.login")
public class WechatProperties {

    /**
     * appId
     */
    private String appId;

    /**
     * appSecret
     */
    private String appSecret;
}
