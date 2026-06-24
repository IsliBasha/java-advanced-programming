package com.university.travelbookingapi.dto;

import com.university.travelbookingapi.entity.Booking;
import com.university.travelbookingapi.entity.BookingStatus;

import java.math.BigDecimal;
import java.time.LocalDate;

/** Response DTO for all booking endpoints. */
public record BookingResponseDto(
        Long id, String customerName, String destination,
        LocalDate departureDate, LocalDate returnDate,
        BigDecimal price, BookingStatus status, Integer numberOfTravellers
) {
    public static BookingResponseDto from(Booking b) {
        return new BookingResponseDto(b.getId(), b.getCustomerName(), b.getDestination(),
                b.getDepartureDate(), b.getReturnDate(), b.getPrice(), b.getStatus(), b.getNumberOfTravellers());
    }
}
