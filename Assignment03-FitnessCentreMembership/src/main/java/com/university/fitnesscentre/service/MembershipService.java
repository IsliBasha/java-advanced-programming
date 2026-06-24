package com.university.fitnesscentre.service;

import com.university.fitnesscentre.dto.MemberSummaryDto;
import com.university.fitnesscentre.model.Member;
import com.university.fitnesscentre.model.MembershipType;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Manages fitness centre members using List, Set, and Map.
 */
public class MembershipService {

    private final List<Member> members = new ArrayList<>();
    private final Set<String> classTypes = new HashSet<>();
    private final Map<String, List<Member>> membersByClassType = new HashMap<>();

    // ── CRUD ─────────────────────────────────────────────────────────────

    public void addMember(Member member) {
        members.add(member);
        classTypes.addAll(member.attendedClasses());
        for (String classType : member.attendedClasses()) {
            membersByClassType.computeIfAbsent(classType, k -> new ArrayList<>()).add(member);
        }
    }

    public boolean removeMember(String memberId) {
        boolean removed = members.removeIf(m -> m.memberId().equals(memberId));
        if (removed) rebuildDerivedCollections();
        return removed;
    }

    public boolean updateMember(String memberId, Member updated) {
        for (int i = 0; i < members.size(); i++) {
            if (members.get(i).memberId().equals(memberId)) {
                members.set(i, updated);
                rebuildDerivedCollections();
                return true;
            }
        }
        return false;
    }

    // ── QUERY ─────────────────────────────────────────────────────────────

    public Optional<Member> findById(String memberId) {
        return members.stream()
                .filter(m -> m.memberId().equals(memberId))
                .findFirst();
    }

    /** Stream lookup: members who attended a specific class type. */
    public List<Member> findByClassAttendance(String classType) {
        return members.stream()
                .filter(m -> m.attendedClasses().contains(classType))
                .collect(Collectors.toList());
    }

    /** Filter by active status. */
    public List<Member> findByActive(boolean active) {
        return members.stream()
                .filter(m -> m.active() == active)
                .collect(Collectors.toList());
    }

    /** Filter by membership type. */
    public List<Member> findByMembershipType(MembershipType type) {
        return members.stream()
                .filter(m -> m.membershipType() == type)
                .collect(Collectors.toList());
    }

    /** Combined filter: active status AND membership type. */
    public List<Member> findByActiveAndType(boolean active, MembershipType type) {
        return members.stream()
                .filter(m -> m.active() == active && m.membershipType() == type)
                .collect(Collectors.toList());
    }

    /** Map each member name to their total attended class count. */
    public Map<String, Integer> mapNamesToAttendedClassCounts() {
        return members.stream()
                .collect(Collectors.toMap(
                        Member::fullName,
                        m -> m.attendedClasses().size(),
                        Integer::sum  // merge duplicate names
                ));
    }

    /** Stream transform to lightweight DTO. */
    public List<MemberSummaryDto> toSummaries() {
        return members.stream()
                .map(m -> new MemberSummaryDto(
                        m.fullName(),
                        m.membershipType().name(),
                        m.attendedClasses().size()))
                .collect(Collectors.toList());
    }

    // ── ACCESSORS ─────────────────────────────────────────────────────────

    public List<Member> getAllMembers() {
        return Collections.unmodifiableList(members);
    }

    public Set<String> getClassTypes() {
        return Collections.unmodifiableSet(classTypes);
    }

    public Map<String, List<Member>> getMembersByClassType() {
        return Collections.unmodifiableMap(membersByClassType);
    }

    // ── PRIVATE HELPERS ───────────────────────────────────────────────────

    private void rebuildDerivedCollections() {
        classTypes.clear();
        membersByClassType.clear();
        for (Member m : members) {
            classTypes.addAll(m.attendedClasses());
            for (String ct : m.attendedClasses()) {
                membersByClassType.computeIfAbsent(ct, k -> new ArrayList<>()).add(m);
            }
        }
    }
}
