package com.library.publications.controllers;

import com.library.dtos.FavoriteRequestDTO;
import com.library.dtos.FavoriteResponseDTO;
import com.library.dtos.PaginatedResponseDTO;

import com.library.publications.services.FavoriteService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/favorites")
public class FavoriteController {
    @Autowired
    private FavoriteService favoriteService;

    @PostMapping
    public ResponseEntity<FavoriteResponseDTO> add(@RequestBody FavoriteRequestDTO dto) {
        FavoriteResponseDTO created = favoriteService.add(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping({"/page/{page}/size/{size}", "/page/{page}", "/size/{size}"})
    public ResponseEntity<PaginatedResponseDTO> getPaginated(
            @PathVariable(required = false) Integer page,
            @PathVariable(required = false) Integer size) {

        int pageNum = page != null ? page : 0;
        int sizeNum = size != null ? size : 20;

        PaginatedResponseDTO favorites = favoriteService.getPaginated(pageNum, sizeNum);
        return ResponseEntity.ok(favorites);
    }

    @GetMapping({"/user/{user}/page/{page}/size/{size}", "/user/{user}/page/{page}", "/user/{user}/size/{size}"})
    public ResponseEntity<PaginatedResponseDTO> getPaginatedByUser(
            @PathVariable Integer user,
            @PathVariable(required = false) Integer page,
            @PathVariable(required = false) Integer size) {

        int pageNum = page != null ? page : 0;
        int sizeNum = size != null ? size : 20;

        PaginatedResponseDTO favorites = favoriteService.getPaginatedByUser(user, pageNum, sizeNum);
        return ResponseEntity.ok(favorites);
    }

    @GetMapping({"/publication/{pub}/page/{page}/size/{size}", "/publication/{pub}/page/{page}", "/publication/{pub}/size/{size}"})
    public ResponseEntity<PaginatedResponseDTO> getPaginatedByPublication(
            @PathVariable Integer pub,
            @PathVariable(required = false) Integer page,
            @PathVariable(required = false) Integer size) {

        int pageNum = page != null ? page : 0;
        int sizeNum = size != null ? size : 20;

        PaginatedResponseDTO favorites = favoriteService.getPaginatedByPublication(pub, pageNum, sizeNum);
        return ResponseEntity.ok(favorites);
    }

    @GetMapping("/{id}")
    public ResponseEntity<FavoriteResponseDTO> getById(@PathVariable Integer id) {
        FavoriteResponseDTO favorite = favoriteService.getById(id);
        return ResponseEntity.ok(favorite);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remove(@PathVariable Integer id) {
        favoriteService.remove(id);
        return ResponseEntity.noContent().build();
    }
}