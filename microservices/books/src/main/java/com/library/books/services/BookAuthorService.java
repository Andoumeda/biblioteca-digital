package com.library.books.services;

import com.library.books.config.PaginationConfig;
import com.library.books.exceptions.BadRequestException;
import com.library.books.exceptions.ConflictException;
import com.library.books.mappers.BookAuthorMapper;
import com.library.books.repositories.BookAuthorRepository;
import com.library.books.repositories.BookRepository;
import com.library.books.repositories.AuthorRepository;
import com.library.books.utils.NormalizeParameter;
import com.library.dtos.BookAuthorRequestDTO;
import com.library.dtos.BookAuthorResponseDTO;
import com.library.dtos.PaginatedBookAuthorResponseDTO;
import com.library.entities.Book;
import com.library.entities.Author;
import com.library.entities.BookAuthor;
import com.library.books.exceptions.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.CacheEvict;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookAuthorService {
    private final BookAuthorRepository bookAuthorRepository;
    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;
    private final BookAuthorMapper bookAuthorMapper;
    private final PaginationConfig paginationConfig;

    @Transactional
    @CachePut(value = "bookAuthors", key = "#result.id")
    public BookAuthorResponseDTO createBookAuthor(BookAuthorRequestDTO dto) {
        // Validar IDs requeridos y positivos
        if (dto.getBookId() == null || dto.getBookId() <= 0) {
            log.error("Error al crear relación libro-autor: ID de libro inválido: {}", dto.getBookId());
            throw new BadRequestException("El ID del libro debe ser un número positivo");
        }
        if (dto.getAuthorId() == null || dto.getAuthorId() <= 0) {
            log.error("Error al crear relación libro-autor: ID de autor inválido: {}", dto.getAuthorId());
            throw new BadRequestException("El ID del autor debe ser un número positivo");
        }

        // ContributionType obligatorio
        if (dto.getContributionType() == null || dto.getContributionType().getValue().isEmpty()) {
            log.error("Error al crear relación libro-autor: contributionType es obligatorio");
            throw new BadRequestException("El tipo de contribución es obligatorio");
        }

        // Validar existencia de libro y autor (no eliminados)
        Book book = bookRepository.findByIdAndIsDeletedFalse(dto.getBookId())
                .orElseThrow(() -> new ResourceNotFoundException("Libro", "id", dto.getBookId()));
        Author author = authorRepository.findByIdAndIsDeletedFalse(dto.getAuthorId())
                .orElseThrow(() -> new ResourceNotFoundException("Autor", "id", dto.getAuthorId()));

        // Validar que no exista ya una relación igual (no eliminada)
        Optional<BookAuthor> duplicate = bookAuthorRepository.findByBookIdAndAuthorIdAndIsDeletedFalse(
            dto.getBookId(), dto.getAuthorId()
        );
        if (duplicate.isPresent()) {
            log.error("Error al crear relación libro-autor: Ya existe una relación igual (bookId={}, authorId={}, contributionType={})", dto.getBookId(), dto.getAuthorId(), dto.getContributionType());
            throw new ConflictException("Ya existe una relación libro-autor con ese tipo de contribución");
        }

        // Crear y guardar la relación
        BookAuthor bookAuthor = new BookAuthor();
        bookAuthor.setBook(book);
        bookAuthor.setAuthor(author);
        bookAuthor.setContributionType(dto.getContributionType().getValue());
        bookAuthor.setIsDeleted(false);
        bookAuthor.setCreatedAt(LocalDateTime.now());
        bookAuthor.setUpdatedAt(LocalDateTime.now());
        BookAuthor saved = bookAuthorRepository.save(bookAuthor);

        return bookAuthorMapper.toResponseDTO(saved);
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "bookAuthors", key = "#id")
    public BookAuthorResponseDTO getBookAuthorById(Integer id) {
        if (id == null || id <= 0) {
            log.error("Error al obtener relación libro-autor: ID inválido: {}", id);
            throw new BadRequestException("El ID de la relación libro-autor debe ser un número positivo");
        }
        BookAuthor bookAuthor = bookAuthorRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("BookAuthor", "id", id));
        return bookAuthorMapper.toResponseDTO(bookAuthor);
    }

    /**
     * Actualiza una relación libro-autor validando todos los datos y reglas de negocio.
     */
    @Transactional
    @CachePut(value = "bookAuthors", key = "#id")
    public BookAuthorResponseDTO updateBookAuthor(Integer id, BookAuthorRequestDTO dto) {
        // Validar ID de la relación
        if (id == null || id <= 0) {
            log.error("Error al actualizar relación libro-autor: ID inválido: {}", id);
            throw new BadRequestException("El ID de la relación libro-autor debe ser un número positivo");
        }

        BookAuthor bookAuthor = bookAuthorRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("BookAuthor", "id", id));

        // Validar IDs requeridos y positivos
        if (dto.getBookId() == null || dto.getBookId() <= 0) {
            log.error("Error al crear relación libro-autor: ID de libro inválido: {}", dto.getBookId());
            throw new BadRequestException("El ID del libro debe ser un número positivo");
        }
        if (dto.getAuthorId() == null || dto.getAuthorId() <= 0) {
            log.error("Error al crear relación libro-autor: ID de autor inválido: {}", dto.getAuthorId());
            throw new BadRequestException("El ID del autor debe ser un número positivo");
        }

        // ContributionType obligatorio
        if (dto.getContributionType() == null || dto.getContributionType().getValue().isEmpty()) {
            log.error("Error al crear relación libro-autor: contributionType es obligatorio");
            throw new BadRequestException("El tipo de contribución es obligatorio");
        }

        // Validar existencia de libro y autor
        Book book = bookRepository.findByIdAndIsDeletedFalse(dto.getBookId())
                .orElseThrow(() -> new ResourceNotFoundException("Libro", "id", dto.getBookId()));
        Author author = authorRepository.findByIdAndIsDeletedFalse(dto.getAuthorId())
                .orElseThrow(() -> new ResourceNotFoundException("Autor", "id", dto.getAuthorId()));

        if (
            dto.getBookId() != bookAuthor.getBook().getId() ||
            dto.getAuthorId() != bookAuthor.getAuthor().getId() ||
            !dto.getContributionType().getValue().equals(bookAuthor.getContributionType())
        ) {
            // Validar que no exista ya una relación igual en la DB
            Optional<BookAuthor> duplicate = bookAuthorRepository.findByBookIdAndAuthorIdAndIsDeletedFalse(
                    dto.getBookId(), dto.getAuthorId()
            );
            if (duplicate.isPresent()) {
                log.error("Error al crear relación libro-autor: Ya existe una relación igual (bookId={}, authorId={}, contributionType={})", dto.getBookId(), dto.getAuthorId(), dto.getContributionType());
                throw new ConflictException("Ya existe una relación libro-autor con ese tipo de contribución");
            }
        }

        // Actualizar y guardar la relación
        bookAuthor.setBook(book);
        bookAuthor.setAuthor(author);
        bookAuthor.setContributionType(dto.getContributionType().getValue());
        bookAuthor.setUpdatedAt(LocalDateTime.now());

        BookAuthor updated = bookAuthorRepository.save(bookAuthor);

        return bookAuthorMapper.toResponseDTO(updated);
    }

    /**
     * Realiza un soft delete de la relación libro-autor validando el ID y el estado.
     */
    @Transactional
    @CacheEvict(value = "bookAuthors", key = "#id")
    public void deleteBookAuthor(Integer id) {
        // Validar ID positivo
        if (id == null || id <= 0) {
            log.error("Error al eliminar relación libro-autor: ID inválido: {}", id);
            throw new BadRequestException("El ID de la relación libro-autor debe ser un número positivo");
        }
        // Validar existencia y que no esté ya eliminada
        BookAuthor bookAuthor = bookAuthorRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("BookAuthor", "id", id));
        // Marcar como eliminado (soft delete)
        bookAuthor.setIsDeleted(true);
        bookAuthor.setUpdatedAt(LocalDateTime.now());
        bookAuthorRepository.save(bookAuthor);
    }

    /**
     * Busca relaciones libro-autor con filtros y paginación, validando los parámetros de entrada.
     */
    @Transactional(readOnly = true)
    public PaginatedBookAuthorResponseDTO getBookAuthorsByFilters(Integer bookId, Integer authorId, String contribution, Integer page) {
        // Validación de parámetros de paginación
        if (page == null || page < 0) {
            log.error("Número de página inválido: {}", page);
            throw new BadRequestException("El número de página debe ser 0 o mayor");
        }

        // Convertir valores especiales a null
        Integer normalizedBookId = NormalizeParameter.normalizeInteger(bookId);
        Integer normalizedAuthorId = NormalizeParameter.normalizeInteger(authorId);
        String normalizedContribution = NormalizeParameter.normalizeString(contribution);

        // Validación de la id de libro y autor si existen en la DB
        if (normalizedBookId != null && normalizedBookId < 0) {
            log.error("ID de libro inválido en filtro: {}", normalizedBookId);
            throw new BadRequestException("El ID de libro en el filtro debe ser 0 o un número positivo");
        } else if (normalizedBookId != null && !bookRepository.existsByIdAndIsDeletedFalse(normalizedBookId)) {
            log.error("Libro no encontrado para el filtro bookId={}", normalizedBookId);
            throw new ResourceNotFoundException("Libro", "id", normalizedBookId);
        }
        if (normalizedAuthorId != null && normalizedAuthorId < 0) {
            log.error("ID de autor inválido en filtro: {}", normalizedAuthorId);
            throw new BadRequestException("El ID de autor en el filtro debe ser 0 o un número positivo");
        } else if (normalizedAuthorId != null && !authorRepository.existsByIdAndIsDeletedFalse(normalizedAuthorId)) {
            log.error("Autor no encontrado para el filtro authorId={}", normalizedAuthorId);
            throw new ResourceNotFoundException("Autor", "id", normalizedAuthorId);
        }

        if (normalizedContribution != null && normalizedContribution.isEmpty()) {
           log.error("Tipo de contribución inválido en filtro: '{}'", contribution);
           throw new BadRequestException("El tipo de contribución es obligatorio");
        }

        Pageable pageable = PageRequest.of(page, paginationConfig.getPageSize());
        Page<BookAuthor> bookAuthorPage = bookAuthorRepository.findByFilters(normalizedBookId, normalizedAuthorId, normalizedContribution, pageable);
        List<BookAuthorResponseDTO> dtos = bookAuthorPage.getContent().stream()
                .map(bookAuthorMapper::toResponseDTO)
                .collect(Collectors.toList());
        PaginatedBookAuthorResponseDTO response = new PaginatedBookAuthorResponseDTO();
        response.setData(dtos);
        response.setPageSize(bookAuthorPage.getSize());
        response.setTotalItems((int) bookAuthorPage.getTotalElements());
        response.setCurrentPage(page);
        response.setTotalPages(bookAuthorPage.getTotalPages());
        response.setNext(bookAuthorPage.hasNext());
        response.setPrev(bookAuthorPage.hasPrevious());

        return response;
    }
}