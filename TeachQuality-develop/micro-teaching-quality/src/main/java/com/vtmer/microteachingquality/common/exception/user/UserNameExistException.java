package com.vtmer.microteachingquality.common.exception.user;

public class UserNameExistException extends UserException {

    private static final long serialVersionUID = 1L;

    public UserNameExistException() {
        super("user.name.exist", null);
    }

}
