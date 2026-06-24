package com.university.fitnesscentre.dto;

/** Lightweight DTO for stream-mapped member results. */
public record MemberSummaryDto(String fullName, String membershipType, int attendedClassCount) {}
