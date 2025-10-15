package com.library.publications.controllers;

import com.library.publications.api.CategoriesApi;

import com.library.dtos.CategoryRequestDTO;
import com.library.dtos.CategoryResponseDTO;
import com.library.dtos.PaginatedResponseDTO;

import com.library.publications.services.CategoryService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class CategoryController implements CategoriesApi {
    @Autowired
    private CategoryService categoryService;

    @Override
    public ResponseEntity<CategoryResponseDTO> createCategory(CategoryRequestDTO dto) {
        CategoryResponseDTO created = categoryService.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @Override
    public ResponseEntity<PaginatedResponseDTO> getAllCategories(Integer page, Integer size) {
        PaginatedResponseDTO categories = categoryService.getPaginated(page, size);
        return ResponseEntity.ok(categories);
    }

    @Override
    public ResponseEntity<PaginatedResponseDTO> getCategoriesByName(String name, Integer page, Integer size) {
        PaginatedResponseDTO categories = categoryService.getPaginatedByName(name, page, size);
        return ResponseEntity.ok(categories);
    }

    @Override
    public ResponseEntity<CategoryResponseDTO> getCategoryById(Integer id) {
        CategoryResponseDTO category = categoryService.getById(id);
        return ResponseEntity.ok(category);
    }

    @Override
    public ResponseEntity<CategoryResponseDTO> updateCategory(Integer id, CategoryRequestDTO dto) {
        CategoryResponseDTO updated = categoryService.update(id, dto);
        return ResponseEntity.ok(updated);
    }

    @Override
    public ResponseEntity<Void> deleteCategory(Integer id) {
        categoryService.delete(id);
        return ResponseEntity.noContent().build();
    }
}