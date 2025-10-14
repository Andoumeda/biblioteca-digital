package com.library.publications.services;

import com.library.dtos.FavoriteRequestDTO;
import com.library.dtos.FavoriteResponseDTO;
import com.library.dtos.PaginatedResponseDTO;

import com.library.entities.Favorite;
import com.library.entities.Publication;
import com.library.entities.UserProfile;

import com.library.publications.repositories.FavoriteRepository;
import com.library.publications.repositories.PublicationRepository;

import com.library.publications.exceptions.DuplicateResourceException;
import com.library.publications.exceptions.ResourceNotFoundException;

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
import java.util.ArrayList;
import java.util.List;

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
                throw new DuplicateResourceException("Esta publicación ya está en favoritos");
            }

            // Verificar si existe el perfil de usuario
            UserProfile userProfile = favoriteRepository.findUserByIdNotDeleted(dto.getUserProfileId())
                    .orElseThrow(() -> {
                        logger.error("No se encontró el User Profile con ID " + dto.getUserProfileId());
                        return new ResourceNotFoundException("User Profile", "ID", dto.getUserProfileId());
                    });

            // Verificar si existe la publicación
            Publication publication = publicationRepository.findByIdNotDeleted(dto.getPublicationId())
                    .orElseThrow(() -> {
                        logger.error("No se encontró la Publicación con ID " + dto.getPublicationId());
                        return new ResourceNotFoundException("Publicación", "ID", dto.getPublicationId());
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
        Page<Favorite> favoritePage = favoriteRepository.findAllNotDeleted(pageable);

        return buildPaginatedResponse(favoritePage);
    }

    @Transactional(readOnly = true)
    public PaginatedResponseDTO getPaginatedByUser(Integer user, Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Favorite> favoritePage = favoriteRepository.findByUserNotDeleted(user, pageable);

        return buildPaginatedResponse(favoritePage);
    }

    @Transactional(readOnly = true)
    public PaginatedResponseDTO getPaginatedByPublication(Integer pub, Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Favorite> favoritePage = favoriteRepository.findByPublicationNotDeleted(pub, pageable);

        return buildPaginatedResponse(favoritePage);
    }

    @Transactional(readOnly = true)
    public FavoriteResponseDTO getById(Integer id) {
        Favorite favorite = favoriteRepository.findByIdNotDeleted(id)
                .orElseThrow(() -> {
                    logger.error("No se encontró el favorito con ID " + id);
                    return new ResourceNotFoundException("Favorito", "ID", id);
                });

        return modelMapper.map(favorite, FavoriteResponseDTO.class);
    }

    @Transactional
    public void remove(Integer id) {
        Favorite favorite = favoriteRepository.findByIdNotDeleted(id)
                .orElseThrow(() -> {
                    logger.error("No se encontró el favorito con ID " + id);
                    return new ResourceNotFoundException("Favorito", "ID", id);
                });

        // Soft delete (borrado lógico)
        favorite.setIsDeleted(true);
        favorite.setUpdatedAt(LocalDateTime.now());
        favoriteRepository.save(favorite);
    }

    // Método auxiliar para construir respuesta paginada
    private PaginatedResponseDTO buildPaginatedResponse(Page<Favorite> page) {
        PaginatedResponseDTO response = new PaginatedResponseDTO();

        List<FavoriteResponseDTO> data = page.getContent().stream()
                .map(fav -> modelMapper.map(fav, FavoriteResponseDTO.class))
                .toList();

        response.setData(new ArrayList<>(data));
        response.setPageSize(page.getSize());
        response.setTotalItems((int) page.getTotalElements());
        response.setCurrentPage(page.getNumber());
        response.setTotalPages(page.getTotalPages());
        response.setPrev(page.hasPrevious());
        response.setNext(page.hasNext());

        return response;
    }
}