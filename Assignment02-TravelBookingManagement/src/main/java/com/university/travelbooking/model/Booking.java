package com.university.travelbooking.model;

import java.time.LocalDate;

/**
 * Represents a travel booking.
 * Immutable record — use {@code withStatus} for status updates.
 */
public record Booking(
        String bookingId,
        String customerId,
        String customerName,
        String destination,
        LocalDate departureDate,
        LocalDate returnDate,
        double price,
        BookingStatus status
) {
    public Booking {
        if (price < 0) throw new IllegalArgumentException("Price cannot be negative.");
    }

    public Booking withStatus(BookingStatus newStatus) {
        return new Booking(bookingId, customerId, customerName, destination, departureDate, returnDate, price, newStatus);
    }
}
