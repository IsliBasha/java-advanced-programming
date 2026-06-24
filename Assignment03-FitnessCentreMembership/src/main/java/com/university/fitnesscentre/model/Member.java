package com.university.fitnesscentre.model;

import java.time.LocalDate;
import java.util.List;

/**
 * Represents a fitness centre member.
 * {@code attendedClasses} is a defensive-copied list of class type strings.
 */
public record Member(
        String memberId,
        String fullName,
        String email,
        MembershipType membershipType,
        boolean active,
        LocalDate joinDate,
        List<String> attendedClasses
) {
    public Member {
        attendedClasses = List.copyOf(attendedClasses); // immutable copy
    }

    /** Returns a new Member with active status toggled. */
    public Member withActive(boolean newActive) {
        return new Member(memberId, fullName, email, membershipType, newActive, joinDate, attendedClasses);
    }
}
