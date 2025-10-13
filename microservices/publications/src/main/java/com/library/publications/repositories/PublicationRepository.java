package com.library.publications.repositories;

import com.library.entities.Publication;
import com.library.entities.Publication.PublicationState;
import com.library.entities.UserProfile;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PublicationRepository extends JpaRepository<Publication, Integer> {
    // Buscar todas las publicaciones no eliminadas con paginación
    @Query("SELECT p FROM Publication p WHERE p.isDeleted = false")
    Page<Publication> findAllNotDeleted(Pageable pageable);

    // Buscar por título con paginación (case insensitive)
    @Query("SELECT p FROM Publication p WHERE LOWER(p.title) LIKE LOWER(CONCAT('%', :text, '%')) AND p.isDeleted = false")
    Page<Publication> findByTitleNotDeleted(@Param("text") String text, Pageable pageable);

    // Buscar por descripción con paginación (case insensitive)
    @Query("SELECT p FROM Publication p WHERE LOWER(p.description) LIKE LOWER(CONCAT('%', :text, '%')) AND p.isDeleted = false")
    Page<Publication> findByDescriptionNotDeleted(@Param("text") String text, Pageable pageable);

    // Buscar por estado con paginación
    @Query("SELECT p FROM Publication p WHERE p.state = :state AND p.isDeleted = false")
    Page<Publication> findByStateNotDeleted(@Param("state") PublicationState state, Pageable pageable);

    // Buscar por perfil de usuario con paginación
    @Query("SELECT p FROM Publication p WHERE p.userProfile.id = :userProfileId AND p.isDeleted = false")
    Page<Publication> findByUserNotDeleted(@Param("userProfileId") Integer userProfileId, Pageable pageable);

    // Buscar por categoría con paginación
    @Query("SELECT p FROM Publication p JOIN p.categories c WHERE c.id = :categoryId AND p.isDeleted = false")
    Page<Publication> findByCategoryNotDeleted(@Param("categoryId") Integer categoryId, Pageable pageable);

    // Buscar por ID
    @Query("SELECT p FROM Publication p WHERE p.id = :id AND p.isDeleted = false")
    Optional<Publication> findByIdNotDeleted(@Param("id") Integer id);

    // Buscar perfil de usuario por ID
    @Query("SELECT u FROM UserProfile u WHERE u.id = :id AND u.isDeleted = false")
    Optional<UserProfile> findUserByIdNotDeleted(@Param("id") Integer id);
}