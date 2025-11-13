package com.library.publications.controllers;

import com.library.dtos.PublicationCategoryRequestDTO;
import com.library.dtos.PublicationCategoryResponseDTO;
import com.library.dtos.PaginatedPublicationCategoryResponseDTO;
import com.library.publications.api.PublicationCategoriesApi;
import com.library.publications.services.PublicationCategoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
@Slf4j
public class PublicationCategoryController implements PublicationCategoriesApi {

    private final PublicationCategoryService publicationCategoryService;

    @Override
    public ResponseEntity<PublicationCategoryResponseDTO> createPublicationCategory(PublicationCategoryRequestDTO dto) {
        log.info("Creando relación publicación-categoría: publicationId={}, categoryId={}", dto.getPublicationId(), dto.getCategoryId());
        return ResponseEntity.status(201).body(publicationCategoryService.createPublicationCategory(dto));
    }

    @Override
    public ResponseEntity<Void> deletePublicationCategory(Integer id) {
        log.info("Eliminando relación publicación-categoría id={}", id);
        publicationCategoryService.deletePublicationCategory(id);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<PublicationCategoryResponseDTO> getPublicationCategoryById(Integer id) {
        log.info("Obteniendo relación publicación-categoría id={}", id);
        return ResponseEntity.ok(publicationCategoryService.getPublicationCategoryById(id));
    }

    @Override
    public ResponseEntity<PaginatedPublicationCategoryResponseDTO> getPublicationCategoriesByFilters(
            Integer publicationId, Integer categoryId, Integer minRelevance, Integer maxRelevance, Integer page) {
        log.info("Buscando relaciones publicación-categoría con filtros: publicationId={}, categoryId={}, min_relevance={}, max_relevance={}, page={}",
                publicationId, categoryId, minRelevance, maxRelevance, page);
        return ResponseEntity.ok(
            publicationCategoryService.getPublicationCategoriesByFilters(publicationId, categoryId, minRelevance, maxRelevance, page)
        );
    }

    @Override
    public ResponseEntity<PublicationCategoryResponseDTO> updatePublicationCategory(Integer id, PublicationCategoryRequestDTO dto) {
        log.info("Actualizando relación publicación-categoría id={}", id);
        return ResponseEntity.ok(publicationCategoryService.updatePublicationCategory(id, dto));
    }
}
