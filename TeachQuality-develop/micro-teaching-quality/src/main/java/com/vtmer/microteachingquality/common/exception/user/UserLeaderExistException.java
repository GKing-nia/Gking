package com.vtmer.microteachingquality.common.exception.user;

public class UserLeaderExistException extends UserException {

    private static final long serialVersionUID = 1L;

    public UserLeaderExistException() {
        super("user.leader.exist", null);
    }
}
