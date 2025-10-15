package com.library.users.services;

import com.library.entities.Announcement;
import org.springframework.data.domain.Page;

public interface AnnouncementService {

    Page<Announcement> getAllAnnouncements(int page, int size);

    Announcement getAnnouncementById(Integer id);

    Announcement createAnnouncement(Announcement announcement);

    Announcement updateAnnouncement(Integer id, Announcement announcement);

    void deleteAnnouncement(Integer id);

    Page<Announcement> getAnnouncementsByType(String type, int page, int size);

    Page<Announcement> getAnnouncementsByAudience(String targetAudience, int page, int size);
}
