package com.definexjavaspringbootbootcamp.definexgraduationproject.exception;

public class ProjectNotFoundException extends RuntimeException {
    public ProjectNotFoundException(String message) {
        super(message);
    }
}
