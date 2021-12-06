package com.glx.xuan;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author gzp
 * @date 2021/11/26
 */
@SpringBootApplication(scanBasePackages = {"com"})
@EnableDiscoveryClient
@ComponentScan(basePackages = {"com.glx.xuan"})
@EnableFeignClients(basePackages = {"com.glx.xuan"})
public class GatewayApplication {
    public static void main(String[] args) {
        SpringApplication.run(GatewayApplication.class,args);
    }
}