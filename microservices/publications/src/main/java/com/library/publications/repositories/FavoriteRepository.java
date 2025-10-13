package com.library.publications.repositories;

import com.library.entities.Favorite;
import com.library.entities.UserProfile;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FavoriteRepository extends JpaRepository<Favorite, Integer> {
    // Buscar todos los favoritos no eliminados con paginación
    @Query("SELECT f FROM Favorite f WHERE f.isDeleted = false")
    Page<Favorite> findAllNotDeleted(Pageable pageable);

    // Buscar por perfil de usuario con paginación
    @Query("SELECT f FROM Favorite f WHERE f.userProfile.id = :userProfileId AND f.isDeleted = false")
    Page<Favorite> findByUserNotDeleted(@Param("userProfileId") Integer userProfileId, Pageable pageable);

    // Buscar por publicación con paginación
    @Query("SELECT f FROM Favorite f WHERE f.publication.id = :publicationId AND f.isDeleted = false")
    Page<Favorite> findByPublicationNotDeleted(@Param("publicationId") Integer publicationId, Pageable pageable);

    // Buscar por ID
    @Query("SELECT f FROM Favorite f WHERE f.id = :id AND f.isDeleted = false")
    Optional<Favorite> findByIdNotDeleted(@Param("id") Integer id);

    // Buscar por usuario y publicación
    @Query("SELECT f FROM Favorite f WHERE f.userProfile.id = :userProfileId AND f.publication.id = :publicationId AND f.isDeleted = false")
    Optional<Favorite> findByUserAndPublicationNotDeleted(@Param("userProfileId") Integer userProfileId, @Param("publicationId") Integer publicationId);

    // Buscar perfil de usuario por ID
    @Query("SELECT u FROM UserProfile u WHERE u.id = :id AND u.isDeleted = false")
    Optional<UserProfile> findUserByIdNotDeleted(@Param("id") Integer id);
}