package com.definexjavaspringbootbootcamp.definexgraduationproject.exception;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String message) {
        super(message);
    }
}
