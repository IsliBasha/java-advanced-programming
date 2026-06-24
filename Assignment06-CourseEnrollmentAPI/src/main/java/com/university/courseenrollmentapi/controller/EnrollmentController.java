package com.university.courseenrollmentapi.controller;

import com.university.courseenrollmentapi.dto.EnrollmentRequestDto;
import com.university.courseenrollmentapi.dto.EnrollmentResponseDto;
import com.university.courseenrollmentapi.entity.EnrollmentStatus;
import com.university.courseenrollmentapi.service.EnrollmentService;
import jakarta.validation.Valid;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for course enrolment endpoints.
 * Thin layer — all logic delegated to {@link EnrollmentService}.
 */
@RestController
@RequestMapping("/api/enrollments")
public class EnrollmentController {

    private final EnrollmentService service;

    public EnrollmentController(EnrollmentService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<EnrollmentResponseDto> create(@Valid @RequestBody EnrollmentRequestDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(dto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<EnrollmentResponseDto> findById(@PathVariable Long id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @GetMapping
    public ResponseEntity<List<EnrollmentResponseDto>> findAll(
            @RequestParam(required = false) EnrollmentStatus status,
            @RequestParam(required = false) String courseId,
            @RequestParam(required = false) String studentId) {
        return ResponseEntity.ok(service.findAll(status, courseId, studentId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<EnrollmentResponseDto> update(
            @PathVariable Long id, @Valid @RequestBody EnrollmentRequestDto dto) {
        return ResponseEntity.ok(service.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
