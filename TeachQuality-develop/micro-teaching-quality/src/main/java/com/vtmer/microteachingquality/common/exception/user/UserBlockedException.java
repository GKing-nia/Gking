package com.vtmer.microteachingquality.common.exception.user;

public class UserBlockedException extends UserException {

    private static final long serialVersionUID = 1L;

    public UserBlockedException() {
        super("user.blocked", null);
    }
}
