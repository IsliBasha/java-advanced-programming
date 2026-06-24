package com.university.travelbooking;

import com.university.travelbooking.dto.BookingDto;
import com.university.travelbooking.model.Booking;
import com.university.travelbooking.model.BookingStatus;
import com.university.travelbooking.service.BookingService;

import java.time.LocalDate;
import java.util.List;

/**
 * Entry point for Assignment 02 – Travel Booking Management System.
 */
public class TravelBookingApp {

    public static void main(String[] args) {
        BookingService service = new BookingService();

        // ── Seed data ─────────────────────────────────────────────────────
        service.addBooking(new Booking("B001", "C001", "Alice Johnson",  "Paris",     LocalDate.of(2024, 6, 10), LocalDate.of(2024, 6, 20), 1250.00, BookingStatus.CONFIRMED));
        service.addBooking(new Booking("B002", "C002", "Bob Martinez",   "Tokyo",     LocalDate.of(2024, 7, 5),  LocalDate.of(2024, 7, 15), 3400.00, BookingStatus.CONFIRMED));
        service.addBooking(new Booking("B003", "C001", "Alice Johnson",  "Barcelona", LocalDate.of(2024, 8, 1),  LocalDate.of(2024, 8, 8),  850.00,  BookingStatus.PENDING));
        service.addBooking(new Booking("B004", "C003", "Clara Schmidt",  "New York",  LocalDate.of(2024, 6, 15), LocalDate.of(2024, 6, 22), 1900.00, BookingStatus.CONFIRMED));
        service.addBooking(new Booking("B005", "C004", "David Okonkwo",  "Dubai",     LocalDate.of(2024, 9, 10), LocalDate.of(2024, 9, 18), 2100.00, BookingStatus.PENDING));
        service.addBooking(new Booking("B006", "C002", "Bob Martinez",   "Sydney",    LocalDate.of(2024, 10, 3), LocalDate.of(2024, 10, 14),4800.00, BookingStatus.CONFIRMED));
        service.addBooking(new Booking("B007", "C005", "Eva Lindström",  "Paris",     LocalDate.of(2024, 6, 20), LocalDate.of(2024, 6, 28), 1350.00, BookingStatus.CANCELLED));

        printHeader("ALL BOOKINGS");
        printBookings(service.getAllBookings());

        printHeader("DESTINATIONS Set");
        service.getDestinations().stream().sorted().forEach(d -> System.out.println("  " + d));

        printHeader("CUSTOMER → DESTINATIONS Map<String, Set<String>>");
        service.getCustomerDestinations().forEach((cid, dests) ->
                System.out.printf("  %-6s → %s%n", cid, dests));

        printHeader("Optional LOOKUP: B003");
        service.findById("B003").ifPresentOrElse(
                b -> System.out.printf("  Found: %s → %s on %s%n", b.customerName(), b.destination(), b.departureDate()),
                () -> System.out.println("  Not found."));

        printHeader("DEPARTURE RANGE: 01 Jun 2024 – 30 Jun 2024");
        List<Booking> june = service.findByDepartureRange(LocalDate.of(2024, 6, 1), LocalDate.of(2024, 6, 30));
        printBookings(june);
        System.out.printf("  Total for June: £%.2f%n", service.computeTotal(june));

        printHeader("STREAM MAP → BookingDto (customerName, destination)");
        List<BookingDto> dtos = service.toBookingDtos();
        dtos.forEach(dto -> System.out.printf("  %-20s → %s%n", dto.customerName(), dto.destination()));

        printHeader("PARTIAL DESTINATION SEARCH: \"ar\"");
        service.searchByDestination("ar").forEach(b ->
                System.out.printf("  %s → %s%n", b.bookingId(), b.destination()));

        printHeader("PRICE RANGE: £1000 – £2500");
        List<Booking> midRange = service.findByPriceRange(1000.00, 2500.00);
        printBookings(midRange);
        System.out.printf("  Total (loop, no aggregates): £%.2f%n", service.computeTotal(midRange));
    }

    private static void printHeader(String title) {
        System.out.println();
        System.out.println("═".repeat(65));
        System.out.println("  " + title);
        System.out.println("─".repeat(65));
    }

    private static void printBookings(List<Booking> list) {
        System.out.printf("  %-6s %-20s %-12s %-12s %8s  %-10s%n",
                "ID", "Customer", "Destination", "Departure", "Price", "Status");
        System.out.println("  " + "─".repeat(62));
        list.forEach(b -> System.out.printf(
                "  %-6s %-20s %-12s %-12s £%7.2f  %-10s%n",
                b.bookingId(), b.customerName(), b.destination(),
                b.departureDate(), b.price(), b.status()));
    }
}
