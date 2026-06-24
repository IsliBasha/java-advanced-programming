package com.university.courseenrollmentapi.dto;

import com.university.courseenrollmentapi.entity.Enrollment;
import com.university.courseenrollmentapi.entity.EnrollmentStatus;

import java.time.LocalDate;

/** Response DTO returned from all endpoints. */
public record EnrollmentResponseDto(
        Long id,
        String studentId,
        String studentName,
        String courseId,
        String courseName,
        EnrollmentStatus status,
        Double completionPercentage,
        LocalDate enrolledOn
) {
    public static EnrollmentResponseDto from(Enrollment e) {
        return new EnrollmentResponseDto(
                e.getId(), e.getStudentId(), e.getStudentName(),
                e.getCourseId(), e.getCourseName(), e.getStatus(),
                e.getCompletionPercentage(), e.getEnrolledOn());
    }
}
