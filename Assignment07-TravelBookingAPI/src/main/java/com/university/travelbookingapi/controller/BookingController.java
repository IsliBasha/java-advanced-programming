package com.university.travelbookingapi.controller;

import com.university.travelbookingapi.dto.BookingRequestDto;
import com.university.travelbookingapi.dto.BookingResponseDto;
import com.university.travelbookingapi.entity.BookingStatus;
import com.university.travelbookingapi.service.BookingService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/** REST endpoints for managing travel bookings. */
@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    public ResponseEntity<BookingResponseDto> create(@Valid @RequestBody BookingRequestDto request) {
        BookingResponseDto created = bookingService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/{id}")
    public BookingResponseDto findById(@PathVariable Long id) {
        return bookingService.findById(id);
    }

    @GetMapping
    public List<BookingResponseDto> findAll(
            @RequestParam(required = false) String destination,
            @RequestParam(required = false) BookingStatus status) {
        return bookingService.findAll(destination, status);
    }

    @PutMapping("/{id}")
    public BookingResponseDto update(@PathVariable Long id, @Valid @RequestBody BookingRequestDto request) {
        return bookingService.update(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        bookingService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
