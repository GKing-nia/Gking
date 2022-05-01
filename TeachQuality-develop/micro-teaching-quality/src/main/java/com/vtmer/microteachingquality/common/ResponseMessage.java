package com.vtmer.microteachingquality.common;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 统一返回结果集
 *
 * @param <T>
 * @author Hung
 */
@ApiModel("统一返回结果集(json)")
public class ResponseMessage<T> {
    // 成功返回状态码
    public static final int STATUS_SUCCESS = 200;
    // 失败返回状态码
    public static final int STATUS_ERROR = 400;

    @ApiModelProperty(value = "状态码")
    private int code;
    @ApiModelProperty(value = "数据对象")
    private T data;
    @ApiModelProperty(value = "描述")
    private String message;

    public ResponseMessage(int code, T data, String message) {
        this.code = code;
        this.data = data;
        this.message = message;
    }

    public static <T> ResponseMessage<T> newSuccessInstance(T data, String message) {
        return new ResponseMessage<>(ResponseMessage.STATUS_SUCCESS, data, message);
    }

    public static <T> ResponseMessage<T> newSuccessInstance(String message) {
        return new ResponseMessage<>(ResponseMessage.STATUS_SUCCESS, null, message);
    }

    public static <T> ResponseMessage<T> newSuccessInstance(T data) {
        return new ResponseMessage<>(ResponseMessage.STATUS_SUCCESS, data, null);
    }

    public static <T> ResponseMessage<T> newErrorInstance(String message) {
        return new ResponseMessage<>(ResponseMessage.STATUS_ERROR, null, message);
    }

    public static <T> ResponseMessage<T> newErrorInstance(T data, int status) {
        return new ResponseMessage<>(status, data, null);
    }

    public static int getStatusSuccess() {
        return STATUS_SUCCESS;
    }

    public static int getStatusError() {
        return STATUS_ERROR;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}