package com.university.fitnessmembershipapi.dto;

import com.university.fitnessmembershipapi.entity.Member;
import com.university.fitnessmembershipapi.entity.MembershipType;

import java.math.BigDecimal;
import java.time.LocalDate;

/** Response DTO for all member endpoints. */
public record MemberResponseDto(
        Long id, String fullName, String email, MembershipType membershipType,
        boolean active, LocalDate joinDate, BigDecimal monthlyFee
) {
    public static MemberResponseDto from(Member m) {
        return new MemberResponseDto(m.getId(), m.getFullName(), m.getEmail(),
                m.getMembershipType(), m.isActive(), m.getJoinDate(), m.getMonthlyFee());
    }
}
