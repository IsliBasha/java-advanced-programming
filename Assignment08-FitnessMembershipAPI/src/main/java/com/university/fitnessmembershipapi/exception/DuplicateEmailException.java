package com.university.fitnessmembershipapi.exception;

/** Thrown when a member would reuse an email that already exists. */
public class DuplicateEmailException extends RuntimeException {
    public DuplicateEmailException(String message) { super(message); }
}
