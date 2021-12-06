package com.glx.xuan.auth.controller;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Info;
import io.swagger.annotations.SwaggerDefinition;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author gzp
 * @date 2021/12/2
 */
@RestController
@RequestMapping(path = "/auth")
@SwaggerDefinition(info = @Info(description = "身份认证、鉴权等管理", title = "认证、鉴权管理接口", version = "v1"), basePath = "/auth")
public class AuthController {


    @ApiOperation(value = "yes", notes = "yess")
    @GetMapping("/test")
    public String getCustomerList(){
        return "yes";
    }
}
