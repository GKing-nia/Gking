package com.vtmer.microteachingquality.common.exception.clazz;

import com.vtmer.microteachingquality.common.exception.base.BaseException;

public class ClazzException extends BaseException {
    private static final long serialVersionUID = 1L;

    public ClazzException(String code, Object[] args) {
        super("clazz", code, args, null);
    }
}
