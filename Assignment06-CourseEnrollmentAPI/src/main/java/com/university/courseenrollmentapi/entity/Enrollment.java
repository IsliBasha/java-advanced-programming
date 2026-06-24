package com.university.courseenrollmentapi.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import java.time.LocalDate;

/**
 * JPA entity representing a student's enrolment in a course.
 */
@Entity
@Table(name = "enrollments",
       uniqueConstraints = @UniqueConstraint(columnNames = {"student_id", "course_id", "status"},
               name = "uk_student_course_active"))
public class Enrollment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Student ID is required")
    @Column(name = "student_id", nullable = false)
    private String studentId;

    @NotBlank(message = "Student name is required")
    @Column(name = "student_name", nullable = false)
    private String studentName;

    @NotBlank(message = "Course ID is required")
    @Column(name = "course_id", nullable = false)
    private String courseId;

    @NotBlank(message = "Course name is required")
    @Column(name = "course_name", nullable = false)
    private String courseName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EnrollmentStatus status;

    @DecimalMin(value = "0.0", message = "Completion percentage cannot be negative")
    @DecimalMax(value = "100.0", message = "Completion percentage cannot exceed 100")
    @Column(name = "completion_percentage", nullable = false)
    private Double completionPercentage;

    @Column(name = "enrolled_on", nullable = false)
    private LocalDate enrolledOn;

    protected Enrollment() {}

    public Enrollment(String studentId, String studentName, String courseId, String courseName,
                      EnrollmentStatus status, Double completionPercentage, LocalDate enrolledOn) {
        this.studentId = studentId;
        this.studentName = studentName;
        this.courseId = courseId;
        this.courseName = courseName;
        this.status = status;
        this.completionPercentage = completionPercentage;
        this.enrolledOn = enrolledOn;
    }

    public Long getId() { return id; }
    public String getStudentId() { return studentId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }
    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }
    public String getCourseId() { return courseId; }
    public void setCourseId(String courseId) { this.courseId = courseId; }
    public String getCourseName() { return courseName; }
    public void setCourseName(String courseName) { this.courseName = courseName; }
    public EnrollmentStatus getStatus() { return status; }
    public void setStatus(EnrollmentStatus status) { this.status = status; }
    public Double getCompletionPercentage() { return completionPercentage; }
    public void setCompletionPercentage(Double completionPercentage) { this.completionPercentage = completionPercentage; }
    public LocalDate getEnrolledOn() { return enrolledOn; }
    public void setEnrolledOn(LocalDate enrolledOn) { this.enrolledOn = enrolledOn; }
}
