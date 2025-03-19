package com.definexjavaspringbootbootcamp.definexgraduationproject.exception;

public class TaskNotFoundException extends RuntimeException {
    public TaskNotFoundException(String message) {
        super(message);
    }
}
