package com.university.digitalmedia;

import com.university.digitalmedia.model.MediaItem;
import com.university.digitalmedia.model.MediaType;
import com.university.digitalmedia.service.MediaCollectionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("MediaCollectionService Tests")
class MediaCollectionServiceTest {

    private MediaCollectionService service;

    @BeforeEach
    void setUp() {
        service = new MediaCollectionService();
        service.addItem(new MediaItem("M001", "Inception",      MediaType.MOVIE, "Sci-Fi", 2010, 148));
        service.addItem(new MediaItem("M002", "Clean Code",     MediaType.EBOOK, "Tech",   2008, null));
        service.addItem(new MediaItem("M003", "Interstellar",   MediaType.MOVIE, "Sci-Fi", 2014, 169));
        service.addItem(new MediaItem("M004", "Bohemian",       MediaType.MUSIC, "Rock",   1975, 6));
    }

    @Test @DisplayName("addItem populates all collections")
    void testAdd() {
        assertEquals(4, service.getAllItems().size());
        assertTrue(service.getGenres().contains("Sci-Fi"));
        assertEquals(2, service.getItemsByGenre().get("Sci-Fi").size());
    }

    @Test @DisplayName("findById returns present Optional")
    void testFindById() {
        assertTrue(service.findById("M002").isPresent());
        assertTrue(service.findById("M999").isEmpty());
    }

    @Test @DisplayName("findByExactTitle returns correct Optional")
    void testFindByExactTitle() {
        assertTrue(service.findByExactTitle("Inception").isPresent());
        assertTrue(service.findByExactTitle("inception").isEmpty()); // case-sensitive
    }

    @Test @DisplayName("findByReleaseYearRange filters correctly")
    void testYearRange() {
        List<MediaItem> result = service.findByReleaseYearRange(2010, 2015);
        assertEquals(2, result.size());
    }

    @Test @DisplayName("findByType returns only matching type")
    void testFindByType() {
        List<MediaItem> movies = service.findByType(MediaType.MOVIE);
        assertEquals(2, movies.size());
        assertTrue(movies.stream().allMatch(i -> i.type() == MediaType.MOVIE));
    }

    @Test @DisplayName("findByTypeGenreAndMinDuration applies all three filters")
    void testCombinedFilter() {
        List<MediaItem> result = service.findByTypeGenreAndMinDuration(MediaType.MOVIE, "Sci-Fi", 160);
        assertEquals(1, result.size());
        assertEquals("Interstellar", result.get(0).title());
    }

    @Test @DisplayName("mapTitlesToDurations excludes null-duration items")
    void testTitlesToDurations() {
        Map<String, Integer> map = service.mapTitlesToDurations();
        assertTrue(map.containsKey("Inception"));
        assertFalse(map.containsKey("Clean Code")); // ebook has no duration
        assertEquals(148, map.get("Inception"));
    }

    @Test @DisplayName("toSummaries maps fields correctly")
    void testToSummaries() {
        var summaries = service.toSummaries();
        assertEquals(4, summaries.size());
        assertTrue(summaries.stream().anyMatch(s -> s.title().equals("Inception") && s.type().equals("MOVIE")));
    }

    @Test @DisplayName("removeItem updates derived collections")
    void testRemove() {
        service.removeItem("M001");
        assertEquals(3, service.getAllItems().size());
        assertEquals(1, service.getItemsByGenre().get("Sci-Fi").size());
    }
}
