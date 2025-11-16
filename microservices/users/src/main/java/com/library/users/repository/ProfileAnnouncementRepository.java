package com.library.users.repository;

import com.library.entities.ProfileAnnouncement;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

@Repository
public interface ProfileAnnouncementRepository extends JpaRepository<ProfileAnnouncement, Integer> {
    Optional<ProfileAnnouncement> findByIdAndIsDeletedFalse(Integer id);

    Optional<ProfileAnnouncement> findByUserProfileIdAndAnnouncementIdAndIsDeletedFalse(Integer userProfileId, Integer announcementId);

    @Query("SELECT pa FROM ProfileAnnouncement pa " +
           "WHERE pa.isDeleted = false " +
           "AND (:profileId IS NULL OR pa.userProfile.id = :profileId) " +
           "AND (:announcementId IS NULL OR pa.announcement.id = :announcementId) " +
           "AND (:isRead IS NULL OR pa.isRead = :isRead) " +
           "AND (:min IS NULL OR pa.programmedDate >= :min) ")
    Page<ProfileAnnouncement> findByFiltersWithMin(
        @Param("profileId") Integer profileId,
        @Param("announcementId") Integer announcementId,
        @Param("min") LocalDateTime min,
        @Param("isRead") Boolean isRead,
        Pageable pageable
    );

    @Query("SELECT pa FROM ProfileAnnouncement pa " +
           "WHERE pa.isDeleted = false " +
           "AND (:profileId IS NULL OR pa.userProfile.id = :profileId) " +
           "AND (:announcementId IS NULL OR pa.announcement.id = :announcementId) " +
           "AND (:isRead IS NULL OR pa.isRead = :isRead) " +
           "AND (:max IS NULL OR pa.programmedDate <= :max)")
    Page<ProfileAnnouncement> findByFiltersWithMax(
        @Param("profileId") Integer profileId,
        @Param("announcementId") Integer announcementId,
        @Param("max") LocalDateTime max,
        @Param("isRead") Boolean isRead,
        Pageable pageable
    );

    @Query("SELECT pa FROM ProfileAnnouncement pa " +
           "WHERE pa.isDeleted = false " +
           "AND (:profileId IS NULL OR pa.userProfile.id = :profileId) " +
           "AND (:announcementId IS NULL OR pa.announcement.id = :announcementId) " +
           "AND (:isRead IS NULL OR pa.isRead = :isRead)")
    Page<ProfileAnnouncement> findByFilters(
        @Param("profileId") Integer profileId,
        @Param("announcementId") Integer announcementId,
        @Param("isRead") Boolean isRead,
        Pageable pageable
    );

    @Query("SELECT pa FROM ProfileAnnouncement pa " +
           "WHERE pa.isDeleted = false " +
           "AND (:profileId IS NULL OR pa.userProfile.id = :profileId) " +
           "AND (:announcementId IS NULL OR pa.announcement.id = :announcementId) " +
           "AND (:isRead IS NULL OR pa.isRead = :isRead) " +
           "AND (:min IS NULL OR pa.programmedDate >= :min) " +
           "AND (:max IS NULL OR pa.programmedDate <= :max)")
    Page<ProfileAnnouncement> findByFilters(
        @Param("profileId") Integer profileId,
        @Param("announcementId") Integer announcementId,
        @Param("min") LocalDateTime min,
        @Param("max") LocalDateTime max,
        @Param("isRead") Boolean isRead,
        Pageable pageable
    );
}
