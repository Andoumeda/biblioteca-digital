package com.library.books.services;

import com.library.books.utils.NormalizeParameter;
import com.library.dtos.*;
import com.library.entities.Author;
import com.library.books.repositories.AuthorRepository;
import com.library.books.repositories.BookRepository;
import com.library.books.exceptions.ResourceNotFoundException;
import com.library.books.exceptions.BadRequestException;
import com.library.books.config.PaginationConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AuthorService {

    private final AuthorRepository authorRepository;
    private final BookRepository bookRepository;
    private final ModelMapper modelMapper;
    private final PaginationConfig paginationConfig;

    /**
     * Crear un nuevo autor
     */
    public AuthorResponseDTO createAuthor(AuthorRequestDTO requestDTO) {
        log.info("Creando nuevo autor: {}", requestDTO.getFullName());
        
        // El nombre completo es obligatorio
        if (requestDTO.getFullName() == null || requestDTO.getFullName().trim().isEmpty()) {
            throw new BadRequestException("El nombre completo del autor es obligatorio");
        }

        // La fecha de nacimiento es obligatoria y no puede ser futura
        if (requestDTO.getBirthDate() == null || requestDTO.getBirthDate().isAfter(LocalDate.now())) {
            throw new BadRequestException("La fecha de nacimiento del autor es obligatoria y no puede ser futura");
        }

        // La nacionalidad es obligatoria
        if (requestDTO.getNationality() == null || requestDTO.getNationality().trim().isEmpty()) {
            throw new BadRequestException("La nacionalidad del autor es obligatoria");
        }

        Author author = new Author();
        author.setFullName(requestDTO.getFullName());
        author.setBio(requestDTO.getBio());
        author.setBirthDate(requestDTO.getBirthDate());
        author.setNationality(requestDTO.getNationality());
        author.setCreatedAt(LocalDateTime.now());
        author.setUpdatedAt(LocalDateTime.now());
        author.setIsDeleted(false);
        
        Author savedAuthor = authorRepository.save(author);
        log.info("Autor creado exitosamente con ID: {}", savedAuthor.getId());
        
        return convertToResponseDTO(savedAuthor);
    }

    /**
     * Obtener autores con paginación mediante múltiples filtros
     */
    @Transactional(readOnly = true)
    public PaginatedAuthorResponseDTO getAuthorsByFilters(Integer bookId, String fullName, LocalDate minDate, LocalDate maxDate, String nationality, Integer page) {
        log.info("Buscando autores por filtros - libroId: {}, nombre: {}, minDate: {}, maxDate: {}, nacionalidad: {}, página: {}, tamaño: {}",
                bookId, fullName, minDate, maxDate, nationality, page, paginationConfig.getPageSize());

        // Validar parámetro page
        if (page == null || page < 0) {
            throw new BadRequestException("El número de página debe ser mayor o igual a 0");
        }

        // Convertir valores especiales a null
        Integer normalizedBookId = NormalizeParameter.normalizeInteger(bookId);
        String normalizedFullName = NormalizeParameter.normalizeString(fullName);
        String normalizedNationality = NormalizeParameter.normalizeString(nationality);
        LocalDate normalizedMinDate = NormalizeParameter.normalizeDate(minDate);
        LocalDate normalizedMaxDate = NormalizeParameter.normalizeDate(maxDate);

        // Validar si el libro existe
        if (normalizedBookId != null && normalizedBookId < 0) {
            throw new BadRequestException("El ID del libro debe ser un número positivo");
        } else if (normalizedBookId != null && !bookRepository.existsById(normalizedBookId)) {
            throw new ResourceNotFoundException("Libro", "id", normalizedBookId);
        }

        // Validar rango de fechas solo si ambas están presentes
        if (normalizedMinDate != null && normalizedMaxDate != null && normalizedMinDate.isAfter(normalizedMaxDate)) {
            throw new BadRequestException("La fecha mínima no puede ser posterior a la fecha máxima");
        }

        // Validar que las fechas no sean futuras
        if (normalizedMinDate != null && normalizedMinDate.isAfter(LocalDate.now())) {
            throw new BadRequestException("La fecha mínima no puede ser futura");
        }

        if (normalizedMaxDate != null && normalizedMaxDate.isAfter(LocalDate.now())) {
            throw new BadRequestException("La fecha máxima no puede ser futura");
        }

        // Nombre completo obligatorio
        if (fullName == null || fullName.trim().isEmpty()) {
            throw new BadRequestException("El nombre de búsqueda no puede estar vacío");
        }

        // Nacionalidad obligatoria
        if (nationality == null || nationality.trim().isEmpty()) {
            throw new BadRequestException("La nacionalidad de búsqueda no puede estar vacía");
        }

        Pageable pageable = PageRequest.of(page, paginationConfig.getPageSize());
        Page<Author> authorPage = authorRepository.findByFilters(
                normalizedBookId,
                normalizedFullName,
                normalizedMinDate,
                normalizedMaxDate,
                normalizedNationality,
                pageable
        );

        log.info("Búsqueda por filtros completada. Total de autores encontrados: {}", authorPage.getTotalElements());

        return buildPaginatedResponse(authorPage);
    }

    /**
     * Obtener autor por ID
     */
    @Transactional(readOnly = true)
    public AuthorResponseDTO getAuthorById(Integer id) {
        log.info("Obteniendo autor con ID: {}", id);

        // Id de autor obligatorio y debe ser positivo
        if (id == null || id <= 0) {
            throw new BadRequestException("El ID del autor debe ser un número mayor a 0");
        }

        // Buscar autor en la DB
        Author author = authorRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Autor", "id", id));

        return convertToResponseDTO(author);
    }

    /**
     * Actualizar autor
     */
    public AuthorResponseDTO updateAuthor(Integer id, AuthorRequestDTO requestDTO) {
        log.info("Actualizando autor con ID: {}", id);
        
        // Id de autor obligatorio y debe ser positivo
        if (id == null || id <= 0) {
            throw new BadRequestException("El ID del autor debe ser un número positivo");
        }

        // Buscar autor en la DB
        Author author = authorRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Autor", "id", id));

        // Nombre completo obligatorio
        if (requestDTO.getFullName() == null || requestDTO.getFullName().trim().isEmpty()) {
            throw new BadRequestException("El nombre completo del autor es obligatorio");
        }

        // La fecha de nacimiento es obligatoria y no puede ser futura
        if (requestDTO.getBirthDate() == null || requestDTO.getBirthDate().isAfter(LocalDate.now())) {
            throw new BadRequestException("La fecha de nacimiento del autor debe ser obligatoria y no puede ser futura");
        }

        // La nacionalidad es obligatoria
        if (requestDTO.getNationality() == null || requestDTO.getNationality().trim().isEmpty()) {
            throw new BadRequestException("La nacionalidad del autor es obligatoria");
        }

        author.setFullName(requestDTO.getFullName());
        author.setBio(requestDTO.getBio());
        author.setBirthDate(requestDTO.getBirthDate());
        author.setNationality(requestDTO.getNationality());
        author.setUpdatedAt(LocalDateTime.now());
        
        Author updatedAuthor = authorRepository.save(author);
        log.info("Autor actualizado exitosamente: {}", updatedAuthor.getId());
        
        return convertToResponseDTO(updatedAuthor);
    }

    /**
     * Eliminar autor (soft delete)
     */
    public void deleteAuthor(Integer id) {
        log.info("Eliminando autor con ID: {}", id);
        
        // Id de autor obligatorio y debe ser positivo
        if (id == null || id <= 0) {
            throw new BadRequestException("El ID del autor debe ser un número positivo");
        }

        // Buscar autor en la DB
        Author author = authorRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Autor", "id", id));

        author.setIsDeleted(true);
        author.setUpdatedAt(LocalDateTime.now());
        authorRepository.save(author);
        
        log.info("Autor eliminado exitosamente (soft delete): {}", id);
    }

    /**
     * Convertir entidad Author a AuthorResponseDTO
     */
    private AuthorResponseDTO convertToResponseDTO(Author author) {
        AuthorResponseDTO dto = modelMapper.map(author, AuthorResponseDTO.class);
        log.debug("Conversión Author de entidad a ResponseDTO con ModelMapper: {}", dto);

        return dto;
    }

    /**
     * Construir respuesta paginada
     */
    private PaginatedAuthorResponseDTO buildPaginatedResponse(Page<Author> authorPage) {
        PaginatedAuthorResponseDTO response = new PaginatedAuthorResponseDTO();
        
        log.debug("Construyendo respuesta paginada para autores");

        List<AuthorResponseDTO> authorDTOs = authorPage.getContent().stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
        
        response.setData(authorDTOs);
        response.setPageSize(authorPage.getSize());
        response.setTotalItems((int) authorPage.getTotalElements());
        response.setCurrentPage(authorPage.getNumber());
        response.setTotalPages(authorPage.getTotalPages());
        response.setNext(authorPage.hasNext());
        response.setPrev(authorPage.hasPrevious());
        
        return response;
    }
}
