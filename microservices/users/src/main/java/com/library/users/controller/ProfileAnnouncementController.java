package com.library.users.controller;

import com.library.dtos.UpdateProfileAnnouncementRequestDTO;
import com.library.users.api.ProfileAnnouncementsApi;
import com.library.users.services.ProfileAnnouncementService;
import com.library.dtos.ProfileAnnouncementRequestDTO;
import com.library.dtos.ProfileAnnouncementResponseDTO;
import com.library.dtos.PaginatedProfileAnnouncementResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

import java.time.OffsetDateTime;

@Controller
@RequiredArgsConstructor
@Slf4j
public class ProfileAnnouncementController implements ProfileAnnouncementsApi {
    private final ProfileAnnouncementService profileAnnouncementService;

    @Override
    public ResponseEntity<ProfileAnnouncementResponseDTO> createProfileAnnouncement(ProfileAnnouncementRequestDTO dto) {
        log.info("Creando relación perfil-anuncio: profileId={}, announcementId={}", dto.getProfileId(), dto.getAnnouncementId());
        return ResponseEntity.status(201).body(profileAnnouncementService.createProfileAnnouncement(dto));
    }

    @Override
    public ResponseEntity<Void> deleteProfileAnnouncement(Integer id) {
        log.info("Eliminando relación perfil-anuncio id={}", id);
        profileAnnouncementService.deleteProfileAnnouncement(id);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<ProfileAnnouncementResponseDTO> getProfileAnnouncementById(Integer id) {
        log.info("Obteniendo relación perfil-anuncio id={}", id);
        return ResponseEntity.ok(profileAnnouncementService.getProfileAnnouncementById(id));
    }

    @Override
    public ResponseEntity<PaginatedProfileAnnouncementResponseDTO> getProfileAnnouncementsByFilters(
            Integer profileId, Integer announcementId, OffsetDateTime min, OffsetDateTime max, String isRead, Integer page) {
        log.info("Buscando relaciones perfil-anuncio con filtros: profileId={}, announcementId={}, min={}, max={}, isRead={}, page={}",
                profileId, announcementId, min, max, isRead, page);
        return ResponseEntity.ok(
            profileAnnouncementService.getProfileAnnouncementsByFilters(profileId, announcementId, min, max, isRead, page)
        );
    }

    @Override
    public ResponseEntity<ProfileAnnouncementResponseDTO> updateProfileAnnouncement(Integer id, UpdateProfileAnnouncementRequestDTO dto) {
        log.info("Actualizando relación perfil-anuncio id={}", id);
        return ResponseEntity.ok(profileAnnouncementService.updateProfileAnnouncement(id, dto));
    }
}
