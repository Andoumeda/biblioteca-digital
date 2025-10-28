package com.library.users.services;

import com.library.entities.Announcement;
import com.library.users.exception.BadRequestException;
import com.library.users.exception.ResourceNotFoundException;
import com.library.users.repository.AnnouncementRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Transactional
public class AnnouncementServiceImpl implements AnnouncementService {

    private final AnnouncementRepository announcementRepository;

    public AnnouncementServiceImpl(AnnouncementRepository announcementRepository) {
        this.announcementRepository = announcementRepository;
    }

    @Override
    public Page<Announcement> getAllAnnouncements(int page, int size) {
        return announcementRepository.findByIsDeletedFalse(PageRequest.of(page, size));
    }

    @Override
    public Announcement getAnnouncementById(Integer id) {
        return announcementRepository.findById(id)
                .filter(a -> !Boolean.TRUE.equals(a.getIsDeleted()))
                .orElseThrow(() -> new ResourceNotFoundException("Announcement not found with id " + id));
    }


    @Override
    public Announcement createAnnouncement(Announcement announcement) {
        announcement.setCreatedAt(LocalDateTime.now());
        announcement.setUpdatedAt(LocalDateTime.now());
        announcement.setIsDeleted(false);
        return announcementRepository.save(announcement);
    }

    @Override
    public Announcement updateAnnouncement(Integer id, Announcement announcement) {
        Announcement existing = getAnnouncementById(id);
        existing.setTitle(announcement.getTitle());
        existing.setMessage(announcement.getMessage());
        existing.setType(announcement.getType());
        existing.setTargetAudience(announcement.getTargetAudience());
        existing.setUpdatedAt(LocalDateTime.now());
        return announcementRepository.save(existing);
    }

    @Override
    public void deleteAnnouncement(Integer id) {
        Announcement existing = getAnnouncementById(id);
        existing.setIsDeleted(true);
        announcementRepository.save(existing);
    }

    @Override
    public Page<Announcement> getAnnouncementsByType(String type, int page, int size) {
        try {
            return announcementRepository.findByTypeAndIsDeletedFalse(
                    Announcement.AnnouncementType.valueOf(type.toUpperCase()),
                    PageRequest.of(page, size)
            );
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Invalid announcement type: " + type);
        }
    }

    @Override
    public Page<Announcement> getAnnouncementsByAudience(String targetAudience, int page, int size) {
        try {
            return announcementRepository.findByTargetAudienceAndIsDeletedFalse(
                    Announcement.TargetAudience.valueOf(targetAudience.toUpperCase()),
                    PageRequest.of(page, size)
            );
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Invalid target audience: " + targetAudience);
        }
    }
}