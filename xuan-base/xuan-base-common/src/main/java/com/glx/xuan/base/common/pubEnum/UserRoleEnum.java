package com.glx.xuan.base.common.pubEnum;

/**
 * UserRoleEnum 枚举
 */
public enum UserRoleEnum {



    /**
     * 产品运营人员
     */
    CPYYRY("产品运营人员角色"),
    /**
     * 平台运营人员
     */
    PTYYRY("平台运营人员角色");

    private String value;

    UserRoleEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
