package com.library.books.controllers;

import com.library.books.api.BookAuthorsApi;
import com.library.books.services.BookAuthorService;
import com.library.dtos.BookAuthorRequestDTO;
import com.library.dtos.BookAuthorResponseDTO;
import com.library.dtos.PaginatedBookAuthorResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
@Slf4j
public class BookAuthorController implements BookAuthorsApi {

    private final BookAuthorService bookAuthorService;

    @Override
    public ResponseEntity<BookAuthorResponseDTO> createBookAuthor(BookAuthorRequestDTO dto) {
        log.info("Creando relación libro-autor: bookId={}, authorId={}", dto.getBookId(), dto.getAuthorId());
        return ResponseEntity.status(201).body(bookAuthorService.createBookAuthor(dto));
    }

    @Override
    public ResponseEntity<Void> deleteBookAuthor(Integer id) {
        log.info("Eliminando relación libro-autor id={}", id);
        bookAuthorService.deleteBookAuthor(id);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<BookAuthorResponseDTO> getBookAuthorById(Integer id) {
        log.info("Obteniendo relación libro-autor id={}", id);
        return ResponseEntity.ok(bookAuthorService.getBookAuthorById(id));
    }

    @Override
    public ResponseEntity<PaginatedBookAuthorResponseDTO> getBookAuthorsByFilters(
            Integer bookId, Integer authorId, String contribution, Integer page) {
        log.info("Buscando relaciones libro-autor con filtros: bookId={}, authorId={}, contribution={}, page={}",
                bookId, authorId, contribution, page);
        return ResponseEntity.ok(
            bookAuthorService.getBookAuthorsByFilters(bookId, authorId, contribution, page)
        );
    }

    @Override
    public ResponseEntity<BookAuthorResponseDTO> updateBookAuthor(Integer id, BookAuthorRequestDTO dto) {
        log.info("Actualizando relación libro-autor id={}", id);
        return ResponseEntity.ok(bookAuthorService.updateBookAuthor(id, dto));
    }
}
