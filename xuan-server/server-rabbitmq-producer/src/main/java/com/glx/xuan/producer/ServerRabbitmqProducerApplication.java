package com.glx.xuan.producer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
//(exclude = {DruidDataSourceAutoConfigure.class})
//@EnableDiscoveryClient
//@ComponentScan(value = {"com.glx.xuan"})
//@EnableFeignClients(basePackages = {"com.glx.xuan"})
public class ServerRabbitmqProducerApplication {

    public static void main(String[] args) {
        SpringApplication.run(ServerRabbitmqProducerApplication.class, args);
    }


}
