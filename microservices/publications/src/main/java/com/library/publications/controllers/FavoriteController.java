package com.library.publications.controllers;

import com.library.publications.api.FavoritesApi;

import com.library.dtos.FavoriteRequestDTO;
import com.library.dtos.FavoriteResponseDTO;
import com.library.dtos.PaginatedResponseDTO;

import com.library.publications.services.FavoriteService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class FavoriteController implements FavoritesApi {
    @Autowired
    private FavoriteService favoriteService;

    @Override
    public ResponseEntity<FavoriteResponseDTO> addFavorite(FavoriteRequestDTO dto) {
        FavoriteResponseDTO created = favoriteService.add(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @Override
    public ResponseEntity<PaginatedResponseDTO> getAllFavorites(Integer page, Integer size) {
        PaginatedResponseDTO favorites = favoriteService.getPaginated(page, size);
        return ResponseEntity.ok(favorites);
    }

    @Override
    public ResponseEntity<PaginatedResponseDTO> getFavoritesByUser(Integer user, Integer page, Integer size) {
        PaginatedResponseDTO favorites = favoriteService.getPaginatedByUser(user, page, size);
        return ResponseEntity.ok(favorites);
    }

    @Override
    public ResponseEntity<PaginatedResponseDTO> getFavoritesByPublication(Integer pub, Integer page, Integer size) {
        PaginatedResponseDTO favorites = favoriteService.getPaginatedByPublication(pub, page, size);
        return ResponseEntity.ok(favorites);
    }

    @Override
    public ResponseEntity<FavoriteResponseDTO> getFavoriteById(Integer id) {
        FavoriteResponseDTO favorite = favoriteService.getById(id);
        return ResponseEntity.ok(favorite);
    }

    @Override
    public ResponseEntity<Void> removeFavorite(Integer id) {
        favoriteService.remove(id);
        return ResponseEntity.noContent().build();
    }
}