package com.shousi.controller;

import com.shousi.entity.BaseAttribute;
import com.shousi.login.TingShuLogin;
import com.shousi.result.RetVal;
import com.shousi.service.BaseAttributeService;
import com.shousi.service.BaseCategory1Service;
import com.shousi.service.BaseCategoryViewService;
import com.shousi.vo.CategoryVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "并发测试")
@RestController
@RequestMapping("/api/album/con")
public class ConCurrentController {

    @Autowired
    private BaseCategory1Service baseCategory1Service;

    @GetMapping("setNum")
    public String setNum(){
        baseCategory1Service.setNum();
        return "success";
    }
}
