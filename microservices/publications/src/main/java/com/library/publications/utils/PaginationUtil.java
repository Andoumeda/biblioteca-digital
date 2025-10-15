package com.library.publications.utils;

import com.library.dtos.PaginatedResponseDTO;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class PaginationUtil {
    private static final ModelMapper modelMapper = new ModelMapper();

    /**
     * Construye una respuesta paginada genérica a partir de una Page de entidades
     * @param page Página de entidades
     * @param dtoClass Clase del DTO de respuesta
     * @param <T> Tipo de la entidad
     * @param <D> Tipo del DTO
     * @return PaginatedResponseDTO con los datos convertidos
     */
    public static <T, D> PaginatedResponseDTO buildPaginatedResponse(
            Page<T> page,
            Class<D> dtoClass) {

        PaginatedResponseDTO response = new PaginatedResponseDTO();

        List<D> data = page.getContent().stream()
                .map(entity -> modelMapper.map(entity, dtoClass))
                .toList();

        response.setData(new ArrayList<>(data));
        response.setPageSize(page.getSize());
        response.setTotalItems((int) page.getTotalElements());
        response.setCurrentPage(page.getNumber());
        response.setTotalPages(page.getTotalPages());
        response.setPrev(page.hasPrevious());
        response.setNext(page.hasNext());

        return response;
    }
}