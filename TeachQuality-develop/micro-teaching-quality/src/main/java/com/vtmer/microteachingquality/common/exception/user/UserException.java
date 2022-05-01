package com.vtmer.microteachingquality.common.exception.user;

import com.vtmer.microteachingquality.common.exception.base.BaseException;

public class UserException extends BaseException {

    private static final long serialVersionUID = 1L;

    public UserException(String code, Object[] args) {
        super("user", code, args, null);
    }
}
