package com.library.books.services;

import com.library.books.utils.NormalizeParameter;
import com.library.dtos.*;
import com.library.entities.Book;
import com.library.entities.Rating;
import com.library.books.repositories.RatingRepository;
import com.library.books.repositories.BookRepository;
import com.library.books.exceptions.ResourceNotFoundException;
import com.library.books.exceptions.BadRequestException;
import com.library.books.exceptions.ConflictException;
import com.library.books.config.PaginationConfig;
import com.library.entities.UserProfile;
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
public class RatingService {

    private final RatingRepository ratingRepository;
    private final BookRepository bookRepository;
    private final ModelMapper modelMapper;
    private final PaginationConfig paginationConfig;

    /**
     * Crear una nueva valoración
     */
    public RatingResponseDTO createRating(RatingRequestDTO requestDTO) {
        Book book;
        UserProfile userProfile;

        log.info("Creando nueva valoración para el libro ID: {} por usuario ID: {}", 
            requestDTO.getBookId(), requestDTO.getUserProfileId());
        
        // Id de libro obligatorio y debe existir en la DB
        if (requestDTO.getBookId() == null || requestDTO.getBookId() <= 0) {
            throw new BadRequestException("El ID del libro debe ser un número positivo");
        }
        book = bookRepository.findByIdAndIsDeletedFalse(requestDTO.getBookId())
                .orElseThrow(() -> new ResourceNotFoundException("Libro", "id", requestDTO.getBookId()));

        // Id de usuario obligatorio y debe existir en la DB
        if (requestDTO.getUserProfileId() == null || requestDTO.getUserProfileId() <= 0) {
            throw new BadRequestException("El ID del usuario debe ser un número positivo");
        }
        userProfile = ratingRepository.findUserProfileByIdAndIsDeletedFalse(requestDTO.getUserProfileId())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", "id", requestDTO.getUserProfileId()));

        // Valoración obligatoria y debe estar entre 1 y 5
        if (requestDTO.getValoration() == null || requestDTO.getValoration() < 1 || requestDTO.getValoration() > 5) {
            throw new BadRequestException("La valoración debe estar entre 1 y 5");
        }

        // Verificar si el usuario ya calificó este libro
        if (ratingRepository.existsByUserProfileIdAndBookIdAndIsDeletedFalse( requestDTO.getUserProfileId(), requestDTO.getBookId())) {
            throw new ConflictException("El usuario ya ha calificado este libro");
        }

        Rating rating = new Rating();
        rating.setBook(book);
        rating.setUserProfile(userProfile);
        rating.setValoration(requestDTO.getValoration());
        rating.setComment(requestDTO.getComment());
        rating.setCreatedAt(LocalDateTime.now());
        rating.setUpdatedAt(LocalDateTime.now());
        rating.setIsDeleted(false);

        // Actualizar la lista de valoraciones del libro
        List<Rating> ratings = book.getRatings();
        ratings.add(rating);
        book.setRatings(ratings);

        Rating savedRating = ratingRepository.save(rating);
        log.info("Valoración creada exitosamente con ID: {}", savedRating.getId());

        bookRepository.save(book);
        log.info("Libro actualizado exitosamente con ID: {}", book.getId());

        return convertToResponseDTO(savedRating);
    }

    /**
     * Obtener valoraciones con paginación mediante múltiples filtros
     *
     */
    @Transactional(readOnly = true)
    public PaginatedRatingResponseDTO getRatingsByFilters(Integer bookId, Integer userProfileId, Integer minValoration, Integer maxValoration, Integer page) {
        log.info("Buscando valoraciones por filtros - bookId: {}, userProfileId: {}, min: {}, max: {}, página: {}, tamaño: {}",
                bookId, userProfileId, minValoration, maxValoration, page, paginationConfig.getPageSize());

        // Validar parámetro page
        if (page == null || page < 0) {
            throw new BadRequestException("El número de página debe ser mayor o igual a 0");
        }

        // Normalizar valores especiales
        Integer normalizedBookId = NormalizeParameter.normalizeInteger(bookId);
        Integer normalizedUserProfileId = NormalizeParameter.normalizeInteger(userProfileId);
        Integer normalizedMinValoration = NormalizeParameter.normalizeInteger(minValoration);
        Integer normalizedMaxValoration = NormalizeParameter.normalizeInteger(maxValoration);

        // Validar rango de valoraciones solo si ambas están presentes
        if (normalizedMinValoration != null && normalizedMaxValoration != null) {
            if (normalizedMinValoration > normalizedMaxValoration) {
                throw new BadRequestException("La valoración mínima no puede ser mayor que la valoración máxima");
            }
        }

        // Validar que las valoraciones estén en el rango válido (1-5)
        if (normalizedMinValoration != null && (normalizedMinValoration < 1 || normalizedMinValoration > 5)) {
            throw new BadRequestException("La valoración mínima debe estar entre 1 y 5");
        }
        if (normalizedMaxValoration != null && (normalizedMaxValoration < 1 || normalizedMaxValoration > 5)) {
            throw new BadRequestException("La valoración máxima debe estar entre 1 y 5");
        }

        // Verificar si la id del libro es positivo y si existe en la DB
        if (normalizedBookId != null && normalizedBookId < 0) {
            throw new BadRequestException("El ID del libro debe ser un número positivo");
        } else if (normalizedBookId != null && !bookRepository.existsById(normalizedBookId)) {
            throw new ResourceNotFoundException("Libro", "id", normalizedBookId);
        }

        // Verificar si la id del perfil es positivo y si existe en la DB
        if (normalizedUserProfileId != null && normalizedUserProfileId < 0) {
            throw new BadRequestException("El ID del perfil de usuario debe ser un número positivo");
        } else if (normalizedUserProfileId != null && !ratingRepository.existsUserProfileByIdAndIsDeletedFalse(normalizedUserProfileId)) {
            throw new ResourceNotFoundException("Usuario", "id", normalizedUserProfileId);
        }

        Pageable pageable = PageRequest.of(page, paginationConfig.getPageSize());
        Page<Rating> ratingPage = ratingRepository.findByFilters(
                normalizedBookId,
                normalizedUserProfileId,
                normalizedMinValoration,
                normalizedMaxValoration,
                pageable
        );

        log.info("Búsqueda por filtros completada. Total de valoraciones encontradas: {}", ratingPage.getTotalElements());

        return buildPaginatedResponse(ratingPage);
    }

    /**
     * Obtener valoración por ID
     */
    @Transactional(readOnly = true)
    public RatingResponseDTO getRatingById(Integer id) {
        log.info("Obteniendo valoración con ID: {}", id);
        
        // Id de valoración obligatorio y debe ser positivo
        if (id == null || id <= 0) {
            throw new BadRequestException("El ID de la valoración debe ser un número positivo");
        }

        // Buscar valoración en la DB
        Rating rating = ratingRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Valoración", "id", id));

        return convertToResponseDTO(rating);
    }

    /**
     * Actualizar valoración
     */
    public RatingResponseDTO updateRating(Integer id, RatingRequestDTO requestDTO) {
        Book book;
        UserProfile userProfile;

        log.info("Actualizando valoración con ID: {}", id);
        
        // Id de valoración obligatorio y debe existir en la DB
        if (id == null || id <= 0) {
            throw new BadRequestException("El ID de la valoración debe ser un número positivo");
        }
        Rating rating = ratingRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Valoración", "id", id));

        // Id de libro obligatorio y debe existir en la DB
        if (requestDTO.getBookId() == null || requestDTO.getBookId() <= 0) {
            throw new BadRequestException("El ID del libro debe ser un número positivo");
        }
        book = bookRepository.findByIdAndIsDeletedFalse(requestDTO.getBookId())
                .orElseThrow(() -> new ResourceNotFoundException("Libro", "id", requestDTO.getBookId()));

        // Id de usuario obligatorio y debe existir en la DB
        if (requestDTO.getUserProfileId() == null || requestDTO.getUserProfileId() <= 0) {
            throw new BadRequestException("El ID del usuario debe ser un número positivo");
        }
        userProfile = ratingRepository.findUserProfileByIdAndIsDeletedFalse(requestDTO.getUserProfileId())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", "id", requestDTO.getUserProfileId()));

        // Valoración obligatoria y debe estar entre 1 y 5
        if (requestDTO.getValoration() == null || requestDTO.getValoration() < 1 || requestDTO.getValoration() > 5) {
            throw new BadRequestException("La valoración debe estar entre 1 y 5");
        }

        // Verificar que el usuario no actualice otra calificación existente
        if (ratingRepository.existsByUserProfileIdAndBookIdAndIsDeletedFalse(requestDTO.getUserProfileId(), requestDTO.getBookId())) {
            if (!rating.getUserProfile().getId().equals(requestDTO.getUserProfileId())){
                throw new ConflictException("El usuario ya ha calificado este libro");
            } else if (!rating.getBook().getId().equals(requestDTO.getBookId())){
                throw new ConflictException("El usuario ya ha calificado este libro");
            }
        }

        rating.setUserProfile(userProfile);
        rating.setBook(book);
        rating.setValoration(requestDTO.getValoration());
        rating.setComment(requestDTO.getComment());
        rating.setUpdatedAt(LocalDateTime.now());

        // Actualizar la lista de valoraciones del libro
        List<Rating> ratings = book.getRatings();
        ratings.add(rating);
        book.setRatings(ratings);

        Rating updatedRating = ratingRepository.save(rating);
        log.info("Valoración actualizada exitosamente: {}", updatedRating.getId());

        bookRepository.save(book);
        log.info("Actualizado con éxito el libro con ID: {}", book.getId());

        return convertToResponseDTO(updatedRating);
    }

    /**
     * Eliminar valoración (soft delete)
     */
    public void deleteRating(Integer id) {
        log.info("Eliminando valoración con ID: {}", id);
        
        // Id de valoración obligatorio y debe ser positivo
        if (id == null || id <= 0) {
            throw new BadRequestException("El ID de la valoración debe ser un número positivo");
        }

        // Buscar valoración en la DB
        Rating rating = ratingRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Valoración", "id", id));

        rating.setIsDeleted(true);
        rating.setUpdatedAt(LocalDateTime.now());
        ratingRepository.save(rating);
        
        log.info("Valoración eliminada exitosamente (soft delete): {}", id);
    }

    /**
     * Convertir entidad Rating a RatingResponseDTO
     */
    private RatingResponseDTO convertToResponseDTO(Rating rating) {
        RatingResponseDTO dto = modelMapper.map(rating, RatingResponseDTO.class);
        log.debug("Conversión Rating de entidad a ResponseDTO con ModelMapper: {}", dto);

        dto.setUserProfileId(rating.getUserProfile().getId());
        dto.setBookId(rating.getBook().getId());

        return dto;
    }

    /**
     * Construir respuesta paginada
     */
    private PaginatedRatingResponseDTO buildPaginatedResponse(Page<Rating> ratingPage) {
        PaginatedRatingResponseDTO response = new PaginatedRatingResponseDTO();
        
        log.debug("Construyendo respuesta paginada para valoraciones");

        List<RatingResponseDTO> ratingDTOs = ratingPage.getContent().stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
        
        response.setData(ratingDTOs);
        response.setPageSize(ratingPage.getSize());
        response.setTotalItems((int) ratingPage.getTotalElements());
        response.setCurrentPage(ratingPage.getNumber());
        response.setTotalPages(ratingPage.getTotalPages());
        response.setNext(ratingPage.hasNext());
        response.setPrev(ratingPage.hasPrevious());
        
        return response;
    }
}
