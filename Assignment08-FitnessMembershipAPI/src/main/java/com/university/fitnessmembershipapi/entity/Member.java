package com.university.fitnessmembershipapi.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;

/** JPA entity representing a fitness-centre member. Email is unique. */
@Entity
@Table(name = "members", uniqueConstraints = @UniqueConstraint(name = "uk_member_email", columnNames = "email"))
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Full name is required")
    @Column(name = "full_name", nullable = false)
    private String fullName;

    @NotBlank(message = "Email is required")
    @Email(message = "Email must be a valid address")
    @Column(nullable = false, unique = true)
    private String email;

    @Enumerated(EnumType.STRING)
    @NotNull(message = "Membership type is required")
    @Column(name = "membership_type", nullable = false)
    private MembershipType membershipType;

    @Column(nullable = false)
    private boolean active;

    @NotNull(message = "Join date is required")
    @Column(name = "join_date", nullable = false)
    private LocalDate joinDate;

    @NotNull(message = "Monthly fee is required")
    @DecimalMin(value = "0.0", message = "Monthly fee cannot be negative")
    @Column(name = "monthly_fee", nullable = false, precision = 8, scale = 2)
    private BigDecimal monthlyFee;

    protected Member() {}

    public Member(String fullName, String email, MembershipType membershipType,
                  boolean active, LocalDate joinDate, BigDecimal monthlyFee) {
        this.fullName = fullName;
        this.email = email;
        this.membershipType = membershipType;
        this.active = active;
        this.joinDate = joinDate;
        this.monthlyFee = monthlyFee;
    }

    public Long getId() { return id; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public MembershipType getMembershipType() { return membershipType; }
    public void setMembershipType(MembershipType membershipType) { this.membershipType = membershipType; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
    public LocalDate getJoinDate() { return joinDate; }
    public void setJoinDate(LocalDate joinDate) { this.joinDate = joinDate; }
    public BigDecimal getMonthlyFee() { return monthlyFee; }
    public void setMonthlyFee(BigDecimal monthlyFee) { this.monthlyFee = monthlyFee; }
}
