package com.glx.xuan.demo.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.Synchronized;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author gzp
 * @date 2021/12/2
 */

@RestController
@RequestMapping(path = "/demo")
@Api(tags = "例子")
@Slf4j
public class DemoController {

    @Resource(name = "bpmTaskExecutor")
    private Executor executor;

    @ApiOperation(value = "yes", notes = "yess")
    @GetMapping("/demo1")
    public String getCustomerList() {
        return "yes";
    }


    private volatile AtomicInteger num = new AtomicInteger(0);

    @ApiOperation(value = "yes2", notes = "yess2")
    @GetMapping("/demo2")
    public void getDemo2() {
        for (int i = 0; i < 10; i++) {
            int finalI = i;
            executor.execute(() -> {
                try {
                    dealLeaveProcess(num.incrementAndGet(), finalI);
                } catch (Exception e) {
                    log.info(e.toString());
                }
            });
        }
    }

    @Synchronized
    public void dealLeaveProcess(int i, int start) throws InterruptedException {
        System.out.println("thread===> " + Thread.currentThread().getName() + "--------" + i + "--------" + start);
//        int a = 0 / 0;
        Thread.sleep(1000);
    }

}
