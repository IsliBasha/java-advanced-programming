package com.university.fitnesscentre;

import com.university.fitnesscentre.model.Member;
import com.university.fitnesscentre.model.MembershipType;
import com.university.fitnesscentre.service.MembershipService;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * Entry point for Assignment 03 – Fitness Centre Membership System.
 */
public class FitnessCentreMembershipApp {

    public static void main(String[] args) {
        MembershipService service = new MembershipService();

        // ── Seed realistic data ───────────────────────────────────────────
        service.addMember(new Member("M001", "Alice Johnson",  "alice@gym.com",  MembershipType.PREMIUM,  true,  LocalDate.of(2023, 1, 10), List.of("Yoga", "Pilates", "Spin")));
        service.addMember(new Member("M002", "Bob Martinez",   "bob@gym.com",    MembershipType.BASIC,    true,  LocalDate.of(2023, 3, 5),  List.of("Yoga", "Boxing")));
        service.addMember(new Member("M003", "Clara Schmidt",  "clara@gym.com",  MembershipType.STANDARD, false, LocalDate.of(2022, 8, 20), List.of("Pilates", "Zumba")));
        service.addMember(new Member("M004", "David Okonkwo",  "david@gym.com",  MembershipType.PREMIUM,  true,  LocalDate.of(2023, 5, 14), List.of("Spin", "Boxing", "CrossFit")));
        service.addMember(new Member("M005", "Eva Lindström",  "eva@gym.com",    MembershipType.BASIC,    false, LocalDate.of(2023, 2, 28), List.of("Yoga")));
        service.addMember(new Member("M006", "Fatima Al-Amin", "fatima@gym.com", MembershipType.STANDARD, true,  LocalDate.of(2024, 1, 3),  List.of("Zumba", "Pilates", "Yoga")));

        printHeader("ALL MEMBERS");
        printMembers(service.getAllMembers());

        printHeader("CLASS TYPES Set");
        service.getClassTypes().stream().sorted().forEach(c -> System.out.println("  " + c));

        printHeader("MEMBERS BY CLASS TYPE Map<String, List<Member>>");
        service.getMembersByClassType().entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .forEach(e -> {
                    System.out.printf("  %-12s → ", e.getKey());
                    System.out.println(e.getValue().stream().map(Member::fullName).toList());
                });

        printHeader("Optional LOOKUP: M004");
        service.findById("M004").ifPresentOrElse(
                m -> System.out.printf("  Found: %s (%s) – active: %b%n", m.fullName(), m.membershipType(), m.active()),
                () -> System.out.println("  Not found."));

        printHeader("STREAM LOOKUP: members who attended Yoga");
        service.findByClassAttendance("Yoga").forEach(m ->
                System.out.printf("  %s (%s)%n", m.fullName(), m.membershipType()));

        printHeader("ACTIVE FILTER");
        System.out.println("  Active members:");
        service.findByActive(true).forEach(m -> System.out.println("    " + m.fullName()));
        System.out.println("  Inactive members:");
        service.findByActive(false).forEach(m -> System.out.println("    " + m.fullName()));

        printHeader("MEMBERSHIP TYPE FILTER: PREMIUM");
        service.findByMembershipType(MembershipType.PREMIUM)
                .forEach(m -> System.out.printf("  %s – active: %b%n", m.fullName(), m.active()));

        printHeader("COMBINED FILTER: active=true AND type=BASIC");
        service.findByActiveAndType(true, MembershipType.BASIC)
                .forEach(m -> System.out.printf("  %s%n", m.fullName()));

        printHeader("NAME → ATTENDED CLASS COUNT Map");
        service.mapNamesToAttendedClassCounts().entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .forEach(e -> System.out.printf("  %-20s → %d classes%n", e.getKey(), e.getValue()));

        printHeader("STREAM MAP → MemberSummaryDto");
        service.toSummaries().forEach(dto ->
                System.out.printf("  %-20s | %-10s | %d classes%n",
                        dto.fullName(), dto.membershipType(), dto.attendedClassCount()));
    }

    private static void printHeader(String title) {
        System.out.println();
        System.out.println("═".repeat(62));
        System.out.println("  " + title);
        System.out.println("─".repeat(62));
    }

    private static void printMembers(List<Member> list) {
        System.out.printf("  %-6s %-20s %-10s %-8s %s%n", "ID", "Name", "Type", "Active", "Classes");
        System.out.println("  " + "─".repeat(58));
        list.forEach(m -> System.out.printf(
                "  %-6s %-20s %-10s %-8b %s%n",
                m.memberId(), m.fullName(), m.membershipType(), m.active(), m.attendedClasses()));
    }
}
