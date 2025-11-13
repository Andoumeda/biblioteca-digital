package com.library.publications.repositories;

import com.library.entities.Category;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Integer> {
    // Buscar categorías no eliminadas
    Page<Category> findAllByIsDeletedFalse(Pageable pageable);

    // Buscar por nombre (case insensitive)
    List<Category> findByNameContainingIgnoreCaseAndIsDeletedFalse(String name);

    // Buscar por nombre (case insensitive) con paginación
    Page<Category> findByNameContainingIgnoreCaseAndIsDeletedFalse(String text, Pageable pageable);

    // Buscar por ID
    Optional<Category> findByIdAndIsDeletedFalse(Integer id);

    boolean existsByIdAndIsDeletedFalse(Integer id);
}