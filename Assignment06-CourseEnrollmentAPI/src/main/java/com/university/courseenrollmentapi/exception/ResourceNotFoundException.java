package com.university.courseenrollmentapi.exception;

/** Thrown when a requested enrollment record does not exist. */
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
