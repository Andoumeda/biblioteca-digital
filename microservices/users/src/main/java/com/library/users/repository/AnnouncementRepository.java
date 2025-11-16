package com.library.users.repository;

import com.library.entities.Announcement;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface AnnouncementRepository extends JpaRepository<Announcement, Integer> {

    Optional<Announcement> findByIdAndIsDeletedFalse(Integer id);

    boolean existsByIdAndIsDeletedFalse(Integer id);

    /**
     * Filtro flexible por tipo y público objetivo (ambos opcionales)
     */
    @Query("""
        SELECT a FROM Announcement a
        WHERE a.isDeleted = false
        AND (:type IS NULL OR a.type = :type)
        AND (:targetAudience IS NULL OR a.targetAudience = :targetAudience)
    """)
    Page<Announcement> findByFilters(
        @Param("type") Announcement.AnnouncementType type,
        @Param("targetAudience") Announcement.TargetAudience targetAudience,
        Pageable pageable
    );

}
