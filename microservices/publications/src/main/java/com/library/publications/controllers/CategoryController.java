package com.library.publications.controllers;

import com.library.publications.api.CategoriesApi;

import com.library.dtos.CategoryRequestDTO;
import com.library.dtos.CategoryResponseDTO;
import com.library.dtos.PaginatedResponseDTO;

import com.library.publications.services.CategoryService;

import org.springframework.beans.factory.annotation.Autowired;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class CategoryController implements CategoriesApi {
    @Autowired
    private CategoryService categoryService;
    private final Logger logger = LoggerFactory.getLogger(CategoryController.class);

    @Override
    public ResponseEntity<CategoryResponseDTO> createCategory(CategoryRequestDTO dto) {
        logger.info("Petición (POST /categories) para crear una categoría con nombre: {}", dto.getName());
        CategoryResponseDTO created = categoryService.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @Override
    public ResponseEntity<PaginatedResponseDTO> getAllCategories(Integer page, Integer size) {
        logger.info("Petición (GET /categories/page/{page}/size/{size}) para obtener todas las categorías - página: {}, tamaño: {}", page, size);
        PaginatedResponseDTO categories = categoryService.getPaginated(page, size);
        return ResponseEntity.ok(categories);
    }

    @Override
    public ResponseEntity<PaginatedResponseDTO> getCategoriesByName(String name, Integer page, Integer size) {
        logger.info("Petición (GET /categories/name/{name}/page/{page}/size/{size}) para obtener categorías por nombre: {} - página: {}, tamaño: {}", name, page, size);
        PaginatedResponseDTO categories = categoryService.getPaginatedByName(name, page, size);
        return ResponseEntity.ok(categories);
    }

    @Override
    public ResponseEntity<CategoryResponseDTO> getCategoryById(Integer id) {
        logger.info("Petición (GET /categories/{id}) para obtener categoría por ID: {}", id);
        CategoryResponseDTO category = categoryService.getById(id);
        return ResponseEntity.ok(category);
    }

    @Override
    public ResponseEntity<CategoryResponseDTO> updateCategory(Integer id, CategoryRequestDTO dto) {
        logger.info("Petición (PUT /categories/{id}) para actualizar categoría con ID: {} con nuevo nombre: {}", id, dto.getName());
        CategoryResponseDTO updated = categoryService.update(id, dto);
        return ResponseEntity.ok(updated);
    }

    @Override
    public ResponseEntity<Void> deleteCategory(Integer id) {
        logger.info("Petición (DELETE /categories/{id}) para eliminar categoría con ID: {}", id);
        categoryService.delete(id);
        return ResponseEntity.noContent().build();
    }
}