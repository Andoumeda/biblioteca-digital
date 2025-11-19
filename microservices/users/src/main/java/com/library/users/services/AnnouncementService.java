package com.library.users.services;

import com.library.dtos.AnnouncementRequestDTO;
import com.library.dtos.AnnouncementResponseDTO;
import com.library.dtos.PaginatedAnnouncementResponseDTO;
import com.library.entities.Announcement;
import com.library.users.config.PaginationConfig;
import com.library.users.exception.BadRequestException;
import com.library.users.exception.ResourceNotFoundException;
import com.library.users.mappers.AnnouncementMapper;
import com.library.users.repository.AnnouncementRepository;
import com.library.users.utils.NormalizeParameter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.CacheEvict;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AnnouncementService {
    private final AnnouncementRepository announcementRepository;
    private final AnnouncementMapper announcementMapper;
    private final PaginationConfig paginationConfig;

    /**
     * Crear un nuevo anuncio
     */
    @Transactional
    @CachePut(value = "announcements", key = "#result.id")
    public AnnouncementResponseDTO createAnnouncement(AnnouncementRequestDTO dto) {
        if (dto.getTitle() == null || dto.getTitle().trim().isEmpty()) {
            log.error("Error al crear anuncio: El título es obligatorio");
            throw new BadRequestException("El título del anuncio es obligatorio");
        }
        if (dto.getMessage() == null || dto.getMessage().trim().isEmpty()) {
            log.error("Error al crear anuncio: El mensaje es obligatorio");
            throw new BadRequestException("El mensaje del anuncio es obligatorio");
        }
        if (dto.getType() == null) {
            log.error("Error al crear anuncio: El tipo es obligatorio");
            throw new BadRequestException("El tipo del anuncio es obligatorio");
        }
        if (dto.getTargetAudience() == null) {
            log.error("Error al crear anuncio: El público objetivo es obligatorio");
            throw new BadRequestException("El público objetivo del anuncio es obligatorio");
        }

        Announcement entity = new Announcement();
        entity.setTitle(dto.getTitle());
        entity.setMessage(dto.getMessage());
        entity.setType(Announcement.AnnouncementType.valueOf(dto.getType().getValue()));
        entity.setTargetAudience(Announcement.TargetAudience.valueOf(dto.getTargetAudience().getValue()));
        entity.setCreatedAt(LocalDateTime.now());
        entity.setUpdatedAt(LocalDateTime.now());
        entity.setIsDeleted(false);

        Announcement saved = announcementRepository.save(entity);
        log.info("Anuncio creado exitosamente con ID: {}", saved.getId());
        return announcementMapper.toResponseDTO(saved);
    }

    /**
     * Obtener anuncio por ID
     */
    @Transactional(readOnly = true)
    @Cacheable(value = "announcements", key = "#id")
    public AnnouncementResponseDTO getAnnouncementById(Integer id) {
        if (id == null || id <= 0) {
            log.error("Error al obtener anuncio: ID inválido: {}", id);
            throw new BadRequestException("El ID del anuncio debe ser un número positivo");
        }
        Announcement entity = announcementRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Announcement no encontrado con id " + id));
        return announcementMapper.toResponseDTO(entity);
    }

    /**
     * Actualizar anuncio
     */
    @Transactional
    @CachePut(value = "announcements", key = "#id")
    public AnnouncementResponseDTO updateAnnouncement(Integer id, AnnouncementRequestDTO dto) {
        if (id == null || id <= 0) {
            log.error("Error al actualizar anuncio: ID inválido: {}", id);
            throw new BadRequestException("El ID del anuncio debe ser un número positivo");
        }
        Announcement entity = announcementRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Announcement no encontrado con id " + id));

        if (dto.getTitle() == null || dto.getTitle().trim().isEmpty()) {
            log.error("Error al actualizar anuncio: El título es obligatorio");
            throw new BadRequestException("El título del anuncio es obligatorio");
        }
        if (dto.getMessage() == null || dto.getMessage().trim().isEmpty()) {
            log.error("Error al actualizar anuncio: El mensaje es obligatorio");
            throw new BadRequestException("El mensaje del anuncio es obligatorio");
        }
        if (dto.getType() == null) {
            log.error("Error al actualizar anuncio: El tipo es obligatorio");
            throw new BadRequestException("El tipo del anuncio es obligatorio");
        }
        if (dto.getTargetAudience() == null) {
            log.error("Error al actualizar anuncio: El público objetivo es obligatorio");
            throw new BadRequestException("El público objetivo del anuncio es obligatorio");
        }

        entity.setTitle(dto.getTitle());
        entity.setMessage(dto.getMessage());
        entity.setType(Announcement.AnnouncementType.valueOf(dto.getType().getValue()));
        entity.setTargetAudience(Announcement.TargetAudience.valueOf(dto.getTargetAudience().getValue()));
        entity.setUpdatedAt(LocalDateTime.now());

        Announcement updated = announcementRepository.save(entity);
        log.info("Anuncio actualizado exitosamente: {}", updated.getId());
        return announcementMapper.toResponseDTO(updated);
    }

    /**
     * Eliminar anuncio (soft delete)
     */
    @Transactional
    @CacheEvict(value = "announcements", key = "#id")
    public void deleteAnnouncement(Integer id) {
        if (id == null || id <= 0) {
            log.error("Error al eliminar anuncio: ID inválido: {}", id);
            throw new BadRequestException("El ID del anuncio debe ser un número positivo");
        }
        Announcement entity = announcementRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Announcement no encontrado con id " + id));
        entity.setIsDeleted(true);
        entity.setUpdatedAt(LocalDateTime.now());
        announcementRepository.save(entity);
        log.info("Anuncio eliminado exitosamente (soft delete): {}", id);
    }

    /**
     * Obtener anuncios con paginación y filtros
     */
    @Transactional(readOnly = true)
    public PaginatedAnnouncementResponseDTO getAnnouncementsByFilters(String targetAudience, String type, Integer page) {
        if (page == null || page < 0) {
            log.error("Error al buscar anuncios: Número de página inválido: {}", page);
            throw new BadRequestException("El número de página debe ser mayor o igual a 0");
        }

        String normalizedType = NormalizeParameter.normalizeString(type);
        String normalizedTargetAudience = NormalizeParameter.normalizeString(targetAudience);

        // Validar enums si se proveen
        if (normalizedType != null) {
            try {
                Announcement.AnnouncementType.valueOf(normalizedType);
            } catch (IllegalArgumentException e) {
                log.error("Error al buscar anuncios: Tipo inválido: {}", normalizedType);
                throw new BadRequestException("Tipo de anuncio inválido: " + normalizedType);
            }
        }

        if (normalizedTargetAudience != null) {
            try {
                Announcement.TargetAudience.valueOf(normalizedTargetAudience);
            } catch (IllegalArgumentException e) {
                log.error("Error al buscar anuncios: Público objetivo inválido: {}", normalizedTargetAudience);
                throw new BadRequestException("Público objetivo de anuncio inválido: " + normalizedTargetAudience);
            }
        }

        Pageable pageable = PageRequest.of(page, paginationConfig.getPageSize());
        Announcement.AnnouncementType typeEnum = normalizedType != null ? Announcement.AnnouncementType.valueOf(normalizedType) : null;
        Announcement.TargetAudience targetEnum = normalizedTargetAudience != null ? Announcement.TargetAudience.valueOf(normalizedTargetAudience) : null;
        Page<Announcement> pageResult = announcementRepository.findByFilters(typeEnum, targetEnum, pageable);
        List<AnnouncementResponseDTO> dtos = pageResult.getContent().stream()
                .map(announcementMapper::toResponseDTO)
                .collect(Collectors.toList());

        PaginatedAnnouncementResponseDTO response = new PaginatedAnnouncementResponseDTO();

        response.setData(dtos);
        response.setPageSize(pageResult.getSize());
        response.setTotalItems((int) pageResult.getTotalElements());
        response.setCurrentPage(pageResult.getNumber());
        response.setTotalPages(pageResult.getTotalPages());
        response.setNext(pageResult.hasNext());
        response.setPrev(pageResult.hasPrevious());

        return response;
    }
}