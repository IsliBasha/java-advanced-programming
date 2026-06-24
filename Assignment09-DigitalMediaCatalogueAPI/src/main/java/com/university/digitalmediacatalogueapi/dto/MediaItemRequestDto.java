package com.university.digitalmediacatalogueapi.dto;

import com.university.digitalmediacatalogueapi.entity.MediaType;
import jakarta.validation.constraints.*;

/** Request DTO for creating or updating a media item. */
public record MediaItemRequestDto(
        @NotBlank(message = "Title is required") String title,
        @NotNull(message = "Media type is required") MediaType type,
        @NotBlank(message = "Genre is required") String genre,
        @NotNull(message = "Release year is required")
        @PositiveOrZero(message = "Release year cannot be negative") Integer releaseYear,
        @NotNull(message = "Availability flag is required") Boolean available
) {}
