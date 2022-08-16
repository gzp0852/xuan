package com.glx.xuan.serverflowable.entity.process;

import lombok.Data;

/**
 * @Auther rongchunjian
 * @Date 2021/3/25
 */
@Data
public class ConditionGroupListVO {
    private String formId;

    private String operationCode;

    private String type;

    private Object value;

    private String valueName;
}
