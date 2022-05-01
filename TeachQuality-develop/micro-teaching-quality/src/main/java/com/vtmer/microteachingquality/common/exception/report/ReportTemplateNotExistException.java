package com.vtmer.microteachingquality.common.exception.report;

public class ReportTemplateNotExistException extends ReportException {

    private static final long serialVersionUID = 1L;

    public ReportTemplateNotExistException() {
        super("report.template.not.exist", null);
    }

}
