package com.university.courseanalytics.dto;

/**
 * DTO for displaying a lightweight summary of an enrolment.
 * Stream transformations target this type (not the full Enrollment record).
 */
public record EnrollmentSummaryDto(
        String studentName,
        String courseName,
        double completionPercentage
) {}
