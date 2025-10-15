package com.library.books.controllers;

import com.library.books.api.AuthorsApi;
import com.library.books.services.AuthorService;
import com.library.dtos.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequiredArgsConstructor
@Slf4j
public class AuthorsController implements AuthorsApi {

    private final AuthorService authorService;

    @Override
    public ResponseEntity<PaginatedAuthorResponseDTO> getAuthorsByFilters(Integer bookId, String fullname, LocalDate min, LocalDate max, String nationality, Integer page) {
        log.info("REST request to get authors by filters - bookId: {}, fullname: {}, min: {}, max: {}, nationality: {}, page: {}",
                 bookId, fullname, min, max, nationality, page);
        PaginatedAuthorResponseDTO response = authorService.getAuthorsByFilters(bookId, fullname, min, max, nationality, page);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<AuthorResponseDTO> createAuthor(AuthorRequestDTO authorRequestDTO) {
        log.info("REST request to create Author: {}", authorRequestDTO.getFullName());
        AuthorResponseDTO response = authorService.createAuthor(authorRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Override
    public ResponseEntity<AuthorResponseDTO> getAuthorById(Integer id) {
        log.info("REST request to get Author by ID: {}", id);
        AuthorResponseDTO response = authorService.getAuthorById(id);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<AuthorResponseDTO> updateAuthor(Integer id, AuthorRequestDTO authorRequestDTO) {
        log.info("REST request to update Author: {}", id);
        AuthorResponseDTO response = authorService.updateAuthor(id, authorRequestDTO);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<Void> deleteAuthor(Integer id) {
        log.info("REST request to delete Author: {}", id);
        authorService.deleteAuthor(id);
        return ResponseEntity.noContent().build();
    }
}
