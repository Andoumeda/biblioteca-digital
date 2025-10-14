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
        log.info("REST request to create Book: {}", bookRequestDTO.getTitle());
        BookResponseDTO response = bookService.createBook(bookRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Override
    public ResponseEntity<PaginatedBookResponseDTO> getAllBooks(Integer page) {
        log.info("REST request to get all Books - page: {}", page);
        PaginatedBookResponseDTO response = bookService.getAllBooks(page);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<PaginatedBookResponseDTO> getBooksByTitle(String title, Integer page) {
        log.info("REST request to get Books by title: {} - page: {}", title, page);
        PaginatedBookResponseDTO response = bookService.getBooksByTitle(title, page);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<PaginatedBookResponseDTO> getBooksByPublicationId(Integer publicationId, Integer page) {
        log.info("REST request to get Books by Publication ID: {} - page: {}", publicationId, page);
        PaginatedBookResponseDTO response = bookService.getBooksByPublicationId(publicationId, page);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<PaginatedBookResponseDTO> getBooksByAuthorId(Integer id, Integer page) {
        log.info("REST request to get Books by Author ID: {} - page: {}", id, page);
        PaginatedBookResponseDTO response = bookService.getBooksByAuthorId(id, page);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<BookResponseDTO> getBookById(Integer id) {
        log.info("REST request to get Book by ID: {}", id);
        BookResponseDTO response = bookService.getBookById(id);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<BookResponseDTO> updateBook(Integer id, BookRequestDTO bookRequestDTO) {
        log.info("REST request to update Book: {}", id);
        BookResponseDTO response = bookService.updateBook(id, bookRequestDTO);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<Void> deleteBook(Integer id) {
        log.info("REST request to delete Book: {}", id);
        bookService.deleteBook(id);
        return ResponseEntity.noContent().build();
    }
}
