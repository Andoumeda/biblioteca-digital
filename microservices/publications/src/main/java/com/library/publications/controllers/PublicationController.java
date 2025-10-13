package com.library.publications.controllers;

import com.library.dtos.PaginatedResponseDTO;
import com.library.dtos.PublicationRequestDTO;
import com.library.dtos.PublicationResponseDTO;

import com.library.publications.services.PublicationService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/publications")
public class PublicationController {
    @Autowired
    private PublicationService publicationService;

    @PostMapping
    public ResponseEntity<PublicationResponseDTO> create(@RequestBody PublicationRequestDTO dto) {
        PublicationResponseDTO created = publicationService.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping({"/page/{page}/size/{size}", "/page/{page}", "/size/{size}"})
    public ResponseEntity<PaginatedResponseDTO> getPaginated(
            @PathVariable(required = false) Integer page,
            @PathVariable(required = false) Integer size) {

        int pageNum = page != null ? page : 0;
        int sizeNum = size != null ? size : 20;

        PaginatedResponseDTO publications = publicationService.getPaginated(pageNum, sizeNum);
        return ResponseEntity.ok(publications);
    }

    @GetMapping({"/title/{title}/page/{page}/size/{size}", "/title/{title}/page/{page}", "/title/{title}/size/{size}"})
    public ResponseEntity<PaginatedResponseDTO> getPaginatedByTitle(
            @PathVariable String title,
            @PathVariable(required = false) Integer page,
            @PathVariable(required = false) Integer size) {

        int pageNum = page != null ? page : 0;
        int sizeNum = size != null ? size : 20;

        PaginatedResponseDTO publications = publicationService.getPaginatedByTitle(title, pageNum, sizeNum);
        return ResponseEntity.ok(publications);
    }

    @GetMapping({"/description/{desc}/page/{page}/size/{size}", "/description/{desc}/page/{page}", "/description/{desc}/size/{size}"})
    public ResponseEntity<PaginatedResponseDTO> getPaginatedByDescription(
            @PathVariable String desc,
            @PathVariable(required = false) Integer page,
            @PathVariable(required = false) Integer size) {

        int pageNum = page != null ? page : 0;
        int sizeNum = size != null ? size : 20;

        PaginatedResponseDTO publications = publicationService.getPaginatedByDescription(desc, pageNum, sizeNum);
        return ResponseEntity.ok(publications);
    }

    @GetMapping({"/state/{state}/page/{page}/size/{size}", "/state/{state}/page/{page}", "/state/{state}/size/{size}"})
    public ResponseEntity<PaginatedResponseDTO> getPaginatedByState(
            @PathVariable String state,
            @PathVariable(required = false) Integer page,
            @PathVariable(required = false) Integer size) {

        int pageNum = page != null ? page : 0;
        int sizeNum = size != null ? size : 20;

        PaginatedResponseDTO publications = publicationService.getPaginatedByState(state, pageNum, sizeNum);
        return ResponseEntity.ok(publications);
    }

    @GetMapping({"/user/{user}/page/{page}/size/{size}", "/user/{user}/page/{page}", "/user/{user}/size/{size}"})
    public ResponseEntity<PaginatedResponseDTO> getPaginatedByUser(
            @PathVariable Integer user,
            @PathVariable(required = false) Integer page,
            @PathVariable(required = false) Integer size) {

        int pageNum = page != null ? page : 0;
        int sizeNum = size != null ? size : 20;

        PaginatedResponseDTO publications = publicationService.getPaginatedByUser(user, pageNum, sizeNum);
        return ResponseEntity.ok(publications);
    }

    @GetMapping({"/category/{cat}/page/{page}/size/{size}", "/category/{cat}/page/{page}", "/category/{cat}/size/{size}"})
    public ResponseEntity<PaginatedResponseDTO> getPaginatedByCategory(
            @PathVariable Integer cat,
            @PathVariable(required = false) Integer page,
            @PathVariable(required = false) Integer size) {

        int pageNum = page != null ? page : 0;
        int sizeNum = size != null ? size : 20;

        PaginatedResponseDTO publications = publicationService.getPaginatedByCategory(cat, pageNum, sizeNum);
        return ResponseEntity.ok(publications);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PublicationResponseDTO> getById(@PathVariable Integer id) {
        PublicationResponseDTO publication = publicationService.getById(id);
        return ResponseEntity.ok(publication);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PublicationResponseDTO> update(@PathVariable Integer id, @RequestBody PublicationRequestDTO dto) {
        PublicationResponseDTO updated = publicationService.update(id, dto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        publicationService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/approve")
    public ResponseEntity<PublicationResponseDTO> approve(@PathVariable Integer id) {
        PublicationResponseDTO approved = publicationService.approve(id);
        return ResponseEntity.ok(approved);
    }

    @PatchMapping("/{id}/reject")
    public ResponseEntity<PublicationResponseDTO> reject(@PathVariable Integer id) {
        PublicationResponseDTO rejected = publicationService.reject(id);
        return ResponseEntity.ok(rejected);
    }
}