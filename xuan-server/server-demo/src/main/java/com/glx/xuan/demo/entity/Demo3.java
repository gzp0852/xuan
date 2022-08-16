package com.glx.xuan.demo.entity;

import lombok.Data;

/**
 * @author gaozepeng
 * @since 2022/7/19
 */
@Data
public class Demo3 {
    private String name;

    public Demo3() {
    }

    public Demo3(String name) {
        this.name = name;
    }
}
