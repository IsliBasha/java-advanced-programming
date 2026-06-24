package com.university.courseenrollmentapi.service;

import com.university.courseenrollmentapi.dto.EnrollmentRequestDto;
import com.university.courseenrollmentapi.dto.EnrollmentResponseDto;
import com.university.courseenrollmentapi.entity.Enrollment;
import com.university.courseenrollmentapi.entity.EnrollmentStatus;
import com.university.courseenrollmentapi.exception.DuplicateEnrollmentException;
import com.university.courseenrollmentapi.exception.ResourceNotFoundException;
import com.university.courseenrollmentapi.repository.EnrollmentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Business logic for course enrolment management.
 * All rules enforced here — controllers remain thin.
 */
@Service
@Transactional
public class EnrollmentService {

    private final EnrollmentRepository repository;

    public EnrollmentService(EnrollmentRepository repository) {
        this.repository = repository;
    }

    public EnrollmentResponseDto create(EnrollmentRequestDto dto) {
        if (dto.status() == EnrollmentStatus.ACTIVE) {
            repository.findByStudentIdAndCourseIdAndStatus(dto.studentId(), dto.courseId(), EnrollmentStatus.ACTIVE)
                    .ifPresent(e -> {
                        throw new DuplicateEnrollmentException(
                                "Student " + dto.studentId() + " already has an ACTIVE enrolment for course " + dto.courseId());
                    });
        }

        Enrollment saved = repository.save(new Enrollment(
                dto.studentId(), dto.studentName(), dto.courseId(), dto.courseName(),
                dto.status(), dto.completionPercentage(), dto.enrolledOn()));

        return EnrollmentResponseDto.from(saved);
    }

    @Transactional(readOnly = true)
    public EnrollmentResponseDto findById(Long id) {
        return EnrollmentResponseDto.from(getOrThrow(id));
    }

    @Transactional(readOnly = true)
    public List<EnrollmentResponseDto> findAll(EnrollmentStatus status, String courseId, String studentId) {
        if (status != null) {
            return repository.findByStatus(status).stream().map(EnrollmentResponseDto::from).toList();
        }
        if (courseId != null) {
            return repository.findByCourseId(courseId).stream().map(EnrollmentResponseDto::from).toList();
        }
        if (studentId != null) {
            return repository.findByStudentId(studentId).stream().map(EnrollmentResponseDto::from).toList();
        }
        return repository.findAll().stream().map(EnrollmentResponseDto::from).toList();
    }

    public EnrollmentResponseDto update(Long id, EnrollmentRequestDto dto) {
        Enrollment existing = getOrThrow(id);

        if (dto.status() == EnrollmentStatus.ACTIVE && existing.getStatus() != EnrollmentStatus.ACTIVE) {
            repository.findByStudentIdAndCourseIdAndStatus(dto.studentId(), dto.courseId(), EnrollmentStatus.ACTIVE)
                    .ifPresent(e -> {
                        throw new DuplicateEnrollmentException(
                                "Student already has an ACTIVE enrolment for this course.");
                    });
        }

        existing.setStudentId(dto.studentId());
        existing.setStudentName(dto.studentName());
        existing.setCourseId(dto.courseId());
        existing.setCourseName(dto.courseName());
        existing.setStatus(dto.status());
        existing.setCompletionPercentage(dto.completionPercentage());
        existing.setEnrolledOn(dto.enrolledOn());

        return EnrollmentResponseDto.from(repository.save(existing));
    }

    public void delete(Long id) {
        getOrThrow(id);
        repository.deleteById(id);
    }

    private Enrollment getOrThrow(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Enrollment not found with id: " + id));
    }
}
