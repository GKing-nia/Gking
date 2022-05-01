package com.hung.microoauth2commons.commonutils.api;

/**
 * 封装api错误码
 *
 * @author Hung
 * @date 2021/11/2 23:51
 */
public interface IErrorCode {
    /**
     * 获取状态码
     *
     * @return 状态码
     */
    long getCode();

    /**
     * 获取状态信息
     *
     * @return 状态信息
     */
    String getMessage();
}
