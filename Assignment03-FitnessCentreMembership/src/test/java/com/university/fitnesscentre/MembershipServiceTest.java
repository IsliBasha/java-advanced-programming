package com.university.fitnesscentre;

import com.university.fitnesscentre.model.Member;
import com.university.fitnesscentre.model.MembershipType;
import com.university.fitnesscentre.service.MembershipService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("MembershipService Tests")
class MembershipServiceTest {

    private MembershipService service;
    private static final LocalDate DATE = LocalDate.of(2024, 1, 1);

    @BeforeEach
    void setUp() {
        service = new MembershipService();
        service.addMember(new Member("M001", "Alice", "a@gym.com", MembershipType.PREMIUM,  true,  DATE, List.of("Yoga", "Spin")));
        service.addMember(new Member("M002", "Bob",   "b@gym.com", MembershipType.BASIC,    true,  DATE, List.of("Yoga", "Boxing")));
        service.addMember(new Member("M003", "Clara", "c@gym.com", MembershipType.STANDARD, false, DATE, List.of("Pilates")));
    }

    @Test @DisplayName("addMember populates list, set, and map")
    void testAdd() {
        assertEquals(3, service.getAllMembers().size());
        assertTrue(service.getClassTypes().contains("Yoga"));
        assertEquals(2, service.getMembersByClassType().get("Yoga").size());
    }

    @Test @DisplayName("findById returns present Optional")
    void testFindById() {
        assertTrue(service.findById("M002").isPresent());
        assertTrue(service.findById("M999").isEmpty());
    }

    @Test @DisplayName("findByClassAttendance returns correct members")
    void testFindByClass() {
        List<Member> yogaMembers = service.findByClassAttendance("Yoga");
        assertEquals(2, yogaMembers.size());
        assertTrue(yogaMembers.stream().anyMatch(m -> m.fullName().equals("Alice")));
    }

    @Test @DisplayName("findByActive returns only active or inactive members")
    void testFindByActive() {
        assertEquals(2, service.findByActive(true).size());
        assertEquals(1, service.findByActive(false).size());
    }

    @Test @DisplayName("findByMembershipType returns correct subset")
    void testFindByType() {
        assertEquals(1, service.findByMembershipType(MembershipType.PREMIUM).size());
    }

    @Test @DisplayName("findByActiveAndType combines both filters")
    void testCombinedFilter() {
        List<Member> result = service.findByActiveAndType(true, MembershipType.BASIC);
        assertEquals(1, result.size());
        assertEquals("Bob", result.get(0).fullName());
    }

    @Test @DisplayName("mapNamesToAttendedClassCounts returns correct counts")
    void testClassCountMap() {
        Map<String, Integer> counts = service.mapNamesToAttendedClassCounts();
        assertEquals(2, counts.get("Alice"));
        assertEquals(1, counts.get("Clara"));
    }

    @Test @DisplayName("removeMember updates derived collections")
    void testRemove() {
        service.removeMember("M001");
        assertEquals(2, service.getAllMembers().size());
        assertEquals(1, service.getMembersByClassType().get("Yoga").size());
    }

    @Test @DisplayName("toSummaries maps fields correctly")
    void testToSummaries() {
        var summaries = service.toSummaries();
        assertEquals(3, summaries.size());
        assertTrue(summaries.stream().anyMatch(s -> s.fullName().equals("Alice") && s.attendedClassCount() == 2));
    }
}
