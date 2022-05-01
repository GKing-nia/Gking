package com.vtmer.microteachingquality.common.exception.coursereport;

import com.vtmer.microteachingquality.common.exception.base.BaseException;

public class CourseReportException extends BaseException {
    private static final long serialVersionUID = 1L;


    public CourseReportException(String code, String defaultMessage) {
        super("coursereport", code, null, defaultMessage);
    }


}
