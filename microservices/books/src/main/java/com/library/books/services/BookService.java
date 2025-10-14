package com.library.books.services;

import com.library.dtos.*;
import com.library.entities.Author;
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
import org.modelmapper.ModelMapper;
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
    private final ModelMapper modelMapper;
    private final PaginationConfig paginationConfig;

    /**
     * Crear un nuevo libro
     */
    public BookResponseDTO createBook(BookRequestDTO requestDTO) {
        Publication publication;
        List<Author> authors = new java.util.ArrayList<>();

        log.info("Creando nuevo libro: {}", requestDTO.getTitle());

        // El título es obligatorio
        if (requestDTO.getTitle() == null || requestDTO.getTitle().trim().isEmpty()) {
            throw new BadRequestException("El título del libro es obligatorio");
        }

        // La id de publicacion es obligatoria y debe existir en la DB
        if (requestDTO.getPublicationId() == null || requestDTO.getPublicationId() <= 0) {
            throw new BadRequestException("El ID de publicación es obligatorio y debe ser un número positivo");
        }

        // Verificar que la publicación exista usando consulta personalizada
        publication = bookRepository.findPublicationByIdAndIsDeletedFalse(requestDTO.getPublicationId())
            .orElseThrow(() -> new BadRequestException("La publicación con ID " + requestDTO.getPublicationId() + " no existe"));

        // Las ids de autores son obligatorias y deben existir en la DB
        if (requestDTO.getAuthorIds() == null || requestDTO.getAuthorIds().isEmpty()) {
            throw new BadRequestException("Debe asociar al menos un autor al libro");
        }
        for (Integer authorId : requestDTO.getAuthorIds()) {
            if (authorId == null || authorId <= 0) {
                throw new BadRequestException("El ID de autor debe ser un número positivo");
            }

            // Usar AuthorRepository para obtener el autor
            Author author = authorRepository.findByIdAndIsDeletedFalse(authorId)
                    .orElseThrow(() -> new BadRequestException("El autor con ID " + authorId + " no existe"));

            authors.add(author);
        }

        Book book = new Book();
        book.setPublication(publication);
        book.setTitle(requestDTO.getTitle());
        book.setDescription(requestDTO.getDescription());
        book.setBookUrl(requestDTO.getBookUrl());
        book.setCoverImg(requestDTO.getCoverImg());
        book.setAuthors(authors);
        book.setRatings(null);
        book.setCreatedAt(LocalDateTime.now());
        book.setUpdatedAt(LocalDateTime.now());
        book.setIsDeleted(false);

        Book savedBook = bookRepository.save(book);
        log.info("Libro creado exitosamente con ID: {}", savedBook.getId());
        
        return convertToResponseDTO(savedBook);
    }

    /**
     * Obtener todos los libros con paginación
     */
    @Transactional(readOnly = true)
    public PaginatedBookResponseDTO getAllBooks(Integer page) {
        log.info("Obteniendo todos los libros - página: {}, tamaño: {}", page, paginationConfig.getPageSize());

        // Validar parámetro page
        if (page == null || page < 0) {
            throw new BadRequestException("El número de página debe ser mayor o igual a 0");
        }

        Pageable pageable = PageRequest.of(page, paginationConfig.getPageSize());
        Page<Book> paginatedBook = bookRepository.findByIsDeletedFalse(pageable);
        
        return buildPaginatedResponse(paginatedBook);
    }

    /**
     * Buscar libros por título
     */
    @Transactional(readOnly = true)
    public PaginatedBookResponseDTO getBooksByTitle(String title, Integer page) {
        log.info("Buscando libros por título: {} - página: {}, tamaño: {}", title, page, paginationConfig.getPageSize());

        // Validar parámetro page
        if (page == null || page < 0) {
            throw new BadRequestException("El número de página debe ser mayor o igual a 0");
        }

        // Título obligatorio
        if (title == null || title.trim().isEmpty()) {
            throw new BadRequestException("El título de búsqueda no puede estar vacío");
        }

        Pageable pageable = PageRequest.of(page, paginationConfig.getPageSize());
        Page<Book> bookPage = bookRepository.findByTitleContainingIgnoreCaseAndIsDeletedFalse(title, pageable);

        return buildPaginatedResponse(bookPage);
    }

    /**
     * Buscar libros por publicación
     */
    @Transactional(readOnly = true)
    public PaginatedBookResponseDTO getBooksByPublicationId(Integer publicationId, Integer page) {
        log.info("Buscando libros por publicación ID: {} - página: {}, tamaño: {}", publicationId, page, paginationConfig.getPageSize());

        // Validar parámetro page
        if (page == null || page < 0) {
            throw new BadRequestException("El número de página debe ser mayor o igual a 0");
        }

        // Id de publicacion obligatoria y debe ser mayor a cero
        if (publicationId == null || publicationId <= 0) {
            throw new BadRequestException("El ID de publicación debe ser un número positivo");
        }

        Pageable pageable = PageRequest.of(page, paginationConfig.getPageSize());
        Page<Book> bookPage = bookRepository.findByPublicationIdAndIsDeletedFalse(publicationId, pageable);

        return buildPaginatedResponse(bookPage);
    }

    /**
     * Buscar libros por autor
     */
    @Transactional(readOnly = true)
    public PaginatedBookResponseDTO getBooksByAuthorId(Integer authorId, Integer page) {
        log.info("Buscando libros por autor ID: {} - página: {}, tamaño: {}", authorId, page, paginationConfig.getPageSize());

        // Validar parámetro page
        if (page == null || page < 0) {
            throw new BadRequestException("El número de página debe ser mayor o igual a 0");
        }

        // Id de autor obligatoria y debe existir en la DB
        if (authorId == null || authorId <= 0) {
            throw new BadRequestException("El ID de autor debe ser un número positivo");
        }

        // Verificar que el autor exista
        if (!authorRepository.existsById(authorId)) {
            throw new ResourceNotFoundException("Autor", "id", authorId);
        }

        Pageable pageable = PageRequest.of(page, paginationConfig.getPageSize());
        Page<Book> bookPage = bookRepository.findByAuthorIdAndIsDeletedFalse(authorId, pageable);

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
            throw new BadRequestException("El ID del libro debe ser un número positivo");
        }

        // Buscar libro en la DB
        Book book = bookRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Libro", "id", id));

        return convertToResponseDTO(book);
    }

    /**
     * Actualizar libro
     */
    public BookResponseDTO updateBook(Integer id, BookRequestDTO requestDTO) {
        Publication publication;
        List<Author> authors = new java.util.ArrayList<>();

        log.info("Actualizando libro con ID: {}", id);

        // Id de libro obligatorio y debe ser positivo
        if (id == null || id <= 0) {
            throw new BadRequestException("El ID del libro debe ser un número positivo");
        }

        // Buscar libro en la DB
        Book book = bookRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Libro", "id", id));

        // Id de publicación obligatorio y validar si existe la publicación en la DB
        if (requestDTO.getPublicationId() == null || requestDTO.getPublicationId() <= 0) {
            throw new BadRequestException("El ID de publicación debe ser un número positivo");
        }

        // Verificar que la publicación exista
        publication = bookRepository.findPublicationByIdAndIsDeletedFalse(requestDTO.getPublicationId())
            .orElseThrow(() -> new BadRequestException("La publicación con ID " + requestDTO.getPublicationId() + " no existe"));

        // Título obligatorio
        if (requestDTO.getTitle() == null || requestDTO.getTitle().trim().isEmpty()) {
            throw new BadRequestException("El título del libro es obligatorio");
        }

        // URL del libro obligatorio
        if (requestDTO.getBookUrl() == null || requestDTO.getBookUrl().trim().isEmpty()) {
            throw new BadRequestException("La url del libro es obligatorio");
        }

        // URL de la portada obligatorio
        if (requestDTO.getCoverImg() == null || requestDTO.getCoverImg().trim().isEmpty()) {
            throw new BadRequestException("La url de portada del libro es obligatorio");
        }

        // Id de autores obligatorios
        if (requestDTO.getAuthorIds() == null || requestDTO.getAuthorIds().isEmpty()) {
            throw new BadRequestException("Debe asociar al menos un autor al libro");
        }
        for (Integer authorId : requestDTO.getAuthorIds()) {
            // Validar que la id sea positiva
            if (authorId == null || authorId <= 0) {
                throw new BadRequestException("El ID de autor debe ser un número positivo");
            }

            // Usar AuthorRepository para obtener el autor
            Author author = authorRepository.findByIdAndIsDeletedFalse(authorId)
                    .orElseThrow(() -> new BadRequestException("El autor con ID " + authorId + " no existe"));

            authors.add(author);
        }

        book.setPublication(publication);
        book.setTitle(requestDTO.getTitle());
        book.setDescription(requestDTO.getDescription());
        book.setBookUrl(requestDTO.getBookUrl());
        book.setCoverImg(requestDTO.getCoverImg());
        book.setAuthors(authors);
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

        if (id == null || id <= 0) {
            throw new BadRequestException("El ID del libro debe ser un número positivo");
        }

        Book book = bookRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Libro", "id", id));

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
        List<Author> authors = book.getAuthors();

        BookResponseDTO dto = modelMapper.map(book, BookResponseDTO.class);
        log.debug("Conversión Book de entidad a ResponseDTO con ModelMapper: {}", dto);

        dto.setPublicationId(book.getPublication().getId());

        // Añadir las ids a la lista "authorsIds" del dto
        if (!authors.isEmpty()){
            for (Author author : authors){
                dto.getAuthorIds().add(author.getId());
            }
        } else {
            dto.setAuthorIds(null);
        }

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
            dto.setRatingAverage(null);
            dto.setRatingsCount(null);
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
