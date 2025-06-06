package com.shousi;

import com.shousi.result.RetVal;
import com.shousi.vo.UserInfoVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(value = "tingshu-user")
public interface UserFeignClient {
    @GetMapping("api/user/userInfo/getUserById/{userId}")
    public RetVal<UserInfoVo> getUserById(@PathVariable Long userId);
}