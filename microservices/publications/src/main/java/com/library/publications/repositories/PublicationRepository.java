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
    Page<Publication> findAllByIsDeletedFalse(Pageable pageable);

    // Buscar por título con paginación (case insensitive)
    Page<Publication> findByTitleContainingIgnoreCaseAndIsDeletedFalse(String text, Pageable pageable);

    // Buscar por descripción con paginación (case insensitive)
    Page<Publication> findByDescriptionContainingIgnoreCaseAndIsDeletedFalse(String text, Pageable pageable);

    // Buscar por estado con paginación
    Page<Publication> findByStateAndIsDeletedFalse(PublicationState state, Pageable pageable);

    // Buscar por perfil de usuario con paginación
    Page<Publication> findByUserProfileIdAndIsDeletedFalse(Integer userProfileId, Pageable pageable);

    // Buscar por ID
    Optional<Publication> findByIdAndIsDeletedFalse(Integer id);

    boolean existsByIdAndIsDeletedFalse(Integer id);

    // (Consulta personalizada) Buscar perfil de usuario por ID
    @Query("SELECT u FROM UserProfile u WHERE u.id = :id AND u.isDeleted = false")
    Optional<UserProfile> findUserByIdNotDeleted(@Param("id") Integer id);
}