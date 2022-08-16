package com.glx.xuan.serverflowable.controller;

import com.glx.xuan.base.common.model.CommonResult;
import com.glx.xuan.serverflowable.service.ProcessService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * @author gaozepeng
 * @since 2022/7/25
 */
@RestController
@RequestMapping(path = "/process")
@Api(tags = "流程操作")
public class ProcessController {

    @Autowired
    private ProcessService processService;

    @ApiOperation(value = "deploy", notes = "部署")
    @RequestMapping(value = "/deploy")
    public CommonResult<Map> deployByXML(String xmlName) throws Exception {
        Map deplotMap = processService.deployByXML(xmlName);
        return CommonResult.success(deplotMap);
    }


    @ApiOperation(value = "startProcess", notes = "开始流程")
    @RequestMapping(value = "/startProcess")
    public CommonResult<Map> startProcess(String processKey) throws Exception {
        Map result = processService.startProcess(processKey);
        return CommonResult.success(result);
    }
}
