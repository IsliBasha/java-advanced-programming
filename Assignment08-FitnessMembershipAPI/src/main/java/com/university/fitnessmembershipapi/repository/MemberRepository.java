package com.university.fitnessmembershipapi.repository;

import com.university.fitnessmembershipapi.entity.Member;
import com.university.fitnessmembershipapi.entity.MembershipType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/** Spring Data JPA repository for Member entities. */
public interface MemberRepository extends JpaRepository<Member, Long> {

    boolean existsByEmailIgnoreCase(String email);
    boolean existsByEmailIgnoreCaseAndIdNot(String email, Long id);

    List<Member> findByActive(boolean active);
    List<Member> findByMembershipType(MembershipType membershipType);
    List<Member> findByActiveAndMembershipType(boolean active, MembershipType membershipType);
}
