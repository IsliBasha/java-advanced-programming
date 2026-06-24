package com.university.courseenrollmentapi.dto;

import com.university.courseenrollmentapi.entity.EnrollmentStatus;
import jakarta.validation.constraints.*;

import java.time.LocalDate;

/** Request DTO for creating or updating an enrolment. */
public record EnrollmentRequestDto(
        @NotBlank(message = "Student ID is required")
        String studentId,

        @NotBlank(message = "Student name is required")
        String studentName,

        @NotBlank(message = "Course ID is required")
        String courseId,

        @NotBlank(message = "Course name is required")
        String courseName,

        @NotNull(message = "Status is required")
        EnrollmentStatus status,

        @NotNull(message = "Completion percentage is required")
        @DecimalMin(value = "0.0", message = "Completion percentage cannot be negative")
        @DecimalMax(value = "100.0", message = "Completion percentage cannot exceed 100")
        Double completionPercentage,

        @NotNull(message = "Enrolled-on date is required")
        LocalDate enrolledOn
) {}
