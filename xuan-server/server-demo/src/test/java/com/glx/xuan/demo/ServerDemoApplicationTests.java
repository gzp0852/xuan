package com.glx.xuan.demo;

import com.glx.xuan.demo.entity.Demo;
import com.glx.xuan.demo.entity.Demo1;
import com.glx.xuan.demo.entity.Demo2;
import com.glx.xuan.demo.entity.Demo3;
import org.apache.commons.lang3.ObjectUtils;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

@SpringBootTest
class ServerDemoApplicationTests {

    @Test
    void contextLoads() {
        Demo demo = new Demo();
        demo.setDemo1(new Demo1());
        demo.setDemo2(new Demo2());
        demo.setDemo3(new Demo3());
//        demo.setDemo3(new Demo3("aaa"));

        Optional<Object> flag = Optional.of(demo)
                .map(s -> ObjectUtils.isEmpty(s.getDemo1())
                        && ObjectUtils.isEmpty(s.getDemo2())
                        && ObjectUtils.isEmpty(s.getDemo3()));


        System.out.println(flag);
    }

}
