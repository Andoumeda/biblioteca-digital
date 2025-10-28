package com.library.books.services;

import com.library.books.mappers.AuthorMapper;
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
    private final AuthorMapper authorMapper;
    private final PaginationConfig paginationConfig;

    /**
     * Crear un nuevo autor
     */
    public AuthorResponseDTO createAuthor(AuthorRequestDTO requestDTO) {
        log.info("Creando nuevo autor: {}", requestDTO.getFullName());
        
        // El nombre completo es obligatorio
        if (requestDTO.getFullName() == null || requestDTO.getFullName().trim().isEmpty()) {
            log.error("Error al crear autor: El nombre completo es obligatorio");
            throw new BadRequestException("El nombre completo del autor es obligatorio");
        }

        // La fecha de nacimiento es obligatoria y no puede ser futura
        if (requestDTO.getBirthDate() == null || requestDTO.getBirthDate().isAfter(LocalDate.now())) {
            log.error("Error al crear autor: La fecha de nacimiento es inválida o futura: {}", requestDTO.getBirthDate());
            throw new BadRequestException("La fecha de nacimiento del autor es obligatoria y no puede ser futura");
        }

        // La nacionalidad es obligatoria
        if (requestDTO.getNationality() == null || requestDTO.getNationality().trim().isEmpty()) {
            log.error("Error al crear autor: La nacionalidad es obligatoria");
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
            log.error("Error al buscar autores: Número de página inválido: {}", page);
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
            log.error("Error al buscar autores: ID de libro inválido: {}", normalizedBookId);
            throw new BadRequestException("El ID del libro debe ser un número positivo");
        } else if (normalizedBookId != null && !bookRepository.existsById(normalizedBookId)) {
            log.error("Error al buscar autores: Libro no encontrado con ID: {}", normalizedBookId);
            throw new ResourceNotFoundException("Libro", "id", normalizedBookId);
        }

        // Validar rango de fechas solo si ambas están presentes
        if (normalizedMinDate != null && normalizedMaxDate != null && normalizedMinDate.isAfter(normalizedMaxDate)) {
            log.error("Error al buscar autores: Rango de fechas inválido - minDate: {}, maxDate: {}", normalizedMinDate, normalizedMaxDate);
            throw new BadRequestException("La fecha mínima no puede ser posterior a la fecha máxima");
        }

        // Validar que las fechas no sean futuras
        if (normalizedMinDate != null && normalizedMinDate.isAfter(LocalDate.now())) {
            log.error("Error al buscar autores: La fecha mínima es futura: {}", normalizedMinDate);
            throw new BadRequestException("La fecha mínima no puede ser futura");
        }

        if (normalizedMaxDate != null && normalizedMaxDate.isAfter(LocalDate.now())) {
            log.error("Error al buscar autores: La fecha máxima es futura: {}", normalizedMaxDate);
            throw new BadRequestException("La fecha máxima no puede ser futura");
        }

        // Nombre completo obligatorio
        if (normalizedFullName != null && normalizedFullName.trim().isEmpty()) {
            log.error("Error al buscar autores: El nombre de búsqueda está vacío");
            throw new BadRequestException("El nombre de búsqueda no puede estar vacío");
        }

        // Nacionalidad obligatoria
        if (normalizedNationality != null && normalizedNationality.trim().isEmpty()) {
            log.error("Error al buscar autores: La nacionalidad de búsqueda está vacía");
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
            log.error("Error al obtener autor: ID inválido: {}", id);
            throw new BadRequestException("El ID del autor debe ser un número mayor a 0");
        }

        // Buscar autor en la DB
        Author author = authorRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> {
                    log.error("Error al obtener autor: Autor no encontrado con ID: {}", id);
                    return new ResourceNotFoundException("Autor", "id", id);
                });

        return convertToResponseDTO(author);
    }

    /**
     * Actualizar autor
     */
    public AuthorResponseDTO updateAuthor(Integer id, AuthorRequestDTO requestDTO) {
        log.info("Actualizando autor con ID: {}", id);
        
        // Id de autor obligatorio y debe ser positivo
        if (id == null || id <= 0) {
            log.error("Error al actualizar autor: ID inválido: {}", id);
            throw new BadRequestException("El ID del autor debe ser un número positivo");
        }

        // Buscar autor en la DB
        Author author = authorRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> {
                    log.error("Error al actualizar autor: Autor no encontrado con ID: {}", id);
                    return new ResourceNotFoundException("Autor", "id", id);
                });

        // Nombre completo obligatorio
        if (requestDTO.getFullName() == null || requestDTO.getFullName().trim().isEmpty()) {
            log.error("Error al actualizar autor: El nombre completo es obligatorio");
            throw new BadRequestException("El nombre completo del autor es obligatorio");
        }

        // La fecha de nacimiento es obligatoria y no puede ser futura
        if (requestDTO.getBirthDate() == null || requestDTO.getBirthDate().isAfter(LocalDate.now())) {
            log.error("Error al actualizar autor: La fecha de nacimiento es inválida o futura: {}", requestDTO.getBirthDate());
            throw new BadRequestException("La fecha de nacimiento del autor debe ser obligatoria y no puede ser futura");
        }

        // La nacionalidad es obligatoria
        if (requestDTO.getNationality() == null || requestDTO.getNationality().trim().isEmpty()) {
            log.error("Error al actualizar autor: La nacionalidad es obligatoria");
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
            log.error("Error al eliminar autor: ID inválido: {}", id);
            throw new BadRequestException("El ID del autor debe ser un número positivo");
        }

        // Buscar autor en la DB
        Author author = authorRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> {
                    log.error("Error al eliminar autor: Autor no encontrado con ID: {}", id);
                    return new ResourceNotFoundException("Autor", "id", id);
                });

        author.setIsDeleted(true);
        author.setUpdatedAt(LocalDateTime.now());
        authorRepository.save(author);
        
        log.info("Autor eliminado exitosamente (soft delete): {}", id);
    }

    /**
     * Convertir entidad Author a AuthorResponseDTO
     */
    private AuthorResponseDTO convertToResponseDTO(Author author) {
        AuthorResponseDTO dto = authorMapper.toResponseDTO(author);
        log.debug("Conversión de Author de entidad a ResponseDTO con MapStruct: {}", dto);

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
