package com.university.travelbookingapi.repository;

import com.university.travelbookingapi.entity.Booking;
import com.university.travelbookingapi.entity.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/** Spring Data JPA repository for Booking entities. */
public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByStatus(BookingStatus status);
    List<Booking> findByDestinationContainingIgnoreCase(String destination);
    List<Booking> findByDestinationContainingIgnoreCaseAndStatus(String destination, BookingStatus status);
}
