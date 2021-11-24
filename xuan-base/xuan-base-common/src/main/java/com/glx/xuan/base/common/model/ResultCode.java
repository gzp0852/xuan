package com.glx.xuan.base.common.model;

/**
 * 枚举了一些常用API操作码
 * Created by macro on 2019/4/19.
 */
public enum ResultCode implements IErrorCode {
    /**
     * 返回成功
     * */
    SUCCESS(1000, "返回成功"),
    /**
     * 数据处理成功
     * */
    SJCL_SUCCESS(1001,"数据处理成功"),
    /**
     * 上传文件成功
     * */
    WJSC_SUCCESS(1002,"上传文件成功"),
    /**
     * 数据模板导入成功
     * */
    MBDR_SUCCESS(1005,"数据模板导入成功"),
    /**
     * 数据处理失败
     * */
    SJCL_FAILED(-1001,"数据处理失败"),
    /**
     * 上传文件失败
     * */
    WJSC_FAILED(-1002,"上传文件失败"),
    /**
     * 导出文件成功
     * */
    DCWJ_FAILED(-1003,"导出文件失败"),
    /**
     * token验证失败
     * */
    TOKEN_FAILED(-1004,"token验证失败"),
    /**
     * token验证成功
     * */
    TOKEN_SUCCESS(1004,"token验证成功"),
    /**
     * 数据模板导入失败
     * */
    SJDR_FAILED(-1005,"数据模板导入失败"),
    /**
     * 返回失败
     * */
    FAILED(-1000, "返回失败"),
    /**
     * 参数校验错误
     * */
    VALIDATE_FAILED(-1007, "参数校验错误"),
    /**
     * 登录失败
     * */
    LOGIN_FAILED(-1006,"登录失败"),
    /**
     * 登录成功
     * */
    LOGIN_SUCCESS(1006,"登录成功"),

    /**
     * 资源为空
     * */
    RESOURCES_SUCCESS(1009,"无访问权限"),

    /**
     * 资源为空
     * */
    NOT_ALLOWED(-1009,"无使用权限"),


    /**
     * 请稍后再试
     * */
    BLOCK_HANDLER(-9999,"请稍后再试"),

    /**
     * 异常访问
     * */
    FALLBACK(-9998,"异常访问"),

    /**
     * 统一异常返回码
     * */
    EXCEPTION_HANDLER(2500,"服务异常，请联系管理员");

    private long code;
    private String message;

    private ResultCode(long code, String message) {
        this.code = code;
        this.message = message;
    }

    public long getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
