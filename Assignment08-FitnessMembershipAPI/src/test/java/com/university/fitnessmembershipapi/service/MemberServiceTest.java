package com.university.fitnessmembershipapi.service;

import com.university.fitnessmembershipapi.dto.MemberRequestDto;
import com.university.fitnessmembershipapi.dto.MemberResponseDto;
import com.university.fitnessmembershipapi.entity.MembershipType;
import com.university.fitnessmembershipapi.exception.DuplicateEmailException;
import com.university.fitnessmembershipapi.exception.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/** Cross-layer integration tests for the member service backed by H2. */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
class MemberServiceTest {

    @Autowired
    private MemberService memberService;

    private MemberRequestDto request(String name, String email, MembershipType type, boolean active) {
        return new MemberRequestDto(name, email, type, active,
                LocalDate.of(2025, 1, 1), new BigDecimal("39.99"));
    }

    @Test
    void create_persistsAndReturnsMemberWithId() {
        MemberResponseDto created = memberService.create(
                request("Alice", "alice@example.com", MembershipType.PREMIUM, true));
        assertNotNull(created.id());
        assertEquals("alice@example.com", created.email());
        assertTrue(created.active());
    }

    @Test
    void create_duplicateEmail_throwsDuplicateEmail() {
        memberService.create(request("Bob", "bob@example.com", MembershipType.BASIC, true));
        assertThrows(DuplicateEmailException.class, () ->
                memberService.create(request("Bobby", "bob@example.com", MembershipType.STANDARD, true)));
    }

    @Test
    void create_duplicateEmailDifferentCase_throwsDuplicateEmail() {
        memberService.create(request("Carol", "carol@example.com", MembershipType.BASIC, true));
        assertThrows(DuplicateEmailException.class, () ->
                memberService.create(request("Caroline", "CAROL@example.com", MembershipType.PREMIUM, true)));
    }

    @Test
    void findById_existing_returnsMember() {
        MemberResponseDto created = memberService.create(
                request("Dan", "dan@example.com", MembershipType.STANDARD, true));
        MemberResponseDto found = memberService.findById(created.id());
        assertEquals(created.id(), found.id());
        assertEquals("dan@example.com", found.email());
    }

    @Test
    void findById_missing_throwsResourceNotFound() {
        assertThrows(ResourceNotFoundException.class, () -> memberService.findById(99999L));
    }

    @Test
    void findAll_noFilters_returnsAll() {
        memberService.create(request("E", "e@example.com", MembershipType.BASIC, true));
        memberService.create(request("F", "f@example.com", MembershipType.PREMIUM, false));
        assertTrue(memberService.findAll(null, null).size() >= 2);
    }

    @Test
    void findAll_filterByActive_returnsOnlyMatching() {
        memberService.create(request("G", "g@example.com", MembershipType.BASIC, false));
        List<MemberResponseDto> inactive = memberService.findAll(false, null);
        assertFalse(inactive.isEmpty());
        assertTrue(inactive.stream().noneMatch(MemberResponseDto::active));
    }

    @Test
    void findAll_filterByMembershipType_returnsOnlyMatching() {
        memberService.create(request("H", "h@example.com", MembershipType.PREMIUM, true));
        List<MemberResponseDto> premium = memberService.findAll(null, MembershipType.PREMIUM);
        assertFalse(premium.isEmpty());
        assertTrue(premium.stream().allMatch(m -> m.membershipType() == MembershipType.PREMIUM));
    }

    @Test
    void findAll_filterByActiveAndType() {
        memberService.create(request("I", "i@example.com", MembershipType.STANDARD, true));
        memberService.create(request("J", "j@example.com", MembershipType.STANDARD, false));
        List<MemberResponseDto> result = memberService.findAll(true, MembershipType.STANDARD);
        assertFalse(result.isEmpty());
        assertTrue(result.stream().allMatch(m -> m.active() && m.membershipType() == MembershipType.STANDARD));
    }

    @Test
    void update_modifiesFields() {
        MemberResponseDto created = memberService.create(
                request("K", "k@example.com", MembershipType.BASIC, true));
        MemberRequestDto updated = new MemberRequestDto("K Updated", "k@example.com",
                MembershipType.PREMIUM, false, LocalDate.of(2025, 2, 2), new BigDecimal("59.99"));
        MemberResponseDto result = memberService.update(created.id(), updated);
        assertEquals("K Updated", result.fullName());
        assertEquals(MembershipType.PREMIUM, result.membershipType());
        assertFalse(result.active());
    }

    @Test
    void update_keepingOwnEmail_succeeds() {
        MemberResponseDto created = memberService.create(
                request("L", "l@example.com", MembershipType.BASIC, true));
        MemberRequestDto updated = new MemberRequestDto("L Renamed", "l@example.com",
                MembershipType.BASIC, true, LocalDate.of(2025, 1, 1), new BigDecimal("24.99"));
        assertDoesNotThrow(() -> memberService.update(created.id(), updated));
    }

    @Test
    void update_toEmailUsedByAnotherMember_throwsDuplicateEmail() {
        memberService.create(request("M", "m@example.com", MembershipType.BASIC, true));
        MemberResponseDto second = memberService.create(
                request("N", "n@example.com", MembershipType.BASIC, true));
        MemberRequestDto collide = new MemberRequestDto("N", "m@example.com",
                MembershipType.BASIC, true, LocalDate.of(2025, 1, 1), new BigDecimal("24.99"));
        assertThrows(DuplicateEmailException.class, () -> memberService.update(second.id(), collide));
    }

    @Test
    void delete_removesMember() {
        MemberResponseDto created = memberService.create(
                request("O", "o@example.com", MembershipType.BASIC, true));
        memberService.delete(created.id());
        assertThrows(ResourceNotFoundException.class, () -> memberService.findById(created.id()));
    }
}
