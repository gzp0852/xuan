package com.glx.xuan.base.common.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 通用返回对象
 */
@ApiModel(value = "公共返回结果类")
public class CommonResult<T> {
    @ApiModelProperty(value = "返回编码")
    private long code;
    @ApiModelProperty(value = "返回信息")
    private String message;
    @ApiModelProperty(value = "返回结果")
    private T data;

    protected CommonResult() {
    }

    protected CommonResult(long code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    /**
     * 成功返回结果
     *
     * @param data 获取的数据
     */
    public static <T> CommonResult<T> success(T data) {
        return new CommonResult<T>(ResultCode.SUCCESS.getCode(), ResultCode.SUCCESS.getMessage(), data);
    }

    /**
     * 成功返回结果
     *
     * @param errorCode 获取的编码
     * @param data 获取的数据
     * @param  message 提示信息
     */
    public static <T> CommonResult<T> success(IErrorCode errorCode, String message,T data) {
        return new CommonResult<T>(errorCode.getCode(), message, data);
    }

    /**
     * 成功返回结果
     *
     * @param errorCode 获取的数据
     * @param  message 提示信息
     */
    public static <T> CommonResult<T> success(IErrorCode errorCode, String message ) {
        return new CommonResult<T>(errorCode.getCode(), message,null);
    }

    /**
     * 失败返回结果
     * @param errorCode 错误码
     */
    public static <T> CommonResult<T> failed(IErrorCode errorCode) {
        return new CommonResult<T>(errorCode.getCode(), errorCode.getMessage(), null);
    }

    /**
     * 失败返回结果
     * @param errorCode 错误码
     * @param message 错误信息
     */
    public static <T> CommonResult<T> failed(IErrorCode errorCode,String message) {
        return new CommonResult<T>(errorCode.getCode(), message, null);
    }

    /**
     * 失败返回结果
     * @param message 提示信息
     */
    public static <T> CommonResult<T> failed(String message) {
        return new CommonResult<T>(ResultCode.FAILED.getCode(), message, null);
    }

    /**
     * 失败返回结果
     */
    public static <T> CommonResult<T> failed() {
        return failed(ResultCode.FAILED);
    }


    /**
     * 失败返回结果
     *
     * @param errorCode 获取的编码
     * @param data 获取的数据
     * @param  message 提示信息
     */
    public static <T> CommonResult<T> failed(IErrorCode errorCode, String message,T data) {
        return new CommonResult<T>(errorCode.getCode(), message, data);
    }

    public static <T> CommonResult<T> of(IErrorCode errorCode, String message ,T data) {
        return new CommonResult<T>(errorCode.getCode(), message, data);
    }



    public long getCode() {
        return code;
    }

    public void setCode(long code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
