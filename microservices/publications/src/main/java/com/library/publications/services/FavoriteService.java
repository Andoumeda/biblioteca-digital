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
            if (favoriteRepository.findByUserAndPublicationNotDeleted(dto.getUserProfileId(), dto.getPublicationId()).isPresent()) {
                logger.error("Esta publicación ya está en favoritos");
                throw new IllegalArgumentException("Esta publicación ya está en favoritos");
            }

            // Verificar si existe el perfil de usuario
            UserProfile userProfile = favoriteRepository.findUserByIdNotDeleted(dto.getUserProfileId())
                    .orElseThrow(() -> {
                        logger.error("No se encontró el perfil de usuario con ID " + dto.getUserProfileId());
                        return new IllegalArgumentException(
                            String.format("Perfil de usuario no encontrado con ID: %s", dto.getUserProfileId())
                        );
                    });

            // Verificar si existe la publicación
            Publication publication = publicationRepository.findByIdNotDeleted(dto.getPublicationId())
                    .orElseThrow(() -> {
                        logger.error("No se encontró la publicación con ID " + dto.getPublicationId());
                        return new IllegalArgumentException(
                            String.format("Publicación no encontrada con ID: %s", dto.getPublicationId())
                        );
                    });

            Favorite favorite = new Favorite();

            favorite.setUserProfile(userProfile);
            favorite.setPublication(publication);

            Favorite saved = favoriteRepository.save(favorite);
            return modelMapper.map(saved, FavoriteResponseDTO.class);
        } catch (Exception e) {
            logger.error("Error al agregar el favorito: " + e.getMessage(), e);
            throw new RuntimeException("Error al agregar el favorito: " + e.getMessage(), e);
        }
    }

    @Transactional(readOnly = true)
    public PaginatedResponseDTO getPaginated(Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Favorite> favoritesPage = favoriteRepository.findAllNotDeleted(pageable);

        if (favoritesPage.isEmpty()) {
            logger.warn("No se encontraron favoritos");
            throw new IllegalArgumentException("No se encontraron favoritos");
        } else
            logger.info("Se encontraron " + favoritesPage.getTotalElements() + " favoritos");

        return PaginationUtil.buildPaginatedResponse(favoritesPage, FavoriteResponseDTO.class);
    }

    @Transactional(readOnly = true)
    public PaginatedResponseDTO getPaginatedByUser(Integer user, Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Favorite> favoritesPage = favoriteRepository.findByUserNotDeleted(user, pageable);

        if (favoritesPage.isEmpty()) {
            logger.warn("No se encontraron favoritos con el ID de usuario " + user);
            throw new IllegalArgumentException("No se encontraron favoritos con el ID de usuario " + user);
        } else
            logger.info("Se encontraron " + favoritesPage.getTotalElements() + " favoritos con el ID de usuario " + user);

        return PaginationUtil.buildPaginatedResponse(favoritesPage, FavoriteResponseDTO.class);
    }

    @Transactional(readOnly = true)
    public PaginatedResponseDTO getPaginatedByPublication(Integer pub, Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Favorite> favoritePage = favoriteRepository.findByPublicationNotDeleted(pub, pageable);

        if (favoritePage.isEmpty()) {
            logger.warn("No se encontraron favoritos con el ID de publicación " + pub);
            throw new IllegalArgumentException("No se encontraron favoritos con el ID de publicación " + pub);
        } else
            logger.info("Se encontraron " + favoritePage.getTotalElements() + " favoritos con el ID de publicación " + pub);

        return PaginationUtil.buildPaginatedResponse(favoritePage, FavoriteResponseDTO.class);
    }

    @Transactional(readOnly = true)
    public FavoriteResponseDTO getById(Integer id) {
        Favorite favorite = favoriteRepository.findByIdNotDeleted(id)
                .orElseThrow(() -> {
                    logger.error("No se encontró el favorito con ID " + id);
                    return new IllegalArgumentException(
                        String.format("Favorito no encontrado con ID: %s", id)
                    );
                });

        return modelMapper.map(favorite, FavoriteResponseDTO.class);
    }

    @Transactional
    public void remove(Integer id) {
        Favorite favorite = favoriteRepository.findByIdNotDeleted(id)
                .orElseThrow(() -> {
                    logger.error("No se encontró el favorito con ID " + id);
                    return new IllegalArgumentException(
                        String.format("Favorito no encontrado con ID: %s", id)
                    );
                });

        // Soft delete (borrado lógico)
        favorite.setIsDeleted(true);
        favorite.setUpdatedAt(LocalDateTime.now());
        favoriteRepository.save(favorite);
    }
}