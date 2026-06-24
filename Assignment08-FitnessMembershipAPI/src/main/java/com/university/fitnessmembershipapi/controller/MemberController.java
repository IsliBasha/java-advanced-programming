package com.university.fitnessmembershipapi.controller;

import com.university.fitnessmembershipapi.dto.MemberRequestDto;
import com.university.fitnessmembershipapi.dto.MemberResponseDto;
import com.university.fitnessmembershipapi.entity.MembershipType;
import com.university.fitnessmembershipapi.service.MemberService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/** REST endpoints for managing fitness-centre members. */
@RestController
@RequestMapping("/api/members")
public class MemberController {

    private final MemberService memberService;

    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    @PostMapping
    public ResponseEntity<MemberResponseDto> create(@Valid @RequestBody MemberRequestDto request) {
        MemberResponseDto created = memberService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/{id}")
    public MemberResponseDto findById(@PathVariable Long id) {
        return memberService.findById(id);
    }

    @GetMapping
    public List<MemberResponseDto> findAll(
            @RequestParam(required = false) Boolean active,
            @RequestParam(required = false) MembershipType membershipType) {
        return memberService.findAll(active, membershipType);
    }

    @PutMapping("/{id}")
    public MemberResponseDto update(@PathVariable Long id, @Valid @RequestBody MemberRequestDto request) {
        return memberService.update(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        memberService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
