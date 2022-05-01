package com.vtmer.microteachingquality.common.exception.clazz;

public class ClazzFileNotExistException extends ClazzException {

    private static final long serialVersionUID = 1L;

    public ClazzFileNotExistException() {
        super("clazz.file.not.exist", null);
    }

}
