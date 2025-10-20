package com.library.publications.services;

import com.library.dtos.CategoryRequestDTO;
import com.library.dtos.CategoryResponseDTO;
import com.library.dtos.PaginatedResponseDTO;

import com.library.entities.Category;

import com.library.publications.repositories.CategoryRepository;

import com.library.publications.utils.PaginationUtil;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final ModelMapper modelMapper;
    private final Logger logger = LoggerFactory.getLogger(CategoryService.class);

    @Transactional
    public CategoryResponseDTO create(CategoryRequestDTO dto) {
        try {
            // Validar que no exista una categoría con el mismo nombre
            logger.debug("Consulta a la BD: SELECT c FROM Category c WHERE LOWER(c.name) LIKE LOWER(CONCAT('%', :name, '%')) AND c.isDeleted = false");
            if (!categoryRepository.findByNameContainingIgnoreCaseAndIsDeletedFalse(dto.getName()).isEmpty()) {
                logger.error("Una categoría ya existe con el nombre {}", dto.getName());
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Categoría ya existe con nombre: " + dto.getName());
            }

            Category category = new Category();
            category.setName(dto.getName());

            Category saved = categoryRepository.save(category);
            return modelMapper.map(saved, CategoryResponseDTO.class);
        } catch (Exception e) {
            logger.error("Error al crear la categoría: {}", e.getMessage(), e);
            throw new RuntimeException("Error al crear la categoría: " + e.getMessage(), e);
        }
    }

    @Transactional(readOnly = true)
    public PaginatedResponseDTO getPaginated(Integer page, Integer size) {
        logger.debug("Consulta a la BD: SELECT c FROM Category c WHERE c.isDeleted = false");
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Category> categoriesPage = categoryRepository.findAllByIsDeletedFalse(pageable);

        if (categoriesPage.isEmpty()) {
            logger.warn("No se encontraron categorías");
            throw new ResponseStatusException(HttpStatus.NO_CONTENT, "No se encontraron categorías");
        } else
            logger.info("Se encontraron {} categorías", categoriesPage.getTotalElements());

        return PaginationUtil.buildPaginatedResponse(categoriesPage, CategoryResponseDTO.class);
    }

    @Transactional(readOnly = true)
    public PaginatedResponseDTO getPaginatedByName(String name, Integer page, Integer size) {
        logger.debug("Consulta a la BD: SELECT c FROM Category c WHERE LOWER(c.name) LIKE LOWER(CONCAT('%', :name, '%')) AND c.isDeleted = false");
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Category> categoriesPage = categoryRepository.findByNameContainingIgnoreCaseAndIsDeletedFalse(name, pageable);

        if (categoriesPage.isEmpty()) {
            logger.warn("No se encontraron categorías con el nombre {}", name);
            throw new ResponseStatusException(HttpStatus.NO_CONTENT, "No se encontraron categorías con el nombre " + name);
        } else
            logger.info("Se encontraron {} categorías con el nombre {}", categoriesPage.getTotalElements(), name);

        return PaginationUtil.buildPaginatedResponse(categoriesPage, CategoryResponseDTO.class);
    }

    @Transactional(readOnly = true)
    public CategoryResponseDTO getById(Integer id) {
        logger.debug("Consulta a la BD: SELECT c FROM Category c WHERE c.id = :id AND c.isDeleted = false");
        Category category = categoryRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> {
                    logger.error("No se encontró la categoría con ID {}", id);
                    return new ResponseStatusException(HttpStatus.NOT_FOUND, "Categoría no encontrada con ID: " + id);
                });

        return modelMapper.map(category, CategoryResponseDTO.class);
    }

    @Transactional
    public CategoryResponseDTO update(Integer id, CategoryRequestDTO dto) {
        logger.debug("Consulta a la BD: SELECT c FROM Category c WHERE c.id = :id AND c.isDeleted = false");
        Category category = categoryRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> {
                    logger.error("No se encontró la categoría con ID {}", id);
                    return new ResponseStatusException(HttpStatus.NOT_FOUND, "Categoría no encontrada con ID: " + id);
                });

        // Cambiar sólo si no es nombre duplicado
        logger.debug("Consulta a la BD: SELECT c FROM Category c WHERE LOWER(c.name) LIKE LOWER(CONCAT('%', :name, '%')) AND c.isDeleted = false");
        if (dto.getName() == null ||
                dto.getName().equals(category.getName()) ||
                !categoryRepository.findByNameContainingIgnoreCaseAndIsDeletedFalse(dto.getName()).isEmpty()) {
            logger.error("Una categoría ya existe con el nombre {}", dto.getName());
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Categoría ya existe con nombre: " + dto.getName());
        }

        category.setName(dto.getName());
        category.setUpdatedAt(LocalDateTime.now());

        Category updated = categoryRepository.save(category);
        return modelMapper.map(updated, CategoryResponseDTO.class);
    }

    @Transactional
    public void delete(Integer id) {
        logger.debug("Consulta a la BD: SELECT c FROM Category c WHERE c.id = :id AND c.isDeleted = false");
        Category category = categoryRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> {
                    logger.error("No se encontró la categoría con ID {}", id);
                    return new ResponseStatusException(HttpStatus.NOT_FOUND, "Categoría no encontrada con ID: " + id);
                });

        // Soft delete (borrado lógico)
        category.setIsDeleted(true);
        category.setUpdatedAt(LocalDateTime.now());
        categoryRepository.save(category);
    }
}