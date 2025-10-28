package com.library.books.repositories;

import com.library.entities.Rating;
import com.library.entities.UserProfile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RatingRepository extends JpaRepository<Rating, Integer> {

    /**
     * Obtener un UserProfile por su ID, solo si no está eliminado
     */
    @Query("SELECT u FROM UserProfile u WHERE u.id = :userProfileId AND u.isDeleted = false")
    Optional<UserProfile> findUserProfileByIdAndIsDeletedFalse(@Param("userProfileId") Integer userProfileId);

    /**
     * Verificar si existe un UserProfile por su ID y no está eliminado
     */
    @Query("SELECT CASE WHEN COUNT(u) > 0 THEN true ELSE false END FROM UserProfile u WHERE u.id = :userProfileId AND u.isDeleted = false")
    boolean existsUserProfileByIdAndIsDeletedFalse(@Param("userProfileId") Integer userProfileId);

    /**
     * Buscar rating por ID que no esté eliminado
     */
    Optional<Rating> findByIdAndIsDeletedFalse(Integer id);

    /**
     * Verificar si un usuario ya calificó un libro
     */
    boolean existsByUserProfileIdAndBookIdAndIsDeletedFalse(Integer userProfileId, Integer bookId);

    /**
     * Buscar valoraciones por múltiples filtros (libro, usuario, rango de valoración)
     */
    @Query("SELECT r FROM Rating r WHERE " +
            "(:bookId IS NULL OR r.book.id = :bookId) AND " +
            "(:userProfileId IS NULL OR r.userProfile.id = :userProfileId) AND " +
            "(:minValoration IS NULL OR r.valoration >= :minValoration) AND " +
            "(:maxValoration IS NULL OR r.valoration <= :maxValoration) AND " +
            "r.isDeleted = false")
    Page<Rating> findByFilters(@Param("bookId") Integer bookId,
                                @Param("userProfileId") Integer userProfileId,
                                @Param("minValoration") Integer minValoration,
                                @Param("maxValoration") Integer maxValoration,
                                Pageable pageable);
}