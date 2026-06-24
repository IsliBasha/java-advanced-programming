package com.university.travelbooking;

import com.university.travelbooking.dto.BookingDto;
import com.university.travelbooking.model.Booking;
import com.university.travelbooking.model.BookingStatus;
import com.university.travelbooking.service.BookingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("BookingService Tests")
class BookingServiceTest {

    private BookingService service;

    @BeforeEach
    void setUp() {
        service = new BookingService();
        service.addBooking(new Booking("B001", "C001", "Alice", "Paris",  LocalDate.of(2024, 6, 10), LocalDate.of(2024, 6, 20), 1250.00, BookingStatus.CONFIRMED));
        service.addBooking(new Booking("B002", "C002", "Bob",   "Tokyo",  LocalDate.of(2024, 7, 5),  LocalDate.of(2024, 7, 15), 3400.00, BookingStatus.CONFIRMED));
        service.addBooking(new Booking("B003", "C001", "Alice", "Berlin", LocalDate.of(2024, 8, 1),  LocalDate.of(2024, 8, 8),  800.00,  BookingStatus.PENDING));
    }

    @Test
    @DisplayName("addBooking populates list, set, and map")
    void testAddBookingPopulatesCollections() {
        assertEquals(3, service.getAllBookings().size());
        assertTrue(service.getDestinations().contains("Paris"));
        assertEquals(2, service.getCustomerDestinations().get("C001").size());
    }

    @Test
    @DisplayName("findById returns correct booking")
    void testFindById() {
        assertTrue(service.findById("B002").isPresent());
        assertEquals("Tokyo", service.findById("B002").get().destination());
        assertTrue(service.findById("B999").isEmpty());
    }

    @Test
    @DisplayName("findByDepartureRange filters correctly")
    void testDepartureRange() {
        List<Booking> june = service.findByDepartureRange(LocalDate.of(2024, 6, 1), LocalDate.of(2024, 6, 30));
        assertEquals(1, june.size());
        assertEquals("Paris", june.get(0).destination());
    }

    @Test
    @DisplayName("toBookingDtos maps to DTO records correctly")
    void testToBookingDtos() {
        List<BookingDto> dtos = service.toBookingDtos();
        assertEquals(3, dtos.size());
        assertTrue(dtos.stream().anyMatch(d -> d.customerName().equals("Alice") && d.destination().equals("Paris")));
    }

    @Test
    @DisplayName("searchByDestination does case-insensitive partial match")
    void testSearchByDestination() {
        List<Booking> results = service.searchByDestination("ar");
        assertEquals(1, results.size());
        assertEquals("Paris", results.get(0).destination());
    }

    @Test
    @DisplayName("findByPriceRange returns bookings within bounds")
    void testPriceRange() {
        List<Booking> mid = service.findByPriceRange(800.00, 1300.00);
        assertEquals(2, mid.size());
    }

    @Test
    @DisplayName("computeTotal uses loop and produces correct sum")
    void testComputeTotal() {
        double total = service.computeTotal(service.getAllBookings());
        assertEquals(5450.00, total, 0.01);
    }

    @Test
    @DisplayName("removeBooking removes entry and updates derived collections")
    void testRemove() {
        service.removeBooking("B001");
        assertEquals(2, service.getAllBookings().size());
        assertFalse(service.getDestinations().contains("Paris"));
    }

    @Test
    @DisplayName("Booking rejects negative price")
    void testNegativePrice() {
        assertThrows(IllegalArgumentException.class,
                () -> new Booking("B999", "C001", "Test", "X",
                        LocalDate.now(), LocalDate.now().plusDays(7), -100.0, BookingStatus.PENDING));
    }
}
