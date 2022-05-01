package com.vtmer.microteachingquality.common.exception.user;

public class UserNotMatchException extends UserException {

    private static final long serialVersionUID = 1L;

    public UserNotMatchException() {
        super("user.not.match", null);
    }

}
