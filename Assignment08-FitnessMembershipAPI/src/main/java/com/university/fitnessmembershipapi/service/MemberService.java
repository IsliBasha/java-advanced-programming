package com.university.fitnessmembershipapi.service;

import com.university.fitnessmembershipapi.dto.MemberRequestDto;
import com.university.fitnessmembershipapi.dto.MemberResponseDto;
import com.university.fitnessmembershipapi.entity.Member;
import com.university.fitnessmembershipapi.entity.MembershipType;
import com.university.fitnessmembershipapi.exception.DuplicateEmailException;
import com.university.fitnessmembershipapi.exception.ResourceNotFoundException;
import com.university.fitnessmembershipapi.repository.MemberRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Business logic for fitness-centre members.
 *
 * <p>Owns email-uniqueness enforcement, lookups, and persistence so the
 * controller stays a thin HTTP layer with no business decisions.
 */
@Service
@Transactional
public class MemberService {

    private final MemberRepository memberRepository;

    public MemberService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    /** Creates a member, rejecting a duplicate email before it reaches the database. */
    public MemberResponseDto create(MemberRequestDto request) {
        if (memberRepository.existsByEmailIgnoreCase(request.email())) {
            throw new DuplicateEmailException("A member with email '" + request.email() + "' already exists.");
        }
        Member member = new Member(
                request.fullName(),
                request.email(),
                request.membershipType(),
                request.active(),
                request.joinDate(),
                request.monthlyFee());
        return MemberResponseDto.from(memberRepository.save(member));
    }

    @Transactional(readOnly = true)
    public MemberResponseDto findById(Long id) {
        return MemberResponseDto.from(getOrThrow(id));
    }

    /**
     * Returns members, optionally narrowed by active status and/or membership type.
     * Any combination of the two filters is supported.
     */
    @Transactional(readOnly = true)
    public List<MemberResponseDto> findAll(Boolean active, MembershipType membershipType) {
        List<Member> members;
        if (active != null && membershipType != null) {
            members = memberRepository.findByActiveAndMembershipType(active, membershipType);
        } else if (active != null) {
            members = memberRepository.findByActive(active);
        } else if (membershipType != null) {
            members = memberRepository.findByMembershipType(membershipType);
        } else {
            members = memberRepository.findAll();
        }
        return members.stream().map(MemberResponseDto::from).toList();
    }

    /** Updates a member; the email must remain unique across <em>other</em> members. */
    public MemberResponseDto update(Long id, MemberRequestDto request) {
        Member member = getOrThrow(id);
        if (memberRepository.existsByEmailIgnoreCaseAndIdNot(request.email(), id)) {
            throw new DuplicateEmailException("A member with email '" + request.email() + "' already exists.");
        }
        member.setFullName(request.fullName());
        member.setEmail(request.email());
        member.setMembershipType(request.membershipType());
        member.setActive(request.active());
        member.setJoinDate(request.joinDate());
        member.setMonthlyFee(request.monthlyFee());
        return MemberResponseDto.from(memberRepository.save(member));
    }

    public void delete(Long id) {
        memberRepository.delete(getOrThrow(id));
    }

    private Member getOrThrow(Long id) {
        return memberRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Member not found: id=" + id));
    }
}
