package com.library.books.controllers;

import com.library.books.api.BooksApi;
import com.library.books.services.BookService;
import com.library.dtos.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class BooksController implements BooksApi {

    private final BookService bookService;

    @Override
    public ResponseEntity<BookResponseDTO> createBook(BookRequestDTO bookRequestDTO) {
        log.info("Petición REST para crear libro: {}", bookRequestDTO.getTitle());
        BookResponseDTO response = bookService.createBook(bookRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Override
    public ResponseEntity<PaginatedBookResponseDTO> getBooksByFilters(String title, Integer publicationId, Integer authorId, Integer page) {
        log.info("Petición REST para obtener libros por filtros - título: {}, publicaciónId: {}, autorId: {}, página: {}",
                title, publicationId, authorId, page);
        PaginatedBookResponseDTO response = bookService.getBooksByFilters(title, publicationId, authorId, page);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<BookResponseDTO> getBookById(Integer id) {
        log.info("Petición REST para obtener libro por ID: {}", id);
        BookResponseDTO response = bookService.getBookById(id);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<BookResponseDTO> updateBook(Integer id, BookRequestDTO bookRequestDTO) {
        log.info("Petición REST para actualizar libro: {}", id);
        BookResponseDTO response = bookService.updateBook(id, bookRequestDTO);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<Void> deleteBook(Integer id) {
        log.info("Petición REST para eliminar libro: {}", id);
        bookService.deleteBook(id);
        return ResponseEntity.noContent().build();
    }
}
