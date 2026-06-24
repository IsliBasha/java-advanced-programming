package com.university.travelbookingapi.service;

import com.university.travelbookingapi.dto.BookingRequestDto;
import com.university.travelbookingapi.dto.BookingResponseDto;
import com.university.travelbookingapi.entity.BookingStatus;
import com.university.travelbookingapi.exception.InvalidBookingException;
import com.university.travelbookingapi.exception.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/** Cross-layer integration tests for the booking service backed by H2. */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
class BookingServiceTest {

    @Autowired
    private BookingService bookingService;

    private BookingRequestDto sampleRequest(String customer, String destination, BookingStatus status) {
        return new BookingRequestDto(customer, destination,
                LocalDate.of(2026, 7, 1), LocalDate.of(2026, 7, 10),
                new BigDecimal("1200.00"), status, 2);
    }

    @Test
    void create_persistsAndReturnsBookingWithId() {
        BookingResponseDto created = bookingService.create(sampleRequest("Alice", "Paris", BookingStatus.PENDING));
        assertNotNull(created.id());
        assertEquals("Alice", created.customerName());
        assertEquals("Paris", created.destination());
    }

    @Test
    void create_returnBeforeDeparture_throwsInvalidBooking() {
        BookingRequestDto bad = new BookingRequestDto("Bob", "Rome",
                LocalDate.of(2026, 7, 10), LocalDate.of(2026, 7, 1),
                new BigDecimal("900.00"), BookingStatus.PENDING, 1);
        assertThrows(InvalidBookingException.class, () -> bookingService.create(bad));
    }

    @Test
    void findById_existing_returnsBooking() {
        BookingResponseDto created = bookingService.create(sampleRequest("Carol", "Tokyo", BookingStatus.CONFIRMED));
        BookingResponseDto found = bookingService.findById(created.id());
        assertEquals(created.id(), found.id());
        assertEquals("Tokyo", found.destination());
    }

    @Test
    void findById_missing_throwsResourceNotFound() {
        assertThrows(ResourceNotFoundException.class, () -> bookingService.findById(99999L));
    }

    @Test
    void findAll_noFilters_returnsAll() {
        bookingService.create(sampleRequest("D", "Cairo", BookingStatus.PENDING));
        bookingService.create(sampleRequest("E", "Lima", BookingStatus.CONFIRMED));
        List<BookingResponseDto> all = bookingService.findAll(null, null);
        assertTrue(all.size() >= 2);
    }

    @Test
    void findAll_filterByStatus_returnsOnlyMatching() {
        bookingService.create(sampleRequest("F", "Oslo", BookingStatus.CANCELLED));
        List<BookingResponseDto> cancelled = bookingService.findAll(null, BookingStatus.CANCELLED);
        assertFalse(cancelled.isEmpty());
        assertTrue(cancelled.stream().allMatch(b -> b.status() == BookingStatus.CANCELLED));
    }

    @Test
    void findAll_filterByDestination_partialAndCaseInsensitive() {
        bookingService.create(sampleRequest("G", "Barcelona", BookingStatus.PENDING));
        List<BookingResponseDto> result = bookingService.findAll("barce", null);
        assertTrue(result.stream().anyMatch(b -> b.destination().equals("Barcelona")));
    }

    @Test
    void findAll_filterByDestinationAndStatus() {
        bookingService.create(sampleRequest("H", "Madrid", BookingStatus.CONFIRMED));
        bookingService.create(sampleRequest("I", "Madrid", BookingStatus.PENDING));
        List<BookingResponseDto> result = bookingService.findAll("madrid", BookingStatus.CONFIRMED);
        assertFalse(result.isEmpty());
        assertTrue(result.stream().allMatch(b ->
                b.status() == BookingStatus.CONFIRMED && b.destination().equalsIgnoreCase("Madrid")));
    }

    @Test
    void update_modifiesAllEditableFields() {
        BookingResponseDto created = bookingService.create(sampleRequest("J", "Berlin", BookingStatus.PENDING));
        BookingRequestDto updated = new BookingRequestDto("J Updated", "Munich",
                LocalDate.of(2026, 8, 1), LocalDate.of(2026, 8, 5),
                new BigDecimal("1500.00"), BookingStatus.CONFIRMED, 3);
        BookingResponseDto result = bookingService.update(created.id(), updated);
        assertEquals("J Updated", result.customerName());
        assertEquals("Munich", result.destination());
        assertEquals(BookingStatus.CONFIRMED, result.status());
        assertEquals(3, result.numberOfTravellers());
    }

    @Test
    void delete_removesBooking() {
        BookingResponseDto created = bookingService.create(sampleRequest("K", "Dublin", BookingStatus.PENDING));
        bookingService.delete(created.id());
        assertThrows(ResourceNotFoundException.class, () -> bookingService.findById(created.id()));
    }
}
