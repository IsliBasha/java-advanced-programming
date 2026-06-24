package com.university.travelbookingapi.dto;

import com.university.travelbookingapi.entity.BookingStatus;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;

/** Request DTO for creating or updating a booking. */
public record BookingRequestDto(
        @NotBlank(message = "Customer name is required") String customerName,
        @NotBlank(message = "Destination is required") String destination,
        @NotNull(message = "Departure date is required") LocalDate departureDate,
        @NotNull(message = "Return date is required") LocalDate returnDate,
        @NotNull @DecimalMin(value = "0.0", message = "Price cannot be negative") BigDecimal price,
        @NotNull(message = "Status is required") BookingStatus status,
        @NotNull @Positive(message = "Travellers must be positive") Integer numberOfTravellers
) {}
