package com.university.fitnessmembershipapi.dto;

import com.university.fitnessmembershipapi.entity.MembershipType;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;

/** Request DTO for creating or updating a member. */
public record MemberRequestDto(
        @NotBlank(message = "Full name is required") String fullName,
        @NotBlank(message = "Email is required") @Email(message = "Email must be a valid address") String email,
        @NotNull(message = "Membership type is required") MembershipType membershipType,
        @NotNull(message = "Active flag is required") Boolean active,
        @NotNull(message = "Join date is required") LocalDate joinDate,
        @NotNull(message = "Monthly fee is required")
        @DecimalMin(value = "0.0", message = "Monthly fee cannot be negative") BigDecimal monthlyFee
) {}
