package com.glx.xuan.serverflowable.controller;

import com.glx.xuan.serverflowable.service.TestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;


@RestController
public class TestController {

    @Autowired
    private TestService testService;

    @RequestMapping(value = "processDiagram")
    public void genProcessDiagram(HttpServletResponse httpServletResponse, String processId) throws Exception {
        testService.genProcessDiagram(httpServletResponse,processId);
    }

    @GetMapping(value = "test")
    public String test() {
        return "ok";
    }
}
