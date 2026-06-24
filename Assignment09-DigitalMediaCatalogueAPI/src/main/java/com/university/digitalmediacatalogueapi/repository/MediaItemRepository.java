package com.university.digitalmediacatalogueapi.repository;

import com.university.digitalmediacatalogueapi.entity.MediaItem;
import com.university.digitalmediacatalogueapi.entity.MediaType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/** Spring Data JPA repository for MediaItem entities. */
public interface MediaItemRepository extends JpaRepository<MediaItem, Long> {
    List<MediaItem> findByType(MediaType type);
    List<MediaItem> findByAvailable(boolean available);
    List<MediaItem> findByTypeAndAvailable(MediaType type, boolean available);
}
