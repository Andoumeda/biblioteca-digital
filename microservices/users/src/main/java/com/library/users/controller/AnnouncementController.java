package com.library.users.controller;

import com.library.users.api.AnnouncementsApi;
import com.library.dtos.AnnouncementRequestDTO;
import com.library.dtos.AnnouncementResponseDTO;
import com.library.dtos.PaginatedAnnouncementResponseDTO;
import com.library.users.services.AnnouncementService;
import com.library.users.mappers.AnnouncementMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class AnnouncementController implements AnnouncementsApi {

    private final AnnouncementService announcementService;
    private final AnnouncementMapper announcementMapper;

    @Override
    public ResponseEntity<AnnouncementResponseDTO> getAnnouncementById(Integer id) {
        log.info("Recibido request GET /announcements/{}", id);
        AnnouncementResponseDTO response = announcementService.getAnnouncementById(id);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<AnnouncementResponseDTO> createAnnouncement(AnnouncementRequestDTO announcement) {
        log.info("Recibido request POST /announcements: {}", announcement);
        AnnouncementResponseDTO response = announcementService.createAnnouncement(announcement);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Override
    public ResponseEntity<AnnouncementResponseDTO> updateAnnouncement(Integer id, AnnouncementRequestDTO announcement) {
        log.info("Recibido request PUT /announcements/{}: {}", id, announcement);
        AnnouncementResponseDTO response = announcementService.updateAnnouncement(id, announcement);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<Void> deleteAnnouncement(Integer id) {
        log.info("Recibido request DELETE /announcements/{}", id);
        announcementService.deleteAnnouncement(id);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<PaginatedAnnouncementResponseDTO> getAnnouncementsByFilters(String targetAudience, String type,Integer page) {
        log.info("Recibido request GET /announcements/type/{}/page/{}", type, page);
        PaginatedAnnouncementResponseDTO response = announcementService.getAnnouncementsByFilters(targetAudience, type, page);
        return ResponseEntity.ok(response);
    }
}
