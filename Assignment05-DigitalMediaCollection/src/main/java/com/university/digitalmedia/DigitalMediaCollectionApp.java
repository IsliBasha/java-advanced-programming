package com.university.digitalmedia;

import com.university.digitalmedia.model.MediaItem;
import com.university.digitalmedia.model.MediaType;
import com.university.digitalmedia.service.MediaCollectionService;

import java.util.List;
import java.util.Map;

/**
 * Entry point for Assignment 05 – Digital Media Collection Manager.
 */
public class DigitalMediaCollectionApp {

    public static void main(String[] args) {
        MediaCollectionService service = new MediaCollectionService();

        // ── Seed data ─────────────────────────────────────────────────────
        service.addItem(new MediaItem("M001", "Inception",                MediaType.MOVIE, "Sci-Fi",  2010, 148));
        service.addItem(new MediaItem("M002", "The Dark Knight",          MediaType.MOVIE, "Action",  2008, 152));
        service.addItem(new MediaItem("M003", "Bohemian Rhapsody",        MediaType.MUSIC, "Rock",    1975, 6));
        service.addItem(new MediaItem("M004", "Hotel California",         MediaType.MUSIC, "Rock",    1977, 7));
        service.addItem(new MediaItem("M005", "Clean Code",               MediaType.EBOOK, "Tech",    2008, null));
        service.addItem(new MediaItem("M006", "The Pragmatic Programmer",  MediaType.EBOOK, "Tech",    1999, null));
        service.addItem(new MediaItem("M007", "Interstellar",             MediaType.MOVIE, "Sci-Fi",  2014, 169));
        service.addItem(new MediaItem("M008", "Stairway to Heaven",       MediaType.MUSIC, "Rock",    1971, 8));
        service.addItem(new MediaItem("M009", "Minecraft",                MediaType.GAME,  "Sandbox", 2011, null));
        service.addItem(new MediaItem("M010", "Portal 2",                 MediaType.GAME,  "Puzzle",  2011, null));

        printHeader("ALL MEDIA ITEMS");
        printItems(service.getAllItems());

        printHeader("GENRES Set");
        service.getGenres().stream().sorted().forEach(g -> System.out.println("  " + g));

        printHeader("ITEMS BY GENRE Map<String, List<MediaItem>>");
        service.getItemsByGenre().entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .forEach(e -> {
                    System.out.printf("  %-12s → ", e.getKey());
                    System.out.println(e.getValue().stream().map(MediaItem::title).toList());
                });

        printHeader("Optional LOOKUP BY ID: M005");
        service.findById("M005").ifPresentOrElse(
                i -> System.out.printf("  Found: \"%s\" (%s, %d)%n", i.title(), i.type(), i.releaseYear()),
                () -> System.out.println("  Not found."));

        printHeader("Optional LOOKUP BY EXACT TITLE: \"Inception\"");
        service.findByExactTitle("Inception").ifPresentOrElse(
                i -> System.out.printf("  Found: %s (%d min, %d)%n", i.title(), i.durationMinutes(), i.releaseYear()),
                () -> System.out.println("  Not found."));

        printHeader("RELEASE YEAR RANGE: 2008 – 2014");
        printItems(service.findByReleaseYearRange(2008, 2014));

        printHeader("FILTER BY TYPE: MOVIE");
        printItems(service.findByType(MediaType.MOVIE));

        printHeader("COMBINED FILTER: MOVIE + Sci-Fi + duration >= 150 min");
        printItems(service.findByTypeGenreAndMinDuration(MediaType.MOVIE, "Sci-Fi", 150));

        printHeader("TITLE → DURATION MAP");
        service.mapTitlesToDurations().entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .forEach(e -> System.out.printf("  %-35s → %d min%n", e.getKey(), e.getValue()));

        printHeader("STREAM MAP → MediaSummaryDto");
        service.toSummaries().forEach(dto ->
                System.out.printf("  %-35s | %-6s | %d%n", dto.title(), dto.type(), dto.releaseYear()));
    }

    private static void printHeader(String title) {
        System.out.println();
        System.out.println("═".repeat(68));
        System.out.println("  " + title);
        System.out.println("─".repeat(68));
    }

    private static void printItems(List<MediaItem> list) {
        System.out.printf("  %-6s %-35s %-6s %-10s %4s  %s%n",
                "ID", "Title", "Type", "Genre", "Year", "Duration");
        System.out.println("  " + "─".repeat(66));
        list.forEach(i -> System.out.printf(
                "  %-6s %-35s %-6s %-10s %4d  %s%n",
                i.mediaId(), i.title(), i.type(), i.genre(), i.releaseYear(),
                i.durationMinutes() != null ? i.durationMinutes() + " min" : "N/A"));
    }
}
