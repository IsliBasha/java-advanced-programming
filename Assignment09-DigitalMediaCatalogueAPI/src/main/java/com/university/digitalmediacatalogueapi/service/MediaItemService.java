package com.university.digitalmediacatalogueapi.service;

import com.university.digitalmediacatalogueapi.dto.MediaItemRequestDto;
import com.university.digitalmediacatalogueapi.dto.MediaItemResponseDto;
import com.university.digitalmediacatalogueapi.entity.MediaItem;
import com.university.digitalmediacatalogueapi.entity.MediaType;
import com.university.digitalmediacatalogueapi.exception.ResourceNotFoundException;
import com.university.digitalmediacatalogueapi.repository.MediaItemRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Business logic for the digital media catalogue.
 *
 * <p>Owns lookups, filtering, and persistence so that the controller stays a
 * thin HTTP layer with no business decisions of its own.
 */
@Service
@Transactional
public class MediaItemService {

    private final MediaItemRepository mediaItemRepository;

    public MediaItemService(MediaItemRepository mediaItemRepository) {
        this.mediaItemRepository = mediaItemRepository;
    }

    public MediaItemResponseDto create(MediaItemRequestDto request) {
        MediaItem item = new MediaItem(
                request.title(),
                request.type(),
                request.genre(),
                request.releaseYear(),
                request.available());
        return MediaItemResponseDto.from(mediaItemRepository.save(item));
    }

    @Transactional(readOnly = true)
    public MediaItemResponseDto findById(Long id) {
        return MediaItemResponseDto.from(getOrThrow(id));
    }

    /**
     * Returns catalogue items, optionally narrowed by type and/or availability.
     * Any combination of the two filters is supported.
     */
    @Transactional(readOnly = true)
    public List<MediaItemResponseDto> findAll(MediaType type, Boolean available) {
        List<MediaItem> items;
        if (type != null && available != null) {
            items = mediaItemRepository.findByTypeAndAvailable(type, available);
        } else if (type != null) {
            items = mediaItemRepository.findByType(type);
        } else if (available != null) {
            items = mediaItemRepository.findByAvailable(available);
        } else {
            items = mediaItemRepository.findAll();
        }
        return items.stream().map(MediaItemResponseDto::from).toList();
    }

    public MediaItemResponseDto update(Long id, MediaItemRequestDto request) {
        MediaItem item = getOrThrow(id);
        item.setTitle(request.title());
        item.setType(request.type());
        item.setGenre(request.genre());
        item.setReleaseYear(request.releaseYear());
        item.setAvailable(request.available());
        return MediaItemResponseDto.from(mediaItemRepository.save(item));
    }

    public void delete(Long id) {
        mediaItemRepository.delete(getOrThrow(id));
    }

    private MediaItem getOrThrow(Long id) {
        return mediaItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Media item not found: id=" + id));
    }
}
