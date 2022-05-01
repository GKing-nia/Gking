package com.vtmer.microteachingquality.common.exception.report;

import com.vtmer.microteachingquality.common.exception.base.BaseException;

public class ReportException extends BaseException {
    private static final long serialVersionUID = 1L;

    public ReportException(String code, Object[] args) {
        super("report", code, args, null);
    }
}
