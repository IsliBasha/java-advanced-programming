package com.university.digitalmediacatalogueapi.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

/** JPA entity representing a single item in the digital media catalogue. */
@Entity
@Table(name = "media_items")
public class MediaItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Title is required")
    @Column(nullable = false)
    private String title;

    @Enumerated(EnumType.STRING)
    @NotNull(message = "Media type is required")
    @Column(nullable = false)
    private MediaType type;

    @NotBlank(message = "Genre is required")
    @Column(nullable = false)
    private String genre;

    @NotNull(message = "Release year is required")
    @PositiveOrZero(message = "Release year cannot be negative")
    @Column(name = "release_year", nullable = false)
    private Integer releaseYear;

    @Column(nullable = false)
    private boolean available;

    protected MediaItem() {}

    public MediaItem(String title, MediaType type, String genre, Integer releaseYear, boolean available) {
        this.title = title;
        this.type = type;
        this.genre = genre;
        this.releaseYear = releaseYear;
        this.available = available;
    }

    public Long getId() { return id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public MediaType getType() { return type; }
    public void setType(MediaType type) { this.type = type; }
    public String getGenre() { return genre; }
    public void setGenre(String genre) { this.genre = genre; }
    public Integer getReleaseYear() { return releaseYear; }
    public void setReleaseYear(Integer releaseYear) { this.releaseYear = releaseYear; }
    public boolean isAvailable() { return available; }
    public void setAvailable(boolean available) { this.available = available; }
}
