package com.library.publications.services;

import com.library.dtos.PublicationCategoryRequestDTO;
import com.library.dtos.PublicationCategoryResponseDTO;
import com.library.dtos.PaginatedPublicationCategoryResponseDTO;
import com.library.entities.Publication;
import com.library.entities.Category;
import com.library.entities.PublicationCategory;
import com.library.publications.repositories.PublicationCategoryRepository;
import com.library.publications.repositories.PublicationRepository;
import com.library.publications.repositories.CategoryRepository;
import com.library.publications.mappers.PublicationCategoryMapper;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
import com.library.publications.utils.NormalizeParameter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.CacheEvict;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PublicationCategoryService {
    private final PublicationCategoryRepository publicationCategoryRepository;
    private final PublicationRepository publicationRepository;
    private final CategoryRepository categoryRepository;
    private final PublicationCategoryMapper publicationCategoryMapper;
    private final com.library.publications.config.PaginationConfig paginationConfig;

    @Transactional
    @CachePut(value = "publicationCategories", key = "#result.id")
    public PublicationCategoryResponseDTO createPublicationCategory(PublicationCategoryRequestDTO dto) {
        if (dto.getPublicationId() == null || dto.getPublicationId() <= 0) {
            log.error("ID de publicación inválido: {}", dto.getPublicationId());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El ID de la publicación debe ser un número positivo");
        }
        if (dto.getCategoryId() == null || dto.getCategoryId() <= 0) {
            log.error("ID de categoría inválido: {}", dto.getCategoryId());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El ID de la categoría debe ser un número positivo");
        }
        if (dto.getRelevanceScore() == null || dto.getRelevanceScore() < 1 || dto.getRelevanceScore() > 10) {
            log.error("RelevanceScore inválido: {}", dto.getRelevanceScore());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El puntaje de relevancia debe estar entre 1 y 10");
        }

        Publication publication = publicationRepository.findByIdAndIsDeletedFalse(dto.getPublicationId())
                .orElseThrow(() -> {
                    log.error("No se encontró la publicación con ID {}", dto.getPublicationId());
                    return new ResponseStatusException(HttpStatus.NOT_FOUND, "Publicación no encontrada con ID: " + dto.getPublicationId());
                });
        Category category = categoryRepository.findByIdAndIsDeletedFalse(dto.getCategoryId())
                .orElseThrow(() -> {
                    log.error("No se encontró la categoría con ID {}", dto.getCategoryId());
                    return new ResponseStatusException(HttpStatus.NOT_FOUND, "Categoría no encontrada con ID: " + dto.getCategoryId());
                });

        Optional<PublicationCategory> duplicate = publicationCategoryRepository
                .findByPublicationIdAndCategoryIdAndIsDeletedFalse(
                        dto.getPublicationId(), dto.getCategoryId());
        if (duplicate.isPresent()) {
            log.error("Ya existe una relación igual (publicationId={}, categoryId={}, relevanceScore={})", dto.getPublicationId(), dto.getCategoryId(), dto.getRelevanceScore());
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ya existe una relación publicación-categoría con esos valores");
        }

        PublicationCategory pc = new PublicationCategory();
        pc.setPublication(publication);
        pc.setCategory(category);
        pc.setRelevanceScore(dto.getRelevanceScore());
        pc.setIsDeleted(false);
        pc.setCreatedAt(LocalDateTime.now());
        pc.setUpdatedAt(LocalDateTime.now());
        PublicationCategory saved = publicationCategoryRepository.save(pc);
        return publicationCategoryMapper.toResponseDTO(saved);
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "publicationCategories", key = "#id")
    public PublicationCategoryResponseDTO getPublicationCategoryById(Integer id) {
        if (id == null || id <= 0) {
            log.error("ID de relación publicación-categoría inválido: {}", id);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El ID debe ser un número positivo");
        }
        PublicationCategory pc = publicationCategoryRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> {
                    log.error("No se encontró la relación publicación-categoría con ID {}", id);
                    return new ResponseStatusException(HttpStatus.NOT_FOUND, "Relación publicación-categoría no encontrada con ID: " + id);
                });
        return publicationCategoryMapper.toResponseDTO(pc);
    }

    @Transactional
    @CachePut(value = "publicationCategories", key = "#id")
    public PublicationCategoryResponseDTO updatePublicationCategory(Integer id, PublicationCategoryRequestDTO dto) {
        if (id == null || id <= 0) {
            log.error("ID de relación publicación-categoría inválido: {}", id);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El ID debe ser un número positivo");
        }
        PublicationCategory pc = publicationCategoryRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> {
                    log.error("No se encontró la relación publicación-categoría con ID {}", id);
                    return new ResponseStatusException(HttpStatus.NOT_FOUND, "Relación publicación-categoría no encontrada con ID: " + id);
                });
        if (dto.getPublicationId() == null || dto.getPublicationId() <= 0) {
            log.error("ID de publicación inválido: {}", dto.getPublicationId());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El ID de la publicación debe ser un número positivo");
        }
        if (dto.getCategoryId() == null || dto.getCategoryId() <= 0) {
            log.error("ID de categoría inválido: {}", dto.getCategoryId());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El ID de la categoría debe ser un número positivo");
        }
        if (dto.getRelevanceScore() == null || dto.getRelevanceScore() < 1 || dto.getRelevanceScore() > 10) {
            log.error("RelevanceScore inválido: {}", dto.getRelevanceScore());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El puntaje de relevancia debe estar entre 1 y 10");
        }
        Publication publication = publicationRepository.findByIdAndIsDeletedFalse(dto.getPublicationId())
                .orElseThrow(() -> {
                    log.error("No se encontró la publicación con ID {}", dto.getPublicationId());
                    return new ResponseStatusException(HttpStatus.NOT_FOUND, "Publicación no encontrada con ID: " + dto.getPublicationId());
                });
        Category category = categoryRepository.findByIdAndIsDeletedFalse(dto.getCategoryId())
                .orElseThrow(() -> {
                    log.error("No se encontró la categoría con ID {}", dto.getCategoryId());
                    return new ResponseStatusException(HttpStatus.NOT_FOUND, "Categoría no encontrada con ID: " + dto.getCategoryId());
                });

        if (
            !publication.getId().equals(pc.getPublication().getId()) ||
            !category.getId().equals(pc.getCategory().getId()) ||
            !dto.getRelevanceScore().equals(pc.getRelevanceScore())
        ) {
            Optional<PublicationCategory> duplicate = publicationCategoryRepository
                    .findByPublicationIdAndCategoryIdAndIsDeletedFalse(
                            dto.getPublicationId(), dto.getCategoryId());
            if (duplicate.isPresent()) {
                log.error("Ya existe una relación igual (publicationId={}, categoryId={}, relevanceScore={})", dto.getPublicationId(), dto.getCategoryId(), dto.getRelevanceScore());
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Ya existe una relación publicación-categoría con esos valores");
            }
        }

        pc.setPublication(publication);
        pc.setCategory(category);
        pc.setRelevanceScore(dto.getRelevanceScore());
        pc.setUpdatedAt(LocalDateTime.now());
        PublicationCategory updated = publicationCategoryRepository.save(pc);
        return publicationCategoryMapper.toResponseDTO(updated);
    }

    @Transactional
    @CacheEvict(value = "publicationCategories", key = "#id")
    public void deletePublicationCategory(Integer id) {
        if (id == null || id <= 0) {
            log.error("ID de relación publicación-categoría inválido: {}", id);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El ID debe ser un número positivo");
        }
        PublicationCategory pc = publicationCategoryRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> {
                    log.error("No se encontró la relación publicación-categoría con ID {}", id);
                    return new ResponseStatusException(HttpStatus.NOT_FOUND, "Relación publicación-categoría no encontrada con ID: " + id);
                });
        pc.setIsDeleted(true);
        pc.setUpdatedAt(LocalDateTime.now());
        publicationCategoryRepository.save(pc);
    }

    @Transactional(readOnly = true)
    public PaginatedPublicationCategoryResponseDTO getPublicationCategoriesByFilters(Integer publicationId, Integer categoryId, Integer minRelevance, Integer maxRelevance, Integer page) {
        if (page == null || page < 0) {
            log.error("Número de página inválido: {}", page);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El número de página debe ser 0 o mayor");
        }

        Integer normalizedPublicationId = NormalizeParameter.normalizeInteger(publicationId);
        Integer normalizedCategoryId = NormalizeParameter.normalizeInteger(categoryId);
        Integer normalizedMinRelevance = NormalizeParameter.normalizeInteger(minRelevance);
        Integer normalizedMaxRelevance = NormalizeParameter.normalizeInteger(maxRelevance);

        if (normalizedPublicationId != null && normalizedPublicationId < 0) {
            log.error("ID de publicación inválido en filtro: {}", normalizedPublicationId);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El ID de publicación en el filtro debe ser 0 o un número positivo");
        } else if (normalizedPublicationId != null && !publicationRepository.existsByIdAndIsDeletedFalse(normalizedPublicationId)) {
            log.error("Publicación no encontrada para el filtro publicationId={}", normalizedPublicationId);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Publicación no encontrada con ID: " + normalizedPublicationId);
        }

        if (normalizedCategoryId != null && normalizedCategoryId < 0) {
            log.error("ID de categoría inválido en filtro: {}", normalizedCategoryId);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El ID de categoría en el filtro debe ser 0 o un número positivo");
        } else if (normalizedCategoryId != null && !categoryRepository.existsByIdAndIsDeletedFalse(normalizedCategoryId)) {
            log.error("Categoría no encontrada para el filtro categoryId={}", normalizedCategoryId);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Categoría no encontrada con ID: " + normalizedCategoryId);
        }

        Pageable pageable = PageRequest.of(page, paginationConfig.getPageSize());
        Page<PublicationCategory> pcPage = publicationCategoryRepository.findByFilters(
                normalizedPublicationId, normalizedCategoryId, normalizedMinRelevance, normalizedMaxRelevance, pageable);
        List<PublicationCategoryResponseDTO> dtos = pcPage.getContent().stream()
                .map(publicationCategoryMapper::toResponseDTO)
                .collect(Collectors.toList());
        PaginatedPublicationCategoryResponseDTO response = new PaginatedPublicationCategoryResponseDTO();
        response.setData(dtos);
        response.setPageSize(pcPage.getSize());
        response.setTotalItems((int) pcPage.getTotalElements());
        response.setCurrentPage(page);
        response.setTotalPages(pcPage.getTotalPages());
        response.setNext(pcPage.hasNext());
        response.setPrev(pcPage.hasPrevious());
        return response;
    }
}