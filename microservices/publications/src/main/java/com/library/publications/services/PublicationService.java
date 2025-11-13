package com.library.publications.services;

import com.library.dtos.PublicationRequestDTO;
import com.library.dtos.PublicationResponseDTO;
import com.library.dtos.PaginatedResponseDTO;

import com.library.entities.Publication;
import com.library.entities.Publication.PublicationState;
import com.library.entities.UserProfile;

import com.library.publications.repositories.PublicationRepository;

import com.library.publications.mappers.PublicationMapper;

import com.library.publications.utils.PaginationUtil;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PublicationService {
    private final PublicationRepository publicationRepository;
    private final PublicationMapper publicationMapper;
    private final Logger logger = LoggerFactory.getLogger(PublicationService.class);

    @Transactional
    public PublicationResponseDTO create(PublicationRequestDTO dto) {
        try {
            // Verificar si existe el perfil de usuario
            logger.debug("Consulta a la BD: SELECT u FROM UserProfile u WHERE u.id = :id AND u.isDeleted = false");
            UserProfile userProfile = publicationRepository.findUserByIdNotDeleted(dto.getUserProfileId())
                    .orElseThrow(() -> {
                        logger.error("No se encontró el perfil de usuario con ID {}", dto.getUserProfileId());
                        return new ResponseStatusException(HttpStatus.NOT_FOUND, "Perfil de usuario no encontrado con ID: " + dto.getUserProfileId());
                    });

            Publication publication = new Publication();

            publication.setUserProfile(userProfile);
            publication.setTitle(dto.getTitle());
            publication.setDescription(dto.getDescription());
            publication.setState(PublicationState.PENDING);

            Publication saved = publicationRepository.save(publication);
            return publicationMapper.toResponseDTO(saved);
        } catch (Exception e) {
            logger.error("Error al crear la publicación: {}", e.getMessage(), e);
            throw new RuntimeException("Error al crear la publicación: " + e.getMessage(), e);
        }
    }

    @Transactional(readOnly = true)
    public PaginatedResponseDTO getPaginated(Integer page, Integer size) {
        logger.debug("Consulta a la BD: SELECT p FROM Publication p WHERE p.isDeleted = false");
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Publication> publicationsPage = publicationRepository.findAllByIsDeletedFalse(pageable);

        if (publicationsPage.isEmpty()) {
            logger.warn("No se encontraron publicaciones");
            throw new ResponseStatusException(HttpStatus.NO_CONTENT, "No se encontraron publicaciones");
        } else
            logger.info("Se encontraron {} publicaciones", publicationsPage.getTotalElements());

        return PaginationUtil.buildPaginatedResponse(publicationsPage, publicationMapper::toResponseDTO);
    }

    @Transactional(readOnly = true)
    public PaginatedResponseDTO getPaginatedByTitle(String title, Integer page, Integer size) {
        logger.debug("Consulta a la BD: SELECT p FROM Publication p WHERE LOWER(p.title) LIKE LOWER(CONCAT('%', :text, '%')) AND p.isDeleted = false");
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Publication> publicationsPage = publicationRepository.findByTitleContainingIgnoreCaseAndIsDeletedFalse(title, pageable);

        if (publicationsPage.isEmpty()) {
            logger.warn("No se encontraron publicaciones con el título {}", title);
            throw new ResponseStatusException(HttpStatus.NO_CONTENT, "No se encontraron publicaciones con el título " + title);
        } else
            logger.info("Se encontraron {} publicaciones con el título {}", publicationsPage.getTotalElements(), title);

        return PaginationUtil.buildPaginatedResponse(publicationsPage, publicationMapper::toResponseDTO);
    }

    @Transactional(readOnly = true)
    public PaginatedResponseDTO getPaginatedByDescription(String desc, Integer page, Integer size) {
        logger.debug("Consulta a la BD: SELECT p FROM Publication p WHERE LOWER(p.description) LIKE LOWER(CONCAT('%', :text, '%')) AND p.isDeleted = false");
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Publication> publicationsPage = publicationRepository.findByDescriptionContainingIgnoreCaseAndIsDeletedFalse(desc, pageable);

        if (publicationsPage.isEmpty()) {
            logger.warn("No se encontraron publicaciones con la descripción {}", desc);
            throw new ResponseStatusException(HttpStatus.NO_CONTENT, "No se encontraron publicaciones con la descripción " + desc);
        } else
            logger.info("Se encontraron {} publicaciones con la descripción {}", publicationsPage.getTotalElements(), desc);

        return PaginationUtil.buildPaginatedResponse(publicationsPage, publicationMapper::toResponseDTO);
    }

    @Transactional(readOnly = true)
    public PaginatedResponseDTO getPaginatedByState(String state, Integer page, Integer size) {
        logger.debug("Consulta a la BD: SELECT p FROM Publication p WHERE p.state = :state AND p.isDeleted = false");
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        // Convertir a enum
        PublicationState publicationState;
        try {
            publicationState = PublicationState.valueOf(state.toUpperCase());
        } catch (IllegalArgumentException e) {
            logger.error("Estado de publicación inválido: {}", state);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Estado de publicación inválido: " + state);
        }

        Page<Publication> publicationsPage = publicationRepository.findByStateAndIsDeletedFalse(publicationState, pageable);

        if (publicationsPage.isEmpty()) {
            logger.warn("No se encontraron publicaciones con el estado {}", state);
            throw new ResponseStatusException(HttpStatus.NO_CONTENT, "No se encontraron publicaciones con el estado " + state);
        } else
            logger.info("Se encontraron {} publicaciones con el estado {}", publicationsPage.getTotalElements(), state);

        return PaginationUtil.buildPaginatedResponse(publicationsPage, publicationMapper::toResponseDTO);
    }

    @Transactional(readOnly = true)
    public PaginatedResponseDTO getPaginatedByUser(Integer user, Integer page, Integer size) {
        logger.debug("Consulta a la BD: SELECT p FROM Publication p WHERE p.userProfile.id = :userProfileId AND p.isDeleted = false");
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Publication> publicationsPage = publicationRepository.findByUserProfileIdAndIsDeletedFalse(user, pageable);

        if (publicationsPage.isEmpty()) {
            logger.warn("No se encontraron publicaciones con el ID de usuario {}", user);
            throw new ResponseStatusException(HttpStatus.NO_CONTENT, "No se encontraron publicaciones con el ID de usuario " + user);
        } else
            logger.info("Se encontraron {} publicaciones con el ID de usuario {}", publicationsPage.getTotalElements(), user);

        return PaginationUtil.buildPaginatedResponse(publicationsPage, publicationMapper::toResponseDTO);
    }

    @Transactional(readOnly = true)
    public PublicationResponseDTO getById(Integer id) {
        logger.debug("Consulta a la BD: SELECT p FROM Publication p WHERE p.id = :id AND p.isDeleted = false");
        Publication publication = publicationRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> {
                    logger.error("No se encontró la publicación con ID {}", id);
                    return new ResponseStatusException(HttpStatus.NOT_FOUND, "Publicación no encontrada con ID: " + id);
                });

        return publicationMapper.toResponseDTO(publication);
    }

    @Transactional
    public PublicationResponseDTO update(Integer id, PublicationRequestDTO dto) {
        logger.debug("Consulta a la BD: SELECT p FROM Publication p WHERE p.id = :id AND p.isDeleted = false");
        Publication publication = publicationRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> {
                    logger.error("No se encontró la publicación con ID {}", id);
                    return new ResponseStatusException(HttpStatus.NOT_FOUND, "Publicación no encontrada con ID: " + id);
                });

        // Verificar si existe el perfil de usuario
        logger.debug("Consulta a la BD: SELECT u FROM UserProfile u WHERE u.id = :id AND u.isDeleted = false");
        UserProfile userProfile = publicationRepository.findUserByIdNotDeleted(dto.getUserProfileId())
                .orElseThrow(() -> {
                    logger.error("No se encontró el perfil de usuario con ID {}", dto.getUserProfileId());
                    return new ResponseStatusException(HttpStatus.NOT_FOUND, "Perfil de usuario no encontrado con ID: " + dto.getUserProfileId());
                });

        publication.setUserProfile(userProfile);
        publication.setTitle(dto.getTitle());
        publication.setDescription(dto.getDescription());

        publication.setUpdatedAt(LocalDateTime.now());

        Publication updated = publicationRepository.save(publication);
        return publicationMapper.toResponseDTO(updated);
    }

    @Transactional
    public void delete(Integer id) {
        logger.debug("Consulta a la BD: SELECT p FROM Publication p WHERE p.id = :id AND p.isDeleted = false");
        Publication publication = publicationRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> {
                    logger.error("No se encontró la publicación con ID {}", id);
                    return new ResponseStatusException(HttpStatus.NOT_FOUND, "Publicación no encontrada con ID: " + id);
                });

        // Soft delete (borrado lógico)
        publication.setIsDeleted(true);
        publication.setUpdatedAt(LocalDateTime.now());
        publicationRepository.save(publication);
    }

    @Transactional
    public PublicationResponseDTO approve(Integer id) {
        logger.debug("Consulta a la BD: SELECT p FROM Publication p WHERE p.id = :id AND p.isDeleted = false");
        Publication publication = publicationRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> {
                    logger.error("No se encontró la publicación con ID {}", id);
                    return new ResponseStatusException(HttpStatus.NOT_FOUND, "Publicación no encontrada con ID: " + id);
                });

        publication.setState(PublicationState.APPROVED);
        publication.setUpdatedAt(LocalDateTime.now());

        Publication updated = publicationRepository.save(publication);
        return publicationMapper.toResponseDTO(updated);
    }

    @Transactional
    public PublicationResponseDTO reject(Integer id) {
        logger.debug("Consulta a la BD: SELECT p FROM Publication p WHERE p.id = :id AND p.isDeleted = false");
        Publication publication = publicationRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> {
                    logger.error("No se encontró la publicación con ID {}", id);
                    return new ResponseStatusException(HttpStatus.NOT_FOUND, "Publicación no encontrada con ID: " + id);
                });

        publication.setState(PublicationState.REJECTED);
        publication.setUpdatedAt(LocalDateTime.now());

        Publication updated = publicationRepository.save(publication);
        return publicationMapper.toResponseDTO(updated);
    }
}