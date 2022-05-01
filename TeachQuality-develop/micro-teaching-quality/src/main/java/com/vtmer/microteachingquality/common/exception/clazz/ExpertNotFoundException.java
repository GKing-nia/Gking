package com.vtmer.microteachingquality.common.exception.clazz;

public class ExpertNotFoundException extends ClazzException {

    public ExpertNotFoundException() {
        super("expert.not.found", null);
    }
}
