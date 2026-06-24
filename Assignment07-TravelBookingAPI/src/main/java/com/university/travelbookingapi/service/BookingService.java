package com.university.travelbookingapi.service;

import com.university.travelbookingapi.dto.BookingRequestDto;
import com.university.travelbookingapi.dto.BookingResponseDto;
import com.university.travelbookingapi.entity.Booking;
import com.university.travelbookingapi.entity.BookingStatus;
import com.university.travelbookingapi.exception.InvalidBookingException;
import com.university.travelbookingapi.exception.ResourceNotFoundException;
import com.university.travelbookingapi.repository.BookingRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

/**
 * Business logic for travel bookings.
 *
 * <p>All rules (date validation, lookups, persistence) live here so that the
 * controller stays a thin HTTP adapter with no business decisions of its own.
 */
@Service
@Transactional
public class BookingService {

    private final BookingRepository bookingRepository;

    public BookingService(BookingRepository bookingRepository) {
        this.bookingRepository = bookingRepository;
    }

    /** Creates a new booking after validating the travel period. */
    public BookingResponseDto create(BookingRequestDto request) {
        validatePeriod(request.departureDate(), request.returnDate());
        Booking booking = new Booking(
                request.customerName(),
                request.destination(),
                request.departureDate(),
                request.returnDate(),
                request.price(),
                request.status(),
                request.numberOfTravellers());
        return BookingResponseDto.from(bookingRepository.save(booking));
    }

    /** Looks up a single booking or throws if it does not exist. */
    @Transactional(readOnly = true)
    public BookingResponseDto findById(Long id) {
        return BookingResponseDto.from(getOrThrow(id));
    }

    /**
     * Returns bookings, optionally narrowed by destination (partial, case-insensitive)
     * and/or status. Any combination of the two filters is supported.
     */
    @Transactional(readOnly = true)
    public List<BookingResponseDto> findAll(String destination, BookingStatus status) {
        List<Booking> bookings;
        if (destination != null && status != null) {
            bookings = bookingRepository.findByDestinationContainingIgnoreCaseAndStatus(destination, status);
        } else if (destination != null) {
            bookings = bookingRepository.findByDestinationContainingIgnoreCase(destination);
        } else if (status != null) {
            bookings = bookingRepository.findByStatus(status);
        } else {
            bookings = bookingRepository.findAll();
        }
        return bookings.stream().map(BookingResponseDto::from).toList();
    }

    /** Replaces all editable fields of an existing booking. */
    public BookingResponseDto update(Long id, BookingRequestDto request) {
        validatePeriod(request.departureDate(), request.returnDate());
        Booking booking = getOrThrow(id);
        booking.setCustomerName(request.customerName());
        booking.setDestination(request.destination());
        booking.setDepartureDate(request.departureDate());
        booking.setReturnDate(request.returnDate());
        booking.setPrice(request.price());
        booking.setStatus(request.status());
        booking.setNumberOfTravellers(request.numberOfTravellers());
        return BookingResponseDto.from(bookingRepository.save(booking));
    }

    /** Deletes a booking, throwing if the id is unknown. */
    public void delete(Long id) {
        bookingRepository.delete(getOrThrow(id));
    }

    private Booking getOrThrow(Long id) {
        return bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found: id=" + id));
    }

    private void validatePeriod(LocalDate departureDate, LocalDate returnDate) {
        if (returnDate.isBefore(departureDate)) {
            throw new InvalidBookingException("Return date cannot be before departure date.");
        }
    }
}
