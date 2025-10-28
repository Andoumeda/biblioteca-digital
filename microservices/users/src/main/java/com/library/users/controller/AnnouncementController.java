package com.library.users.controller;

import com.library.entities.Announcement;
import com.library.users.services.AnnouncementService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/announcements")
public class AnnouncementController {

    private static final Logger log = LoggerFactory.getLogger(AnnouncementController.class);

    private final AnnouncementService announcementService;

    public AnnouncementController(AnnouncementService announcementService) {
        this.announcementService = announcementService;
    }

    @GetMapping("/page/{page}/size/{size}")
    public ResponseEntity<Page<Announcement>> getAllAnnouncements(
            @PathVariable int page,
            @PathVariable int size) {
        log.info("Recibido request GET /announcements/page/{}/size/{}", page, size);
        Page<Announcement> result = announcementService.getAllAnnouncements(page, size);
        log.info("Se obtuvieron {} anuncios", result.getTotalElements());
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Announcement> getAnnouncementById(@PathVariable Integer id) {
        log.info("Recibido request GET /announcements/{}", id);
        Announcement result = announcementService.getAnnouncementById(id);
        log.info("Anuncio obtenido exitosamente para ID {}", id);
        return ResponseEntity.ok(result);
    }

    @PostMapping
    public ResponseEntity<Announcement> createAnnouncement(@RequestBody Announcement announcement) {
        log.info("Recibido request POST /announcements: {}", announcement);
        Announcement result = announcementService.createAnnouncement(announcement);
        log.info("Anuncio creado con ID {}", result.getId());
        return ResponseEntity.ok(result);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Announcement> updateAnnouncement(
            @PathVariable Integer id,
            @RequestBody Announcement announcement) {
        log.info("Recibido request PUT /announcements/{}: {}", id, announcement);
        Announcement result = announcementService.updateAnnouncement(id, announcement);
        log.info("Anuncio actualizado con ID {}", id);
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAnnouncement(@PathVariable Integer id) {
        log.info("Recibido request DELETE /announcements/{}", id);
        announcementService.deleteAnnouncement(id);
        log.info("Anuncio eliminado con ID {}", id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/type/{type}/page/{page}/size/{size}")
    public ResponseEntity<Page<Announcement>> getAnnouncementsByType(
            @PathVariable String type,
            @PathVariable int page,
            @PathVariable int size) {
        log.info("Recibido request GET /announcements/type/{}/page/{}/size/{}", type, page, size);
        Page<Announcement> result = announcementService.getAnnouncementsByType(type, page, size);
        log.info("Se obtuvieron {} anuncios del tipo {}", result.getTotalElements(), type);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/audience/{targetAudience}/page/{page}/size/{size}")
    public ResponseEntity<Page<Announcement>> getAnnouncementsByAudience(
            @PathVariable String targetAudience,
            @PathVariable int page,
            @PathVariable int size) {
        log.info("Recibido request GET /announcements/audience/{}/page/{}/size/{}", targetAudience, page, size);
        Page<Announcement> result = announcementService.getAnnouncementsByAudience(targetAudience, page, size);
        log.info("Se obtuvieron {} anuncios para audiencia {}", result.getTotalElements(), targetAudience);
        return ResponseEntity.ok(result);
    }
}