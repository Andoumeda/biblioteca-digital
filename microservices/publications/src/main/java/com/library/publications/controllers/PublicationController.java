package com.library.publications.controllers;

import com.library.publications.api.PublicationsApi;

import com.library.dtos.PublicationRequestDTO;
import com.library.dtos.PublicationResponseDTO;
import com.library.dtos.PaginatedResponseDTO;

import com.library.publications.services.PublicationService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class PublicationController implements PublicationsApi {
    @Autowired
    private PublicationService publicationService;

    @Override
    public ResponseEntity<PublicationResponseDTO> createPublication(PublicationRequestDTO dto) {
        PublicationResponseDTO created = publicationService.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @Override
    public ResponseEntity<PaginatedResponseDTO> getAllPublications(Integer page, Integer size) {
        PaginatedResponseDTO publications = publicationService.getPaginated(page, size);
        return ResponseEntity.ok(publications);
    }

    @Override
    public ResponseEntity<PaginatedResponseDTO> getPublicationsByTitle(String title, Integer page, Integer size) {
        PaginatedResponseDTO publications = publicationService.getPaginatedByTitle(title, page, size);
        return ResponseEntity.ok(publications);
    }

    @Override
    public ResponseEntity<PaginatedResponseDTO> getPublicationsByDescription(String desc, Integer page, Integer size) {
        PaginatedResponseDTO publications = publicationService.getPaginatedByDescription(desc, page, size);
        return ResponseEntity.ok(publications);
    }

    @Override
    public ResponseEntity<PaginatedResponseDTO> getPublicationsByState(String state, Integer page, Integer size) {
        PaginatedResponseDTO publications = publicationService.getPaginatedByState(state, page, size);
        return ResponseEntity.ok(publications);
    }

    @Override
    public ResponseEntity<PaginatedResponseDTO> getPublicationsByUser(Integer user, Integer page, Integer size) {
        PaginatedResponseDTO publications = publicationService.getPaginatedByUser(user, page, size);
        return ResponseEntity.ok(publications);
    }

    @Override
    public ResponseEntity<PaginatedResponseDTO> getPublicationsByCategory(Integer cat, Integer page, Integer size) {
        PaginatedResponseDTO publications = publicationService.getPaginatedByCategory(cat, page, size);
        return ResponseEntity.ok(publications);
    }

    @Override
    public ResponseEntity<PublicationResponseDTO> getPublicationById(Integer id) {
        PublicationResponseDTO publication = publicationService.getById(id);
        return ResponseEntity.ok(publication);
    }

    @Override
    public ResponseEntity<PublicationResponseDTO> updatePublication(Integer id, PublicationRequestDTO dto) {
        PublicationResponseDTO updated = publicationService.update(id, dto);
        return ResponseEntity.ok(updated);
    }

    @Override
    public ResponseEntity<Void> deletePublication(Integer id) {
        publicationService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<PublicationResponseDTO> approvePublication(Integer id) {
        PublicationResponseDTO approved = publicationService.approve(id);
        return ResponseEntity.ok(approved);
    }

    @Override
    public ResponseEntity<PublicationResponseDTO> rejectPublication(Integer id) {
        PublicationResponseDTO rejected = publicationService.reject(id);
        return ResponseEntity.ok(rejected);
    }
}