package com.glx.xuan.serverflowable.entity.process;

import lombok.Data;

import java.util.List;

/**
 * @Auther rongchunjian
 * @Date 2021/3/25
 */

@Data
public class FieldVO {

    private List<String> readable;

    private List<String> writable;
}
