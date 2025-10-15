package com.library.publications.services;

import com.library.dtos.PublicationRequestDTO;
import com.library.dtos.PublicationResponseDTO;
import com.library.dtos.PaginatedResponseDTO;

import com.library.entities.Category;
import com.library.entities.Publication;
import com.library.entities.Publication.PublicationState;
import com.library.entities.UserProfile;

import com.library.publications.repositories.CategoryRepository;
import com.library.publications.repositories.PublicationRepository;

import com.library.publications.utils.PaginationUtil;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PublicationService {
    private final PublicationRepository publicationRepository;
    private final CategoryRepository categoryRepository;
    private final ModelMapper modelMapper;
    private final Logger logger = LoggerFactory.getLogger(PublicationService.class);

    @Transactional
    public PublicationResponseDTO create(PublicationRequestDTO dto) {
        try {
            // Verificar si existe el perfil de usuario
            UserProfile userProfile = publicationRepository.findUserByIdNotDeleted(dto.getUserProfileId())
                    .orElseThrow(() -> {
                        logger.error("No se encontró el perfil de usuario con ID " + dto.getUserProfileId());
                        return new IllegalArgumentException(
                            String.format("Perfil de usuario no encontrado con ID: %s", dto.getUserProfileId())
                        );
                    });

            Publication publication = new Publication();

            publication.setUserProfile(userProfile);
            publication.setTitle(dto.getTitle());
            publication.setDescription(dto.getDescription());
            publication.setState(PublicationState.PENDING);

            // Asignar categorías si existen
            if (dto.getCategoryIds() != null && !dto.getCategoryIds().isEmpty()) {
                List<Category> categories = categoryRepository.findAllById(dto.getCategoryIds());
                publication.setCategories(categories);
            }

            Publication saved = publicationRepository.save(publication);
            return modelMapper.map(saved, PublicationResponseDTO.class);
        } catch (Exception e) {
            logger.error("Error al crear la publicación: " + e.getMessage(), e);
            throw new RuntimeException("Error al crear la publicación: " + e.getMessage(), e);
        }
    }

    @Transactional(readOnly = true)
    public PaginatedResponseDTO getPaginated(Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Publication> publicationsPage = publicationRepository.findAllNotDeleted(pageable);

        if (publicationsPage.isEmpty()) {
            logger.warn("No se encontraron publicaciones");
            throw new IllegalArgumentException("No se encontraron publicaciones");
        } else
            logger.info("Se encontraron " + publicationsPage.getTotalElements() + " publicaciones");

        return PaginationUtil.buildPaginatedResponse(publicationsPage, PublicationResponseDTO.class);
    }

    @Transactional(readOnly = true)
    public PaginatedResponseDTO getPaginatedByTitle(String title, Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Publication> publicationsPage = publicationRepository.findByTitleNotDeleted(title, pageable);

        if (publicationsPage.isEmpty()) {
            logger.warn("No se encontraron publicaciones con el título " + title);
            throw new IllegalArgumentException("No se encontraron publicaciones con el título " + title);
        } else
            logger.info("Se encontraron " + publicationsPage.getTotalElements() + " publicaciones con el título " + title);

        return PaginationUtil.buildPaginatedResponse(publicationsPage, PublicationResponseDTO.class);
    }

    @Transactional(readOnly = true)
    public PaginatedResponseDTO getPaginatedByDescription(String desc, Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Publication> publicationsPage = publicationRepository.findByDescriptionNotDeleted(desc, pageable);

        if (publicationsPage.isEmpty()) {
            logger.warn("No se encontraron publicaciones con la descripción " + desc);
            throw new IllegalArgumentException("No se encontraron publicaciones con la descripción " + desc);
        } else
            logger.info("Se encontraron " + publicationsPage.getTotalElements() + " publicaciones con la descripción " + desc);

        return PaginationUtil.buildPaginatedResponse(publicationsPage, PublicationResponseDTO.class);
    }

    @Transactional(readOnly = true)
    public PaginatedResponseDTO getPaginatedByState(String state, Integer page, Integer size) {
        // Convertir a enum
        PublicationState publicationState = PublicationState.valueOf(state.toUpperCase());

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Publication> publicationsPage = publicationRepository.findByStateNotDeleted(publicationState, pageable);

        if (publicationsPage.isEmpty()) {
            logger.warn("No se encontraron publicaciones con el estado " + state);
            throw new IllegalArgumentException("No se encontraron publicaciones con el estado " + state);
        } else
            logger.info("Se encontraron " + publicationsPage.getTotalElements() + " publicaciones con el estado " + state);

        return PaginationUtil.buildPaginatedResponse(publicationsPage, PublicationResponseDTO.class);
    }

    @Transactional(readOnly = true)
    public PaginatedResponseDTO getPaginatedByUser(Integer user, Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Publication> publicationsPage = publicationRepository.findByUserNotDeleted(user, pageable);

        if (publicationsPage.isEmpty()) {
            logger.warn("No se encontraron publicaciones con el ID de usuario " + user);
            throw new IllegalArgumentException("No se encontraron publicaciones con el ID de usuario " + user);
        } else
            logger.info("Se encontraron " + publicationsPage.getTotalElements() + " publicaciones con el ID de usuario " + user);

        return PaginationUtil.buildPaginatedResponse(publicationsPage, PublicationResponseDTO.class);
    }

    @Transactional(readOnly = true)
    public PaginatedResponseDTO getPaginatedByCategory(Integer cat, Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Publication> publicationsPage = publicationRepository.findByCategoryNotDeleted(cat, pageable);

        if (publicationsPage.isEmpty()) {
            logger.warn("No se encontraron publicaciones con el ID de categoría " + cat);
            throw new IllegalArgumentException("No se encontraron publicaciones con el ID de categoría " + cat);
        } else
            logger.info("Se encontraron " + publicationsPage.getTotalElements() + " publicaciones con el ID de categoría " + cat);

        return PaginationUtil.buildPaginatedResponse(publicationsPage, PublicationResponseDTO.class);
    }

    @Transactional(readOnly = true)
    public PublicationResponseDTO getById(Integer id) {
        Publication publication = publicationRepository.findByIdNotDeleted(id)
                .orElseThrow(() -> {
                    logger.error("No se encontró la publicación con ID " + id);
                    return new IllegalArgumentException(
                        String.format("Publicación no encontrada con ID: %s", id)
                    );
                });

        return modelMapper.map(publication, PublicationResponseDTO.class);
    }

    @Transactional
    public PublicationResponseDTO update(Integer id, PublicationRequestDTO dto) {
        Publication publication = publicationRepository.findByIdNotDeleted(id)
                .orElseThrow(() -> {
                    logger.error("No se encontró la publicación con ID " + id);
                    return new IllegalArgumentException(
                        String.format("Publicación no encontrada con ID: %s", id)
                    );
                });

        // Verificar si existe el perfil de usuario
        UserProfile userProfile = publicationRepository.findUserByIdNotDeleted(dto.getUserProfileId())
                .orElseThrow(() -> {
                    logger.error("No se encontró el perfil de usuario con ID " + dto.getUserProfileId());
                    return new IllegalArgumentException(
                        String.format("Perfil de usuario no encontrado con ID: %s", dto.getUserProfileId())
                    );
                });

        publication.setUserProfile(userProfile);
        publication.setTitle(dto.getTitle());
        publication.setDescription(dto.getDescription());

        // Actualizar categorías si se proporcionan
        if (dto.getCategoryIds() != null && !dto.getCategoryIds().isEmpty()) {
            List<Category> categories = categoryRepository.findAllById(dto.getCategoryIds());
            publication.setCategories(categories);
        }

        publication.setUpdatedAt(LocalDateTime.now());

        Publication updated = publicationRepository.save(publication);
        return modelMapper.map(updated, PublicationResponseDTO.class);
    }

    @Transactional
    public void delete(Integer id) {
        Publication publication = publicationRepository.findByIdNotDeleted(id)
                .orElseThrow(() -> {
                    logger.error("No se encontró la publicación con ID " + id);
                    return new IllegalArgumentException(
                        String.format("Publicación no encontrada con ID: %s", id)
                    );
                });

        // Soft delete (borrado lógico)
        publication.setIsDeleted(true);
        publication.setUpdatedAt(LocalDateTime.now());
        publicationRepository.save(publication);
    }

    @Transactional
    public PublicationResponseDTO approve(Integer id) {
        Publication publication = publicationRepository.findByIdNotDeleted(id)
                .orElseThrow(() -> {
                    logger.error("No se encontró la publicación con ID " + id);
                    return new IllegalArgumentException(
                        String.format("Publicación no encontrada con ID: %s", id)
                    );
                });

        publication.setState(PublicationState.APPROVED);
        publication.setUpdatedAt(LocalDateTime.now());

        Publication updated = publicationRepository.save(publication);
        return modelMapper.map(updated, PublicationResponseDTO.class);
    }

    @Transactional
    public PublicationResponseDTO reject(Integer id) {
        Publication publication = publicationRepository.findByIdNotDeleted(id)
                .orElseThrow(() -> {
                    logger.error("No se encontró la publicación con ID " + id);
                    return new IllegalArgumentException(
                        String.format("Publicación no encontrada con ID: %s", id)
                    );
                });

        publication.setState(PublicationState.REJECTED);
        publication.setUpdatedAt(LocalDateTime.now());

        Publication updated = publicationRepository.save(publication);
        return modelMapper.map(updated, PublicationResponseDTO.class);
    }
}