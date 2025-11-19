package com.library.users.services;

import com.library.dtos.PaginatedProfileAnnouncementResponseDTO;
import com.library.dtos.ProfileAnnouncementRequestDTO;
import com.library.dtos.ProfileAnnouncementResponseDTO;
import com.library.dtos.UpdateProfileAnnouncementRequestDTO;
import com.library.entities.ProfileAnnouncement;
import com.library.entities.UserProfile;
import com.library.entities.Announcement;
import com.library.users.config.PaginationConfig;
import com.library.users.exception.DuplicateResourceException;
import com.library.users.repository.ProfileAnnouncementRepository;
import com.library.users.repository.UserProfileRepository;
import com.library.users.repository.AnnouncementRepository;
import com.library.users.mappers.ProfileAnnouncementMapper;
import com.library.users.exception.BadRequestException;
import com.library.users.exception.ResourceNotFoundException;
import com.library.users.utils.NormalizeParameter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.CacheEvict;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ProfileAnnouncementService {
    private final ProfileAnnouncementRepository repository;
    private final UserProfileRepository userProfileRepository;
    private final AnnouncementRepository announcementRepository;
    private final ProfileAnnouncementMapper mapper;
    private final PaginationConfig paginationConfig;

    @Transactional
    @CachePut(value = "profileAnnouncements", key = "#result.id")
    public ProfileAnnouncementResponseDTO createProfileAnnouncement(ProfileAnnouncementRequestDTO dto) {
        // Validar IDs requeridos y positivos
        if (dto.getProfileId() == null || dto.getProfileId() <= 0) {
            log.error("Error al crear relación perfil-anuncio: ID de perfil inválido: {}", dto.getProfileId());
            throw new BadRequestException("El ID del perfil debe ser un número positivo");
        }
        if (dto.getAnnouncementId() == null || dto.getAnnouncementId() <= 0) {
            log.error("Error al crear relación perfil-anuncio: ID de anuncio inválido: {}", dto.getAnnouncementId());
            throw new BadRequestException("El ID del anuncio debe ser un número positivo");
        }

        // Validar fecha obligatoria y tampoco puede ser antes de la fecha actual
        if (dto.getProgrammatedDate() == null && dto.getProgrammatedDate().toString().isEmpty()) {
            log.error("Error al crear relación perfil-anuncio: La fecha programada no puede ser nula");
            throw new BadRequestException("La fecha programada es obligatoria");
        } else if (dto.getProgrammatedDate().isBefore(LocalDateTime.now())) {
            log.error("Error al crear relación perfil-anuncio: La fecha programada no puede ser en el pasado: {}", dto.getProgrammatedDate());
            throw new BadRequestException("La fecha programada no puede ser en el pasado");
        }

        // Validar existencia de perfil y anuncio (no eliminados)
        UserProfile userProfile = userProfileRepository.findByIdAndIsDeletedFalse(dto.getProfileId())
                .orElseThrow(() -> new ResourceNotFoundException("UserProfile no encontrado con id " + dto.getProfileId()));
        Announcement announcement = announcementRepository.findByIdAndIsDeletedFalse(dto.getAnnouncementId())
                .orElseThrow(() -> new ResourceNotFoundException("Announcement no encontrado con id " + dto.getAnnouncementId()));

        // Validar que no exista ya una relación igual (no eliminada)
        Optional<ProfileAnnouncement> duplicate = repository.findByUserProfileIdAndAnnouncementIdAndIsDeletedFalse(
                dto.getProfileId(), dto.getAnnouncementId()
        );
        if (duplicate.isPresent()) {
            log.error("Error al crear relación perfil-anuncio: Ya existe una relación igual (profileId={}, announcementId={})", dto.getProfileId(), dto.getAnnouncementId());
            throw new DuplicateResourceException("Ya existe una relación perfil-anuncio para ese perfil y anuncio");
        }

        // Crear y guardar la relación
        ProfileAnnouncement entity = new ProfileAnnouncement();

        entity.setUserProfile(userProfile);
        entity.setAnnouncement(announcement);
        entity.setProgrammedDate(dto.getProgrammatedDate());
        entity.setIsRead(false);
        entity.setIsDeleted(false);
        entity.setCreatedAt(LocalDateTime.now());
        entity.setUpdatedAt(LocalDateTime.now());
        ProfileAnnouncement saved = repository.save(entity);

        return mapper.toResponseDTO(saved);
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "profileAnnouncements", key = "#id")
    public ProfileAnnouncementResponseDTO getProfileAnnouncementById(Integer id) {
        if (id == null || id <= 0) {
            log.error("Error al obtener relación perfil-anuncio: ID inválido: {}", id);
            throw new BadRequestException("El ID de la relación perfil-anuncio debe ser un número positivo");
        }
        ProfileAnnouncement entity = repository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("ProfileAnnouncement no encontrado con id " + id));
        return mapper.toResponseDTO(entity);
    }

    @Transactional
    @CachePut(value = "profileAnnouncements", key = "#id")
    public ProfileAnnouncementResponseDTO updateProfileAnnouncement(Integer id, UpdateProfileAnnouncementRequestDTO dto) {
        if (id == null || id <= 0) {
            log.error("Error al actualizar relación perfil-anuncio: ID inválido: {}", id);
            throw new BadRequestException("El ID de la relación perfil-anuncio debe ser un número positivo");
        }
        ProfileAnnouncement entity = repository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("ProfileAnnouncement no encontrado con id " + id));

        // Validar IDs requeridos y positivos
        if (dto.getProfileId() == null || dto.getProfileId() <= 0) {
            log.error("Error al actualizar relación perfil-anuncio: ID de perfil inválido: {}", dto.getProfileId());
            throw new BadRequestException("El ID del perfil debe ser un número positivo");
        }
        if (dto.getAnnouncementId() == null || dto.getAnnouncementId() <= 0) {
            log.error("Error al actualizar relación perfil-anuncio: ID de anuncio inválido: {}", dto.getAnnouncementId());
            throw new BadRequestException("El ID del anuncio debe ser un número positivo");
        }

        if (dto.getProgrammatedDate() == null || dto.getProgrammatedDate().toString().isEmpty()) {
            log.error("Error al actualizar relación perfil-anuncio: La fecha programada no puede ser nula");
            throw new BadRequestException("La fecha programada es obligatoria");
        } else if (dto.getProgrammatedDate().isBefore(LocalDateTime.now())) {
            log.error("Error al actualizar relación perfil-anuncio: La fecha programada no puede ser en el pasado: {}", dto.getProgrammatedDate());
            throw new BadRequestException("La fecha programada no puede ser en el pasado");
        }

        // Validar existencia de perfil y anuncio
        UserProfile userProfile = userProfileRepository.findByIdAndIsDeletedFalse(dto.getProfileId())
                .orElseThrow(() -> new ResourceNotFoundException("UserProfile no encontrado con id " + dto.getProfileId()));
        Announcement announcement = announcementRepository.findByIdAndIsDeletedFalse(dto.getAnnouncementId())
                .orElseThrow(() -> new ResourceNotFoundException("Announcement no encontrado con id " + dto.getAnnouncementId()));

        // Validar duplicados si cambian los IDs
        if (!entity.getUserProfile().getId().equals(dto.getProfileId()) ||
            !entity.getAnnouncement().getId().equals(dto.getAnnouncementId())) {
            Optional<ProfileAnnouncement> duplicate = repository.findByUserProfileIdAndAnnouncementIdAndIsDeletedFalse(
                    dto.getProfileId(), dto.getAnnouncementId()
            );
            if (duplicate.isPresent()) {
                log.error("Error al actualizar relación perfil-anuncio: Ya existe una relación igual (profileId={}, announcementId={})", dto.getProfileId(), dto.getAnnouncementId());
                throw new BadRequestException("Ya existe una relación perfil-anuncio para ese perfil y anuncio");
            }
        }

        // Actualizar campos permitidos
        entity.setUserProfile(userProfile);
        entity.setAnnouncement(announcement);
        entity.setProgrammedDate(dto.getProgrammatedDate());
        entity.setIsRead(dto.getIsRead());
        entity.setUpdatedAt(LocalDateTime.now());
        ProfileAnnouncement updated = repository.save(entity);

        return mapper.toResponseDTO(updated);
    }

    @Transactional(readOnly = true)
    public PaginatedProfileAnnouncementResponseDTO getProfileAnnouncementsByFilters(
            Integer profileId, Integer announcementId, OffsetDateTime min, OffsetDateTime max, String isRead, Integer page) {

        if (page == null || page < 0) {
            log.error("Número de página inválido: {}", page);
            throw new BadRequestException("El número de página debe ser 0 o mayor");
        }

        Integer normalizedProfileId = NormalizeParameter.normalizeInteger(profileId);
        Integer normalizedAnnouncementId = NormalizeParameter.normalizeInteger(announcementId);
        LocalDateTime normalizedMin = NormalizeParameter.normalizeDateTime(min);
        LocalDateTime normalizedMax = NormalizeParameter.normalizeDateTime(max);
        Boolean normalizedIsRead = NormalizeParameter.normalizeBoolean(isRead);

        // Validar existencia de UserProfile si se proporciona profileId
        if (normalizedProfileId != null && normalizedProfileId < 0) {
            log.error("UserProfile no encontrado para el filtro profileId={}", normalizedProfileId);
            throw new BadRequestException("El ID del perfil debe ser un número positivo");
        } else if (normalizedProfileId != null && !userProfileRepository.existsByIdAndIsDeletedFalse(normalizedProfileId)) {
            log.error("Filtro profileId inválido: {}", normalizedProfileId);
            throw new ResourceNotFoundException("UserProfile no encontrado con id " + normalizedProfileId);
        }

        // Validar existencia de Announcement si se proporciona announcementId
        if (normalizedAnnouncementId != null && normalizedAnnouncementId < 0) {
            log.error("Announcement no encontrado para el filtro announcementId={}", normalizedAnnouncementId);
            throw new BadRequestException("El ID del anuncio debe ser un número positivo");
        } else if (normalizedAnnouncementId != null && !announcementRepository.existsByIdAndIsDeletedFalse(normalizedAnnouncementId)){
            log.error("Filtro announcementId inválido: {}", normalizedAnnouncementId);
            throw new ResourceNotFoundException("Announcement no encontrado con id " + normalizedAnnouncementId);
        }

        // Validar rango de fechas
        if (normalizedMax != null && normalizedMin != null && normalizedMax.isBefore(normalizedMin)) {
            log.error("Rango de fechas inválido: min={} es posterior a max={}", normalizedMin, normalizedMax);
            throw new BadRequestException("El valor 'max' no puede ser anterior a 'min'");
        }

        Pageable pageable = PageRequest.of(page, paginationConfig.getPageSize());

        log.info("DEBUG: profileId={}, announcementId={}, min={}, max={}, isRead={}, pageable={}",
            normalizedProfileId, normalizedAnnouncementId, normalizedMin, normalizedMax, normalizedIsRead, pageable);

        Page<ProfileAnnouncement> paPage;
        if (normalizedMin == null && normalizedMax == null) {
            paPage = repository.findByFilters(
                normalizedProfileId, normalizedAnnouncementId, normalizedIsRead, pageable
            );
        } else if (normalizedMin == null) {
            paPage = repository.findByFiltersWithMax(
                normalizedProfileId, normalizedAnnouncementId, normalizedMax, normalizedIsRead, pageable
            );
        } else if (normalizedMax == null) {
            paPage = repository.findByFiltersWithMin(
                normalizedProfileId, normalizedAnnouncementId, normalizedMin, normalizedIsRead, pageable
            );
        } else {
            paPage = repository.findByFilters(
                normalizedProfileId, normalizedAnnouncementId, normalizedMin, normalizedMax, normalizedIsRead, pageable
            );
        }

        List<ProfileAnnouncementResponseDTO> dtos = paPage.getContent().stream()
                .map(mapper::toResponseDTO)
                .collect(java.util.stream.Collectors.toList());

        PaginatedProfileAnnouncementResponseDTO response = new PaginatedProfileAnnouncementResponseDTO();
        response.setData(dtos);
        response.setPageSize(paPage.getSize());
        response.setTotalItems((int) paPage.getTotalElements());
        response.setCurrentPage(page);
        response.setTotalPages(paPage.getTotalPages());
        response.setNext(paPage.hasNext());
        response.setPrev(paPage.hasPrevious());

        return response;
    }

    @Transactional
    @CacheEvict(value = "profileAnnouncements", key = "#id")
    public void deleteProfileAnnouncement(Integer id) {
        if (id == null || id <= 0) {
            log.error("Error al eliminar relación perfil-anuncio: ID inválido: {}", id);
            throw new BadRequestException("El ID de la relación perfil-anuncio debe ser un número positivo");
        }
        ProfileAnnouncement entity = repository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("ProfileAnnouncement no encontrado con id " + id));
        entity.setIsDeleted(true);
        entity.setUpdatedAt(LocalDateTime.now());
        repository.save(entity);
    }
}