package com.university.travelbooking.service;

import com.university.travelbooking.dto.BookingDto;
import com.university.travelbooking.model.Booking;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Manages travel bookings with List, Set, Map, and stream operations.
 */
public class BookingService {

    private final List<Booking> bookings = new ArrayList<>();
    private final Set<String> destinations = new HashSet<>();
    private final Map<String, Set<String>> customerDestinations = new HashMap<>();

    // ── CRUD ─────────────────────────────────────────────────────────────

    public void addBooking(Booking booking) {
        bookings.add(booking);
        destinations.add(booking.destination());
        customerDestinations
                .computeIfAbsent(booking.customerId(), k -> new HashSet<>())
                .add(booking.destination());
    }

    public boolean removeBooking(String bookingId) {
        boolean removed = bookings.removeIf(b -> b.bookingId().equals(bookingId));
        if (removed) rebuildDerivedCollections();
        return removed;
    }

    public boolean updateBooking(String bookingId, Booking updated) {
        for (int i = 0; i < bookings.size(); i++) {
            if (bookings.get(i).bookingId().equals(bookingId)) {
                bookings.set(i, updated);
                rebuildDerivedCollections();
                return true;
            }
        }
        return false;
    }

    // ── QUERY ─────────────────────────────────────────────────────────────

    public Optional<Booking> findById(String bookingId) {
        return bookings.stream()
                .filter(b -> b.bookingId().equals(bookingId))
                .findFirst();
    }

    /** Stream filter: bookings whose departure falls within [from, to] inclusive. */
    public List<Booking> findByDepartureRange(LocalDate from, LocalDate to) {
        return bookings.stream()
                .filter(b -> !b.departureDate().isBefore(from) && !b.departureDate().isAfter(to))
                .collect(Collectors.toList());
    }

    /** Stream map: transform to (customerName, destination) DTO pairs. */
    public List<BookingDto> toBookingDtos() {
        return bookings.stream()
                .map(b -> new BookingDto(b.customerName(), b.destination()))
                .collect(Collectors.toList());
    }

    /** Partial destination search — case-insensitive substring match. */
    public List<Booking> searchByDestination(String partial) {
        String lower = partial.toLowerCase();
        return bookings.stream()
                .filter(b -> b.destination().toLowerCase().contains(lower))
                .collect(Collectors.toList());
    }

    /** Price range filter. */
    public List<Booking> findByPriceRange(double minPrice, double maxPrice) {
        return bookings.stream()
                .filter(b -> b.price() >= minPrice && b.price() <= maxPrice)
                .collect(Collectors.toList());
    }

    /**
     * Computes total price of a list of bookings using an ordinary loop
     * — intentionally avoiding stream aggregate methods.
     */
    public double computeTotal(List<Booking> subset) {
        double total = 0.0;
        for (Booking b : subset) {
            total += b.price();
        }
        return total;
    }

    // ── ACCESSORS ─────────────────────────────────────────────────────────

    public List<Booking> getAllBookings() {
        return Collections.unmodifiableList(bookings);
    }

    public Set<String> getDestinations() {
        return Collections.unmodifiableSet(destinations);
    }

    public Map<String, Set<String>> getCustomerDestinations() {
        return Collections.unmodifiableMap(customerDestinations);
    }

    // ── PRIVATE HELPERS ───────────────────────────────────────────────────

    private void rebuildDerivedCollections() {
        destinations.clear();
        customerDestinations.clear();
        for (Booking b : bookings) {
            destinations.add(b.destination());
            customerDestinations.computeIfAbsent(b.customerId(), k -> new HashSet<>()).add(b.destination());
        }
    }
}
