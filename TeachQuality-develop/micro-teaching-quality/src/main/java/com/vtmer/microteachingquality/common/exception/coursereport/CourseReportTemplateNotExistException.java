package com.vtmer.microteachingquality.common.exception.coursereport;

public class CourseReportTemplateNotExistException extends CourseReportException {
    private static final long serialVersionUID = 1L;

    public CourseReportTemplateNotExistException() {
        super("coursereport.template.not.exist", null);
    }

    public CourseReportTemplateNotExistException(String defaultMessage) {
        super("coursereport.template.not.exist", defaultMessage);
    }
}
