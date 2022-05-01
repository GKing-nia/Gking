package com.vtmer.microteachingquality.common.exception.report;

public class ReportEvaluationNotExistException extends ReportException {

    private static final long serialVersionUID = 1L;

    public ReportEvaluationNotExistException() {
        super("report.evaluation.not.exist", null);
    }
}
