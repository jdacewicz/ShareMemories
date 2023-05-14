package com.sharememories.sharememories.exception;

public class NotAuthenticatedException extends RuntimeException {

    public NotAuthenticatedException() {
        super("User not authenticated.");
    }
}
