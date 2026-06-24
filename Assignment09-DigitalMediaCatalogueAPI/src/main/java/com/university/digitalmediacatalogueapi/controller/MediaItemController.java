package com.university.digitalmediacatalogueapi.controller;

import com.university.digitalmediacatalogueapi.dto.MediaItemRequestDto;
import com.university.digitalmediacatalogueapi.dto.MediaItemResponseDto;
import com.university.digitalmediacatalogueapi.entity.MediaType;
import com.university.digitalmediacatalogueapi.service.MediaItemService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/** REST endpoints for managing the digital media catalogue. */
@RestController
@RequestMapping("/api/media")
public class MediaItemController {

    private final MediaItemService mediaItemService;

    public MediaItemController(MediaItemService mediaItemService) {
        this.mediaItemService = mediaItemService;
    }

    @PostMapping
    public ResponseEntity<MediaItemResponseDto> create(@Valid @RequestBody MediaItemRequestDto request) {
        MediaItemResponseDto created = mediaItemService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/{id}")
    public MediaItemResponseDto findById(@PathVariable Long id) {
        return mediaItemService.findById(id);
    }

    @GetMapping
    public List<MediaItemResponseDto> findAll(
            @RequestParam(required = false) MediaType type,
            @RequestParam(required = false) Boolean available) {
        return mediaItemService.findAll(type, available);
    }

    @PutMapping("/{id}")
    public MediaItemResponseDto update(@PathVariable Long id, @Valid @RequestBody MediaItemRequestDto request) {
        return mediaItemService.update(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        mediaItemService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
