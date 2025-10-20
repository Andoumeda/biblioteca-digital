package com.library.publications.controllers;

import com.library.publications.api.FavoritesApi;

import com.library.dtos.FavoriteRequestDTO;
import com.library.dtos.FavoriteResponseDTO;
import com.library.dtos.PaginatedResponseDTO;

import com.library.publications.services.FavoriteService;

import org.springframework.beans.factory.annotation.Autowired;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class FavoriteController implements FavoritesApi {
    @Autowired
    private FavoriteService favoriteService;
    private final Logger logger = LoggerFactory.getLogger(FavoriteController.class);

    @Override
    public ResponseEntity<FavoriteResponseDTO> addFavorite(FavoriteRequestDTO dto) {
        logger.info("Petición (POST /favorites) para agregar un favorito con usuarioId: {} y publicacionId: {}", dto.getUserProfileId(), dto.getPublicationId());
        FavoriteResponseDTO created = favoriteService.add(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @Override
    public ResponseEntity<PaginatedResponseDTO> getAllFavorites(Integer page, Integer size) {
        logger.info("Petición (GET /favorites/page/{page}/size/{size}) para obtener todos los favoritos - página: {}, tamaño: {}", page, size);
        PaginatedResponseDTO favorites = favoriteService.getPaginated(page, size);
        return ResponseEntity.ok(favorites);
    }

    @Override
    public ResponseEntity<PaginatedResponseDTO> getFavoritesByUser(Integer user, Integer page, Integer size) {
        logger.info("Petición (GET /favorites/user/{user}/page/{page}/size/{size}) para obtener favoritos por ID de usuario: {} - página: {}, tamaño: {}", user, page, size);
        PaginatedResponseDTO favorites = favoriteService.getPaginatedByUser(user, page, size);
        return ResponseEntity.ok(favorites);
    }

    @Override
    public ResponseEntity<PaginatedResponseDTO> getFavoritesByPublication(Integer pub, Integer page, Integer size) {
        logger.info("Petición (GET /favorites/publication/{pub}/page/{page}/size/{size}) para obtener favoritos por ID de publicación: {} - página: {}, tamaño: {}", pub, page, size);
        PaginatedResponseDTO favorites = favoriteService.getPaginatedByPublication(pub, page, size);
        return ResponseEntity.ok(favorites);
    }

    @Override
    public ResponseEntity<FavoriteResponseDTO> getFavoriteById(Integer id) {
        logger.info("Petición (GET /favorites/{id}) para obtener favorito por ID: {}", id);
        FavoriteResponseDTO favorite = favoriteService.getById(id);
        return ResponseEntity.ok(favorite);
    }

    @Override
    public ResponseEntity<Void> removeFavorite(Integer id) {
        logger.info("Petición (DELETE /favorites/{id}) para eliminar favorito con ID: {}", id);
        favoriteService.remove(id);
        return ResponseEntity.noContent().build();
    }
}