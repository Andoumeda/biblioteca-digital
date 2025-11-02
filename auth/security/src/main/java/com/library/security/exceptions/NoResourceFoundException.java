package com.library.security.exceptions;

public class NoResourceFoundException extends RuntimeException {
    public NoResourceFoundException(String message) {
        super(message);
    }
    public NoResourceFoundException() {
        super("Resource not found");
    }
}
