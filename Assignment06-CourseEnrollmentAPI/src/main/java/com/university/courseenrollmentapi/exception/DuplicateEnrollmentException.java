package com.university.courseenrollmentapi.exception;

/** Thrown when a student already has an ACTIVE enrollment for the same course. */
public class DuplicateEnrollmentException extends RuntimeException {
    public DuplicateEnrollmentException(String message) {
        super(message);
    }
}
