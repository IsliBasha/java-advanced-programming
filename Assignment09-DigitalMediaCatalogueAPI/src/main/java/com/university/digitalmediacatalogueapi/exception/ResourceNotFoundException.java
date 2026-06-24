package com.university.digitalmediacatalogueapi.exception;

/** Thrown when a requested media item cannot be found. */
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) { super(message); }
}
