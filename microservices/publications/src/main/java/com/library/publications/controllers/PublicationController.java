package com.library.publications.controllers;

import com.library.publications.api.PublicationsApi;

import com.library.dtos.PublicationRequestDTO;
import com.library.dtos.PublicationResponseDTO;
import com.library.dtos.PaginatedResponseDTO;

import com.library.publications.services.PublicationService;

import org.springframework.beans.factory.annotation.Autowired;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class PublicationController implements PublicationsApi {
    @Autowired
    private PublicationService publicationService;
    private final Logger logger = LoggerFactory.getLogger(PublicationController.class);

    @Override
    public ResponseEntity<PublicationResponseDTO> createPublication(PublicationRequestDTO dto) {
        logger.info("Petición (POST /publications) para crear una publicación con título: {}", dto.getTitle());
        PublicationResponseDTO created = publicationService.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @Override
    public ResponseEntity<PaginatedResponseDTO> getAllPublications(Integer page, Integer size) {
        logger.info("Petición (GET /publications/page/{page}/size/{size}) para obtener todas las publicaciones - página: {}, tamaño: {}", page, size);
        PaginatedResponseDTO publications = publicationService.getPaginated(page, size);
        return ResponseEntity.ok(publications);
    }

    @Override
    public ResponseEntity<PaginatedResponseDTO> getPublicationsByTitle(String title, Integer page, Integer size) {
        logger.info("Petición (GET /publications/title/{title}/page/{page}/size/{size}) para obtener publicaciones por título: {} - página: {}, tamaño: {}", title, page, size);
        PaginatedResponseDTO publications = publicationService.getPaginatedByTitle(title, page, size);
        return ResponseEntity.ok(publications);
    }

    @Override
    public ResponseEntity<PaginatedResponseDTO> getPublicationsByDescription(String desc, Integer page, Integer size) {
        logger.info("Petición (GET /publications/description/{desc}/page/{page}/size/{size}) para obtener publicaciones por descripción: {} - página: {}, tamaño: {}", desc, page, size);
        PaginatedResponseDTO publications = publicationService.getPaginatedByDescription(desc, page, size);
        return ResponseEntity.ok(publications);
    }

    @Override
    public ResponseEntity<PaginatedResponseDTO> getPublicationsByState(String state, Integer page, Integer size) {
        logger.info("Petición (GET /publications/state/{state}/page/{page}/size/{size}) para obtener publicaciones por estado: {} - página: {}, tamaño: {}", state, page, size);
        PaginatedResponseDTO publications = publicationService.getPaginatedByState(state, page, size);
        return ResponseEntity.ok(publications);
    }

    @Override
    public ResponseEntity<PaginatedResponseDTO> getPublicationsByUser(Integer user, Integer page, Integer size) {
        logger.info("Petición (GET /publications/user/{user}/page/{page}/size/{size}) para obtener publicaciones por ID de usuario: {} - página: {}, tamaño: {}", user, page, size);
        PaginatedResponseDTO publications = publicationService.getPaginatedByUser(user, page, size);
        return ResponseEntity.ok(publications);
    }

    @Override
    public ResponseEntity<PaginatedResponseDTO> getPublicationsByCategory(Integer cat, Integer page, Integer size) {
        logger.info("Petición (GET /publications/category/{cat}/page/{page}/size/{size}) para obtener publicaciones por ID de categoría: {} - página: {}, tamaño: {}", cat, page, size);
        PaginatedResponseDTO publications = publicationService.getPaginatedByCategory(cat, page, size);
        return ResponseEntity.ok(publications);
    }

    @Override
    public ResponseEntity<PublicationResponseDTO> getPublicationById(Integer id) {
        logger.info("Petición (GET /publications/{id}) para obtener publicación por ID: {}", id);
        PublicationResponseDTO publication = publicationService.getById(id);
        return ResponseEntity.ok(publication);
    }

    @Override
    public ResponseEntity<PublicationResponseDTO> updatePublication(Integer id, PublicationRequestDTO dto) {
        logger.info("Petición (PUT /publications/{id}) para actualizar publicación con ID: {} con nuevo título: {}", id, dto.getTitle());
        PublicationResponseDTO updated = publicationService.update(id, dto);
        return ResponseEntity.ok(updated);
    }

    @Override
    public ResponseEntity<Void> deletePublication(Integer id) {
        logger.info("Petición (DELETE /publications/{id}) para eliminar publicación con ID: {}", id);
        publicationService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<PublicationResponseDTO> approvePublication(Integer id) {
        logger.info("Petición (PATCH /publications/{id}/approve) para aprobar publicación con ID: {}", id);
        PublicationResponseDTO approved = publicationService.approve(id);
        return ResponseEntity.ok(approved);
    }

    @Override
    public ResponseEntity<PublicationResponseDTO> rejectPublication(Integer id) {
        logger.info("Petición (PATCH /publications/{id}/reject) para rechazar publicación con ID: {}", id);
        PublicationResponseDTO rejected = publicationService.reject(id);
        return ResponseEntity.ok(rejected);
    }
}