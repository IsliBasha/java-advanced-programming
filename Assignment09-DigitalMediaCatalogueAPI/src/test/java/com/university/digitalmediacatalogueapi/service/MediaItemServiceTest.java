package com.university.digitalmediacatalogueapi.service;

import com.university.digitalmediacatalogueapi.dto.MediaItemRequestDto;
import com.university.digitalmediacatalogueapi.dto.MediaItemResponseDto;
import com.university.digitalmediacatalogueapi.entity.MediaType;
import com.university.digitalmediacatalogueapi.exception.ResourceNotFoundException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for the media-catalogue service (CRUD + filters) plus
 * bean-validation tests for the request DTO constraints.
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
class MediaItemServiceTest {

    @Autowired
    private MediaItemService mediaItemService;

    @Autowired
    private Validator validator;

    private MediaItemRequestDto request(String title, MediaType type, String genre, int year, boolean available) {
        return new MediaItemRequestDto(title, type, genre, year, available);
    }

    // ---- Service behaviour ----

    @Test
    void create_persistsAndReturnsItemWithId() {
        MediaItemResponseDto created = mediaItemService.create(
                request("Inception", MediaType.MOVIE, "Sci-Fi", 2010, true));
        assertNotNull(created.id());
        assertEquals("Inception", created.title());
        assertTrue(created.available());
    }

    @Test
    void findById_existing_returnsItem() {
        MediaItemResponseDto created = mediaItemService.create(
                request("Hades", MediaType.GAME, "Roguelike", 2020, true));
        MediaItemResponseDto found = mediaItemService.findById(created.id());
        assertEquals(created.id(), found.id());
        assertEquals(MediaType.GAME, found.type());
    }

    @Test
    void findById_missing_throwsResourceNotFound() {
        assertThrows(ResourceNotFoundException.class, () -> mediaItemService.findById(99999L));
    }

    @Test
    void findAll_noFilters_returnsAll() {
        mediaItemService.create(request("A", MediaType.MOVIE, "Drama", 2001, true));
        mediaItemService.create(request("B", MediaType.MUSIC, "Jazz", 1995, false));
        assertTrue(mediaItemService.findAll(null, null).size() >= 2);
    }

    @Test
    void findAll_filterByType_returnsOnlyMatching() {
        mediaItemService.create(request("C", MediaType.EBOOK, "Programming", 2008, true));
        List<MediaItemResponseDto> ebooks = mediaItemService.findAll(MediaType.EBOOK, null);
        assertFalse(ebooks.isEmpty());
        assertTrue(ebooks.stream().allMatch(m -> m.type() == MediaType.EBOOK));
    }

    @Test
    void findAll_filterByAvailability_returnsOnlyMatching() {
        mediaItemService.create(request("D", MediaType.MOVIE, "Action", 2008, false));
        List<MediaItemResponseDto> unavailable = mediaItemService.findAll(null, false);
        assertFalse(unavailable.isEmpty());
        assertTrue(unavailable.stream().noneMatch(MediaItemResponseDto::available));
    }

    @Test
    void findAll_filterByTypeAndAvailability() {
        mediaItemService.create(request("E", MediaType.GAME, "RPG", 2015, true));
        mediaItemService.create(request("F", MediaType.GAME, "RPG", 2011, false));
        List<MediaItemResponseDto> result = mediaItemService.findAll(MediaType.GAME, true);
        assertFalse(result.isEmpty());
        assertTrue(result.stream().allMatch(m -> m.type() == MediaType.GAME && m.available()));
    }

    @Test
    void update_modifiesFields() {
        MediaItemResponseDto created = mediaItemService.create(
                request("Old Title", MediaType.MOVIE, "Drama", 2000, true));
        MediaItemRequestDto updated = request("New Title", MediaType.MUSIC, "Pop", 2022, false);
        MediaItemResponseDto result = mediaItemService.update(created.id(), updated);
        assertEquals("New Title", result.title());
        assertEquals(MediaType.MUSIC, result.type());
        assertEquals(2022, result.releaseYear());
        assertFalse(result.available());
    }

    @Test
    void delete_removesItem() {
        MediaItemResponseDto created = mediaItemService.create(
                request("Temp", MediaType.EBOOK, "Misc", 2019, true));
        mediaItemService.delete(created.id());
        assertThrows(ResourceNotFoundException.class, () -> mediaItemService.findById(created.id()));
    }

    // ---- Bean validation ----

    @Test
    void validation_validRequest_hasNoViolations() {
        Set<ConstraintViolation<MediaItemRequestDto>> violations =
                validator.validate(request("Valid", MediaType.MOVIE, "Drama", 2010, true));
        assertTrue(violations.isEmpty());
    }

    @Test
    void validation_negativeReleaseYear_isRejected() {
        Set<ConstraintViolation<MediaItemRequestDto>> violations =
                validator.validate(request("Bad Year", MediaType.MOVIE, "Drama", -5, true));
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("releaseYear")));
    }

    @Test
    void validation_blankTitle_isRejected() {
        Set<ConstraintViolation<MediaItemRequestDto>> violations =
                validator.validate(request("   ", MediaType.MOVIE, "Drama", 2010, true));
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("title")));
    }

    @Test
    void validation_nullType_isRejected() {
        MediaItemRequestDto bad = new MediaItemRequestDto("No Type", null, "Drama", 2010, true);
        Set<ConstraintViolation<MediaItemRequestDto>> violations = validator.validate(bad);
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("type")));
    }
}
