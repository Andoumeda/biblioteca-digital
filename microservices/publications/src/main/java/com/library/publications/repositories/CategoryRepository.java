package com.library.publications.repositories;

import com.library.entities.Category;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Integer> {
    // Buscar categorías no eliminadas
    @Query("SELECT c FROM Category c WHERE c.isDeleted = false")
    Page<Category> findAllNotDeleted(Pageable pageable);

    // Buscar por nombre (case insensitive)
    @Query("SELECT c FROM Category c WHERE LOWER(c.name) LIKE LOWER(CONCAT('%', :name, '%')) AND c.isDeleted = false")
    List<Category> findByNameNotDeleted(String name);

    // Buscar por nombre (case insensitive)
    @Query("SELECT c FROM Category c WHERE LOWER(c.name) LIKE LOWER(CONCAT('%', :text, '%')) AND c.isDeleted = false")
    Page<Category> findByNameNotDeleted(@Param("text") String text, Pageable pageable);

    // Buscar por ID
    @Query("SELECT c FROM Category c WHERE c.id = :id AND c.isDeleted = false")
    Optional<Category> findByIdNotDeleted(@Param("id") Integer id);
}