package com.university.courseenrollmentapi;

import com.university.courseenrollmentapi.dto.EnrollmentRequestDto;
import com.university.courseenrollmentapi.dto.EnrollmentResponseDto;
import com.university.courseenrollmentapi.entity.EnrollmentStatus;
import com.university.courseenrollmentapi.exception.DuplicateEnrollmentException;
import com.university.courseenrollmentapi.exception.ResourceNotFoundException;
import com.university.courseenrollmentapi.repository.EnrollmentRepository;
import com.university.courseenrollmentapi.service.EnrollmentService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@DisplayName("EnrollmentService Integration Tests")
class EnrollmentServiceTest {

    @Autowired
    private EnrollmentService service;

    @Autowired
    private EnrollmentRepository repository;

    private EnrollmentRequestDto buildDto(String studentId, String courseId, EnrollmentStatus status) {
        return new EnrollmentRequestDto(studentId, "Test Student", courseId, "Test Course",
                status, 50.0, LocalDate.now());
    }

    @Test @DisplayName("create() persists a new enrollment and returns DTO")
    void testCreate() {
        EnrollmentResponseDto result = service.create(buildDto("S001", "CS999", EnrollmentStatus.ACTIVE));
        assertNotNull(result.id());
        assertEquals("S001", result.studentId());
        assertEquals(EnrollmentStatus.ACTIVE, result.status());
    }

    @Test @DisplayName("create() throws DuplicateEnrollmentException for duplicate ACTIVE enrollment")
    void testDuplicateActive() {
        service.create(buildDto("S001", "CS101", EnrollmentStatus.ACTIVE));
        assertThrows(DuplicateEnrollmentException.class,
                () -> service.create(buildDto("S001", "CS101", EnrollmentStatus.ACTIVE)));
    }

    @Test @DisplayName("findById() returns enrollment for valid ID")
    void testFindById() {
        EnrollmentResponseDto created = service.create(buildDto("S002", "CS200", EnrollmentStatus.ACTIVE));
        EnrollmentResponseDto found = service.findById(created.id());
        assertEquals("S002", found.studentId());
    }

    @Test @DisplayName("findById() throws ResourceNotFoundException for invalid ID")
    void testFindByIdNotFound() {
        assertThrows(ResourceNotFoundException.class, () -> service.findById(999999L));
    }

    @Test @DisplayName("findAll() with status filter returns correct subset")
    void testFindAllWithStatusFilter() {
        service.create(buildDto("S003", "CS300", EnrollmentStatus.ACTIVE));
        service.create(new EnrollmentRequestDto("S004", "Test", "CS300", "Test", EnrollmentStatus.COMPLETED, 100.0, LocalDate.now()));
        List<EnrollmentResponseDto> actives = service.findAll(EnrollmentStatus.ACTIVE, null, null);
        assertTrue(actives.stream().allMatch(e -> e.status() == EnrollmentStatus.ACTIVE));
    }

    @Test @DisplayName("update() changes enrollment fields")
    void testUpdate() {
        EnrollmentResponseDto created = service.create(buildDto("S005", "CS400", EnrollmentStatus.ACTIVE));
        EnrollmentRequestDto updateDto = new EnrollmentRequestDto("S005", "Updated Name", "CS400", "Updated Course",
                EnrollmentStatus.COMPLETED, 100.0, LocalDate.now());
        EnrollmentResponseDto updated = service.update(created.id(), updateDto);
        assertEquals("Updated Name", updated.studentName());
        assertEquals(EnrollmentStatus.COMPLETED, updated.status());
    }

    @Test @DisplayName("delete() removes enrollment by ID")
    void testDelete() {
        EnrollmentResponseDto created = service.create(buildDto("S006", "CS500", EnrollmentStatus.ACTIVE));
        service.delete(created.id());
        assertThrows(ResourceNotFoundException.class, () -> service.findById(created.id()));
    }
}
