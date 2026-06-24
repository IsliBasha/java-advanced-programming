package com.university.digitalmedia.service;

import com.university.digitalmedia.dto.MediaSummaryDto;
import com.university.digitalmedia.model.MediaItem;
import com.university.digitalmedia.model.MediaType;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Manages a digital media collection using List, Set, and Map.
 */
public class MediaCollectionService {

    private final List<MediaItem> items = new ArrayList<>();
    private final Set<String> genres = new HashSet<>();
    private final Map<String, List<MediaItem>> itemsByGenre = new HashMap<>();

    // ── CRUD ─────────────────────────────────────────────────────────────

    public void addItem(MediaItem item) {
        items.add(item);
        genres.add(item.genre());
        itemsByGenre.computeIfAbsent(item.genre(), k -> new ArrayList<>()).add(item);
    }

    public boolean removeItem(String mediaId) {
        boolean removed = items.removeIf(i -> i.mediaId().equals(mediaId));
        if (removed) rebuildDerivedCollections();
        return removed;
    }

    public boolean updateItem(String mediaId, MediaItem updated) {
        for (int i = 0; i < items.size(); i++) {
            if (items.get(i).mediaId().equals(mediaId)) {
                items.set(i, updated);
                rebuildDerivedCollections();
                return true;
            }
        }
        return false;
    }

    // ── QUERY ─────────────────────────────────────────────────────────────

    /** Optional lookup by mediaId. */
    public Optional<MediaItem> findById(String mediaId) {
        return items.stream()
                .filter(i -> i.mediaId().equals(mediaId))
                .findFirst();
    }

    /** Optional lookup by exact title (case-sensitive). */
    public Optional<MediaItem> findByExactTitle(String title) {
        return items.stream()
                .filter(i -> i.title().equals(title))
                .findFirst();
    }

    /** Stream filter by release year range [fromYear, toYear] inclusive. */
    public List<MediaItem> findByReleaseYearRange(int fromYear, int toYear) {
        return items.stream()
                .filter(i -> i.releaseYear() >= fromYear && i.releaseYear() <= toYear)
                .collect(Collectors.toList());
    }

    /** Stream filter by media type. */
    public List<MediaItem> findByType(MediaType type) {
        return items.stream()
                .filter(i -> i.type() == type)
                .collect(Collectors.toList());
    }

    /**
     * Combined filter: type AND genre AND minimum duration.
     * All three conditions must match simultaneously.
     */
    public List<MediaItem> findByTypeGenreAndMinDuration(MediaType type, String genre, int minDurationMinutes) {
        return items.stream()
                .filter(i -> i.type() == type
                        && i.genre().equalsIgnoreCase(genre)
                        && i.durationMinutes() != null
                        && i.durationMinutes() >= minDurationMinutes)
                .collect(Collectors.toList());
    }

    /** Map titles to durations (title → durationMinutes). Items without duration are excluded. */
    public Map<String, Integer> mapTitlesToDurations() {
        return items.stream()
                .filter(i -> i.durationMinutes() != null)
                .collect(Collectors.toMap(
                        MediaItem::title,
                        MediaItem::durationMinutes,
                        (existing, duplicate) -> existing
                ));
    }

    /** Stream transform to lightweight DTO. */
    public List<MediaSummaryDto> toSummaries() {
        return items.stream()
                .map(i -> new MediaSummaryDto(i.title(), i.type().name(), i.releaseYear()))
                .collect(Collectors.toList());
    }

    // ── ACCESSORS ─────────────────────────────────────────────────────────

    public List<MediaItem> getAllItems() {
        return Collections.unmodifiableList(items);
    }

    public Set<String> getGenres() {
        return Collections.unmodifiableSet(genres);
    }

    public Map<String, List<MediaItem>> getItemsByGenre() {
        return Collections.unmodifiableMap(itemsByGenre);
    }

    // ── PRIVATE HELPERS ───────────────────────────────────────────────────

    private void rebuildDerivedCollections() {
        genres.clear();
        itemsByGenre.clear();
        for (MediaItem item : items) {
            genres.add(item.genre());
            itemsByGenre.computeIfAbsent(item.genre(), k -> new ArrayList<>()).add(item);
        }
    }
}
