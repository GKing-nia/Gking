package com.vtmer.microteachingquality.common.exception.coursereport;

public class CourseReportTemplateFailUploadException extends CourseReportException {
    private static final long serialVersionUID = 1L;

    public CourseReportTemplateFailUploadException() {
        super("coursereport.template.fail.upload", null);
    }

    public CourseReportTemplateFailUploadException(String defaultMessage) {
        super("coursereport.template.fail.upload", defaultMessage);
    }
}
