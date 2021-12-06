package com.glx.xuan.demo.controller;

import com.glx.xuan.base.common.model.CommonResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * @author gzp
 * @date 2021/12/2
 */

@RestController
@RequestMapping(path = "/demo")
@Api(tags = "例子")
public class DemoController {


    @ApiOperation(value = "yes", notes = "yess")
    @GetMapping("/demo1")
    public String getCustomerList(){
        return "yes";
    }
}
