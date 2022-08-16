package com.glx.xuan.activiti.serveractiviti;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RestController;

@RestController
@SpringBootApplication
public class ServerActivitiApplication {

    public static void main(String[] args) {
        SpringApplication.run(ServerActivitiApplication.class, args);
    }

}
