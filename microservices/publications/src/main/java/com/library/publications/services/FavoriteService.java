package com.library.publications.services;

import com.library.dtos.FavoriteRequestDTO;
import com.library.dtos.FavoriteResponseDTO;
import com.library.dtos.PaginatedResponseDTO;

import com.library.entities.Favorite;
import com.library.entities.Publication;
import com.library.entities.UserProfile;

import com.library.publications.repositories.FavoriteRepository;
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
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class FavoriteService {
    private final FavoriteRepository favoriteRepository;
    private final PublicationRepository publicationRepository;
    private final ModelMapper modelMapper;
    private final Logger logger = LoggerFactory.getLogger(FavoriteService.class);

    @Transactional
    public FavoriteResponseDTO add(FavoriteRequestDTO dto) {
        try {
            // Verificar que no existe ya el favorito
            logger.debug("Consulta a la BD: SELECT f FROM Favorite f WHERE f.userProfile.id = :userProfileId AND f.publication.id = :publicationId AND f.isDeleted = false");
            if (favoriteRepository.findByUserProfileIdAndPublicationIdAndIsDeletedFalse(dto.getUserProfileId(), dto.getPublicationId()).isPresent()) {
                logger.error("Esta publicación ya está en favoritos");
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Esta publicación ya está en favoritos");
            }

            // Verificar si existe el perfil de usuario
            logger.debug("Consulta a la BD: SELECT u FROM UserProfile u WHERE u.id = :id AND u.isDeleted = false");
            UserProfile userProfile = favoriteRepository.findUserByIdNotDeleted(dto.getUserProfileId())
                    .orElseThrow(() -> {
                        logger.error("No se encontró el perfil de usuario con ID {}", dto.getUserProfileId());
                        return new ResponseStatusException(HttpStatus.NOT_FOUND, "Perfil de usuario no encontrado con ID: " + dto.getUserProfileId());
                    });

            // Verificar si existe la publicación
            logger.debug("Consulta a la BD: SELECT p FROM Publication p WHERE p.id = :id AND p.isDeleted = false");
            Publication publication = publicationRepository.findByIdAndIsDeletedFalse(dto.getPublicationId())
                    .orElseThrow(() -> {
                        logger.error("No se encontró la publicación con ID {}", dto.getPublicationId());
                        return new ResponseStatusException(HttpStatus.NOT_FOUND, "Publicación no encontrada con ID: " + dto.getPublicationId());
                    });

            Favorite favorite = new Favorite();

            favorite.setUserProfile(userProfile);
            favorite.setPublication(publication);

            Favorite saved = favoriteRepository.save(favorite);
            return modelMapper.map(saved, FavoriteResponseDTO.class);
        } catch (Exception e) {
            logger.error("Error al agregar el favorito: {}", e.getMessage(), e);
            throw new RuntimeException("Error al agregar el favorito: " + e.getMessage(), e);
        }
    }

    @Transactional(readOnly = true)
    public PaginatedResponseDTO getPaginated(Integer page, Integer size) {
        logger.debug("Consulta a la BD: SELECT f FROM Favorite f WHERE f.isDeleted = false");
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Favorite> favoritesPage = favoriteRepository.findAllByIsDeletedFalse(pageable);

        if (favoritesPage.isEmpty()) {
            logger.warn("No se encontraron favoritos");
            throw new ResponseStatusException(HttpStatus.NO_CONTENT, "No se encontraron favoritos");
        } else
            logger.info("Se encontraron {} favoritos", favoritesPage.getTotalElements());

        return PaginationUtil.buildPaginatedResponse(favoritesPage, FavoriteResponseDTO.class);
    }

    @Transactional(readOnly = true)
    public PaginatedResponseDTO getPaginatedByUser(Integer user, Integer page, Integer size) {
        logger.debug("Consulta a la BD: SELECT f FROM Favorite f WHERE f.userProfile.id = :userProfileId AND f.isDeleted = false");
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Favorite> favoritesPage = favoriteRepository.findByUserProfileIdAndIsDeletedFalse(user, pageable);

        if (favoritesPage.isEmpty()) {
            logger.warn("No se encontraron favoritos con el ID de usuario {}", user);
            throw new ResponseStatusException(HttpStatus.NO_CONTENT, "No se encontraron favoritos con el ID de usuario " + user);
        } else
            logger.info("Se encontraron {} favoritos con el ID de usuario {}", favoritesPage.getTotalElements(), user);

        return PaginationUtil.buildPaginatedResponse(favoritesPage, FavoriteResponseDTO.class);
    }

    @Transactional(readOnly = true)
    public PaginatedResponseDTO getPaginatedByPublication(Integer pub, Integer page, Integer size) {
        logger.debug("Consulta a la BD: SELECT f FROM Favorite f WHERE f.publication.id = :publicationId AND f.isDeleted = false");
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Favorite> favoritePage = favoriteRepository.findByPublicationIdAndIsDeletedFalse(pub, pageable);

        if (favoritePage.isEmpty()) {
            logger.warn("No se encontraron favoritos con el ID de publicación {}", pub);
            throw new ResponseStatusException(HttpStatus.NO_CONTENT, "No se encontraron favoritos con el ID de publicación " + pub);
        } else
            logger.info("Se encontraron {} favoritos con el ID de publicación {}", favoritePage.getTotalElements(), pub);

        return PaginationUtil.buildPaginatedResponse(favoritePage, FavoriteResponseDTO.class);
    }

    @Transactional(readOnly = true)
    public FavoriteResponseDTO getById(Integer id) {
        logger.debug("Consulta a la BD: SELECT f FROM Favorite f WHERE f.id = :id AND f.isDeleted = false");
        Favorite favorite = favoriteRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> {
                    logger.error("No se encontró el favorito con ID {}", id);
                    return new ResponseStatusException(HttpStatus.NOT_FOUND, "Favorito no encontrado con ID: " + id);
                });

        return modelMapper.map(favorite, FavoriteResponseDTO.class);
    }

    @Transactional
    public void remove(Integer id) {
        logger.debug("Consulta a la BD: SELECT f FROM Favorite f WHERE f.id = :id AND f.isDeleted = false");
        Favorite favorite = favoriteRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> {
                    logger.error("No se encontró el favorito con ID {}", id);
                    return new ResponseStatusException(HttpStatus.NOT_FOUND, "Favorito no encontrado con ID: " + id);
                });

        // Soft delete (borrado lógico)
        favorite.setIsDeleted(true);
        favorite.setUpdatedAt(LocalDateTime.now());
        favoriteRepository.save(favorite);
    }
}