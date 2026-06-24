package com.university.travelbookingapi.exception;

/** Thrown when a booking violates a business rule (e.g. return date before departure). */
public class InvalidBookingException extends RuntimeException {
    public InvalidBookingException(String message) {
        super(message);
    }
}
