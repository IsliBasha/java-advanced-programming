package com.university.fitnessmembershipapi.exception;

/** Thrown when a requested member cannot be found. */
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) { super(message); }
}
