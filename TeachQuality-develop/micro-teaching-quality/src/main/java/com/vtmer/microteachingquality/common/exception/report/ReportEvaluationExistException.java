package com.vtmer.microteachingquality.common.exception.report;

public class ReportEvaluationExistException extends ReportException {

    private static final long serialVersionUID = 1L;

    public ReportEvaluationExistException() {
        super("report.evaluation.exist", null);
    }
}
