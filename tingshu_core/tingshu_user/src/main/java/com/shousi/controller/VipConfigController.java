package com.shousi.controller;

import com.shousi.entity.VipServiceConfig;
import com.shousi.result.RetVal;
import com.shousi.service.VipServiceConfigService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <p>
 * 用户 前端控制器
 * </p>
 *
 * @author 强哥
 * @since 2023-11-28
 */
@Tag(name = "vip服务管理接口")
@RestController
@RequestMapping("api/user/vipConfig")
public class VipConfigController {
    @Autowired
    private VipServiceConfigService vipServiceConfigService;

    @Operation(summary = "获取所有的VIP配置")
    @GetMapping("findAllVipConfig")
    public RetVal<List<VipServiceConfig>> findAllVipConfig() {
        List<VipServiceConfig> list = vipServiceConfigService.list();
        return RetVal.ok(list);
    }

}
