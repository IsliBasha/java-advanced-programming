package com.university.travelbookingapi.exception;

/** Thrown when a requested booking cannot be found. */
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) { super(message); }
}
