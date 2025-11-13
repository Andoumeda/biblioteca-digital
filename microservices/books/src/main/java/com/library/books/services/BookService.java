package com.library.books.services;

import com.library.books.mappers.BookMapper;
import com.library.books.utils.NormalizeParameter;
import com.library.dtos.*;
import com.library.entities.Book;
import com.library.books.repositories.BookRepository;
import com.library.books.repositories.AuthorRepository;
import com.library.books.exceptions.ResourceNotFoundException;
import com.library.books.exceptions.BadRequestException;
import com.library.books.config.PaginationConfig;
import com.library.entities.Publication;
import com.library.entities.Rating;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class BookService {

    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;
    private final BookMapper bookMapper;
    private final PaginationConfig paginationConfig;

    /**
     * Crear un nuevo libro
     */
    public BookResponseDTO createBook(BookRequestDTO requestDTO) {
        Publication publication;

        log.info("Creando nuevo libro: {}", requestDTO.getTitle());

        // El título es obligatorio
        if (requestDTO.getTitle() == null || requestDTO.getTitle().trim().isEmpty()) {
            log.error("Error al crear libro: El título es obligatorio");
            throw new BadRequestException("El título del libro es obligatorio");
        }

        // La URL del libro es obligatoria
        if (requestDTO.getBookUrl() == null || requestDTO.getBookUrl().trim().isEmpty()) {
            log.error("Error al crear libro: La URL del libro es obligatoria");
            throw new BadRequestException("La url del libro es obligatoria");
        }

        // La URL de la portada es obligatoria
        if (requestDTO.getCoverImg() == null || requestDTO.getCoverImg().trim().isEmpty()) {
            log.error("Error al crear libro: La URL de la portada es obligatoria");
            throw new BadRequestException("La url de portada del libro es obligatoria");
        }

        // La id de publicacion es obligatoria y debe existir en la DB
        if (requestDTO.getPublicationId() == null || requestDTO.getPublicationId() <= 0) {
            log.error("Error al crear libro: El ID de publicación es inválido: {}", requestDTO.getPublicationId());
            throw new BadRequestException("El ID de publicación es obligatorio y debe ser un número positivo");
        }

        // Verificar que la publicación exista usando consulta personalizada
        publication = bookRepository.findPublicationByIdAndIsDeletedFalse(requestDTO.getPublicationId())
            .orElseThrow(() -> {
                log.error("Error al crear libro: Publicación no encontrada con ID: {}", requestDTO.getPublicationId());
                return new ResourceNotFoundException("Publicación", "id", requestDTO.getPublicationId());
            });

        Book book = new Book();
        book.setPublication(publication);
        book.setTitle(requestDTO.getTitle());
        book.setDescription(requestDTO.getDescription());
        book.setBookUrl(requestDTO.getBookUrl());
        book.setCoverImg(requestDTO.getCoverImg());
        book.setRatings(null);
        book.setCreatedAt(LocalDateTime.now());
        book.setUpdatedAt(LocalDateTime.now());
        book.setIsDeleted(false);

        Book savedBook = bookRepository.save(book);
        log.info("Libro creado exitosamente con ID: {}", savedBook.getId());
        
        return convertToResponseDTO(savedBook);
    }

    /**
     * Obtener libros con paginación mediante múltiples filtros
     */
    @Transactional(readOnly = true)
    public PaginatedBookResponseDTO getBooksByFilters(String title, Integer publicationId, Integer authorId, Integer page) {
        log.info("Buscando libros por filtros - título: {}, publicaciónId: {}, autorId: {}, página: {}, tamaño: {}",
                title, publicationId, authorId, page, paginationConfig.getPageSize());

        // Validar parámetro page
        if (page == null || page < 0) {
            log.error("Error al buscar libros: Número de página inválido: {}", page);
            throw new BadRequestException("El número de página debe ser mayor o igual a 0");
        }

        // Convertir valores especiales a null
        String normalizedTitle = NormalizeParameter.normalizeString(title);
        Integer normalizedPublicationId = NormalizeParameter.normalizeInteger(publicationId);
        Integer normalizedAuthorId = NormalizeParameter.normalizeInteger(authorId);

        // Validar si el ID de publicación es válido y si existe en la DB
        if (normalizedPublicationId != null && normalizedPublicationId < 0) {
            log.error("Error al buscar libros: ID de publicación inválido: {}", normalizedPublicationId);
            throw new BadRequestException("El ID de publicación debe ser un número positivo");
        } else if (normalizedPublicationId != null && !bookRepository.existsPublicationByIdAndIsDeletedFalse(normalizedPublicationId)) {
            log.error("Error al buscar libros: Publicación no encontrada con ID: {}", normalizedPublicationId);
            throw new ResourceNotFoundException("Publicación", "id", normalizedPublicationId);
        }

        // Validar si el ID de autor es válido y si existe en la DB
        if (normalizedAuthorId != null && normalizedAuthorId < 0) {
            log.error("Error al buscar libros: ID de autor inválido: {}", normalizedAuthorId);
            throw new BadRequestException("El ID de autor debe ser un número positivo");
        } else if (normalizedAuthorId != null && !authorRepository.existsById(normalizedAuthorId)) {
            log.error("Error al buscar libros: Autor no encontrado con ID: {}", normalizedAuthorId);
            throw new ResourceNotFoundException("Autor", "id", normalizedAuthorId);
        }

        Pageable pageable = PageRequest.of(page, paginationConfig.getPageSize());
        Page<Book> bookPage = bookRepository.findByFilters(
                normalizedTitle,
                normalizedPublicationId,
                normalizedAuthorId,
                pageable
        );

        log.info("Búsqueda por filtros completada. Total de libros encontrados: {}", bookPage.getTotalElements());

        return buildPaginatedResponse(bookPage);
    }

    /**
     * Obtener libro por ID
     */
    @Transactional(readOnly = true)
    public BookResponseDTO getBookById(Integer id) {
        log.info("Obteniendo libro con ID: {}", id);

        // Id de libro obligatorio y debe ser positivo
        if (id == null || id <= 0) {
            log.error("Error al obtener libro: ID inválido: {}", id);
            throw new BadRequestException("El ID del libro debe ser un número positivo");
        }

        // Buscar libro en la DB
        Book book = bookRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> {
                    log.error("Error al obtener libro: Libro no encontrado con ID: {}", id);
                    return new ResourceNotFoundException("Libro", "id", id);
                });

        return convertToResponseDTO(book);
    }

    /**
     * Actualizar libro
     */
    public BookResponseDTO updateBook(Integer id, BookRequestDTO requestDTO) {
        Publication publication;

        log.info("Actualizando libro con ID: {}", id);

        // Id de libro obligatorio y debe ser positivo
        if (id == null || id <= 0) {
            log.error("Error al actualizar libro: ID inválido: {}", id);
            throw new BadRequestException("El ID del libro debe ser un número positivo");
        }

        // Buscar libro en la DB
        Book book = bookRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> {
                    log.error("Error al actualizar libro: Libro no encontrado con ID: {}", id);
                    return new ResourceNotFoundException("Libro", "id", id);
                });

        // Id de publicación obligatorio y validar si existe la publicación en la DB
        if (requestDTO.getPublicationId() == null || requestDTO.getPublicationId() <= 0) {
            log.error("Error al actualizar libro: ID de publicación inválido: {}", requestDTO.getPublicationId());
            throw new BadRequestException("El ID de publicación debe ser un número positivo");
        }

        // Verificar que la publicación exista
        publication = bookRepository.findPublicationByIdAndIsDeletedFalse(requestDTO.getPublicationId())
            .orElseThrow(() -> {
                log.error("Error al actualizar libro: Publicación no encontrada con ID: {}", requestDTO.getPublicationId());
                return new BadRequestException("La publicación con ID " + requestDTO.getPublicationId() + " no existe");
            });

        // Título obligatorio
        if (requestDTO.getTitle() == null || requestDTO.getTitle().trim().isEmpty()) {
            log.error("Error al actualizar libro: El título es obligatorio");
            throw new BadRequestException("El título del libro es obligatorio");
        }

        // URL del libro obligatorio
        if (requestDTO.getBookUrl() == null || requestDTO.getBookUrl().trim().isEmpty()) {
            log.error("Error al actualizar libro: La URL del libro es obligatoria");
            throw new BadRequestException("La url del libro es obligatorio");
        }

        // URL de la portada obligatorio
        if (requestDTO.getCoverImg() == null || requestDTO.getCoverImg().trim().isEmpty()) {
            log.error("Error al actualizar libro: La URL de la portada es obligatoria");
            throw new BadRequestException("La url de portada del libro es obligatorio");
        }

        book.setPublication(publication);
        book.setTitle(requestDTO.getTitle());
        book.setDescription(requestDTO.getDescription());
        book.setBookUrl(requestDTO.getBookUrl());
        book.setCoverImg(requestDTO.getCoverImg());
        book.setUpdatedAt(LocalDateTime.now());

        Book updatedBook = bookRepository.save(book);
        log.info("Libro actualizado exitosamente: {}", updatedBook.getId());
        
        return convertToResponseDTO(updatedBook);
    }

    /**
     * Eliminar libro (soft delete)
     */
    public void deleteBook(Integer id) {
        log.info("Eliminando libro con ID: {}", id);

        // Id de libro obligatorio y debe ser positivo
        if (id == null || id <= 0) {
            log.error("Error al eliminar libro: ID inválido: {}", id);
            throw new BadRequestException("El ID del libro debe ser un número positivo");
        }

        // Buscar libro en la DB
        Book book = bookRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> {
                    log.error("Error al eliminar libro: Libro no encontrado con ID: {}", id);
                    return new ResourceNotFoundException("Libro", "id", id);
                });

        book.setIsDeleted(true);
        book.setUpdatedAt(LocalDateTime.now());
        bookRepository.save(book);
        
        log.info("Libro eliminado exitosamente (soft delete): {}", id);
    }

    /**
     * Convertir entidad Book a BookResponseDTO
     */
    private BookResponseDTO convertToResponseDTO(Book book) {
        List<Rating> ratings = book.getRatings();

        BookResponseDTO dto = bookMapper.toResponseDTO(book);
        log.debug("Conversión de Book de entidad a ResponseDTO con MapStruct: {}", dto);

        dto.setPublicationId(book.getPublication().getId());

        if (ratings != null && !ratings.isEmpty()) {
            // Definir el rating promedio
            float averageRating = (float) ratings.stream()
                    .filter(r -> !r.getIsDeleted())
                    .mapToInt(Rating::getValoration)
                    .average()
                    .orElse(0);

            dto.setRatingAverage(averageRating);

            // Definir el número de ratings
            Integer ratingsCount = Math.toIntExact(ratings.stream()
                    .filter(r -> !r.getIsDeleted())
                    .count());
            dto.setRatingsCount(ratingsCount);
        } else {
            dto.setRatingAverage(0f);
            dto.setRatingsCount(0);
        }

        return dto;
    }

    /**
     * Construir respuesta paginada
     */
    private PaginatedBookResponseDTO buildPaginatedResponse(Page<Book> bookPage) {
        PaginatedBookResponseDTO response = new PaginatedBookResponseDTO();

        log.debug("Construyendo respuesta paginada para libros");

        List<BookResponseDTO> bookDTOs = bookPage.getContent().stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
        
        response.setData(bookDTOs);
        response.setPageSize(bookPage.getSize());
        response.setTotalItems((int) bookPage.getTotalElements());
        response.setCurrentPage(bookPage.getNumber());
        response.setTotalPages(bookPage.getTotalPages());
        response.setNext(bookPage.hasNext());
        response.setPrev(bookPage.hasPrevious());
        
        return response;
    }
}
