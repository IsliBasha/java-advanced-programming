package com.university.digitalmediacatalogueapi.dto;

import com.university.digitalmediacatalogueapi.entity.MediaItem;
import com.university.digitalmediacatalogueapi.entity.MediaType;

/** Response DTO for all media-item endpoints. */
public record MediaItemResponseDto(
        Long id, String title, MediaType type, String genre, Integer releaseYear, boolean available
) {
    public static MediaItemResponseDto from(MediaItem m) {
        return new MediaItemResponseDto(m.getId(), m.getTitle(), m.getType(),
                m.getGenre(), m.getReleaseYear(), m.isAvailable());
    }
}
