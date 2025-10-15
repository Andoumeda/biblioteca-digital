package com.library.users.repository;

import com.library.entities.Announcement;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AnnouncementRepository extends JpaRepository<Announcement, Integer> {

    Page<Announcement> findByIsDeletedFalse(Pageable pageable);

    Page<Announcement> findByTypeAndIsDeletedFalse(Announcement.AnnouncementType type, Pageable pageable);

    Page<Announcement> findByTargetAudienceAndIsDeletedFalse(Announcement.TargetAudience targetAudience, Pageable pageable);
}
