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
            if (!categoryRepository.findByNameNotDeleted(dto.getName()).isEmpty()) {
                logger.error("Una categoría ya existe con el nombre " + dto.getName());
                throw new IllegalArgumentException(
                    String.format("Categoría ya existe con nombre: %s", dto.getName())
                );
            }

            Category category = new Category();
            category.setName(dto.getName());

            Category saved = categoryRepository.save(category);
            return modelMapper.map(saved, CategoryResponseDTO.class);
        } catch (Exception e) {
            logger.error("Error al crear la categoría: " + e.getMessage(), e);
            throw new RuntimeException("Error al crear la categoría: " + e.getMessage(), e);
        }
    }

    @Transactional(readOnly = true)
    public PaginatedResponseDTO getPaginated(Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Category> categoriesPage = categoryRepository.findAllNotDeleted(pageable);

        if (categoriesPage.isEmpty()) {
            logger.warn("No se encontraron categorías");
            throw new IllegalArgumentException("No se encontraron categorías");
        } else
            logger.info("Se encontraron " + categoriesPage.getTotalElements() + " categorías");

        return PaginationUtil.buildPaginatedResponse(categoriesPage, CategoryResponseDTO.class);
    }

    @Transactional(readOnly = true)
    public PaginatedResponseDTO getPaginatedByName(String name, Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Category> categoriesPage = categoryRepository.findByNameNotDeleted(name, pageable);

        if (categoriesPage.isEmpty()) {
            logger.warn("No se encontraron categorías con el nombre " + name);
            throw new IllegalArgumentException("No se encontraron categorías con el nombre " + name);
        } else
            logger.info("Se encontraron " + categoriesPage.getTotalElements() + " categorías con el nombre " + name);

        return PaginationUtil.buildPaginatedResponse(categoriesPage, CategoryResponseDTO.class);
    }

    @Transactional(readOnly = true)
    public CategoryResponseDTO getById(Integer id) {
        Category category = categoryRepository.findByIdNotDeleted(id)
                .orElseThrow(() -> {
                    logger.error("No se encontró la categoría con ID " + id);
                    return new IllegalArgumentException(
                        String.format("Categoría no encontrada con ID: %s", id)
                    );
                });

        return modelMapper.map(category, CategoryResponseDTO.class);
    }

    @Transactional
    public CategoryResponseDTO update(Integer id, CategoryRequestDTO dto) {
        Category category = categoryRepository.findByIdNotDeleted(id)
                .orElseThrow(() -> {
                    logger.error("No se encontró la categoría con ID " + id);
                    return new IllegalArgumentException(
                        String.format("Categoría no encontrada con ID: %s", id)
                    );
                });

        // Cambiar sólo si no es nombre duplicado
        if (dto.getName() == null || dto.getName().equals(category.getName()) || !categoryRepository.findByNameNotDeleted(dto.getName()).isEmpty()) {
            logger.error("Una categoría ya existe con el nombre " + dto.getName());
            throw new IllegalArgumentException(
                String.format("Categoría ya existe con nombre: %s", dto.getName())
            );
        }

        category.setName(dto.getName());
        category.setUpdatedAt(LocalDateTime.now());

        Category updated = categoryRepository.save(category);
        return modelMapper.map(updated, CategoryResponseDTO.class);
    }

    @Transactional
    public void delete(Integer id) {
        Category category = categoryRepository.findByIdNotDeleted(id)
                .orElseThrow(() -> {
                    logger.error("No se encontró la categoría con ID " + id);
                    return new IllegalArgumentException(
                        String.format("Categoría no encontrada con ID: %s", id)
                    );
                });

        // Soft delete (borrado lógico)
        category.setIsDeleted(true);
        category.setUpdatedAt(LocalDateTime.now());
        categoryRepository.save(category);
    }
}