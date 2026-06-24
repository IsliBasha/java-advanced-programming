package com.university.digitalmedia.model;

/**
 * Represents an item in the digital media collection.
 * {@code durationMinutes} may be null for e-books (no concept of duration).
 */
public record MediaItem(
        String mediaId,
        String title,
        MediaType type,
        String genre,
        int releaseYear,
        Integer durationMinutes
) {
    public MediaItem {
        if (releaseYear < 1888) throw new IllegalArgumentException("Release year predates cinema (1888).");
    }
}
