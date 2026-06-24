package com.university.courseenrollmentapi.repository;

import com.university.courseenrollmentapi.entity.Enrollment;
import com.university.courseenrollmentapi.entity.EnrollmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/** Spring Data JPA repository for Enrollment entities. */
public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {

    List<Enrollment> findByStatus(EnrollmentStatus status);

    List<Enrollment> findByCourseId(String courseId);

    List<Enrollment> findByStudentId(String studentId);

    Optional<Enrollment> findByStudentIdAndCourseIdAndStatus(
            String studentId, String courseId, EnrollmentStatus status);
}
