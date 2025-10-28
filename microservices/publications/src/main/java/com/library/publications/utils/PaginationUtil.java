package com.library.publications.utils;

import com.library.dtos.PaginatedResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

@Component
public class PaginationUtil {

    /**
     * Construye una respuesta paginada genérica a partir de una Page de entidades
     * Usa una función de mapeo proporcionada (típicamente un mapper de MapStruct)
     * @param page Página de entidades
     * @param mapper Función para convertir entidad a DTO (ejemplo: publicationMapper::toResponseDTO)
     * @param <T> Tipo de la entidad
     * @param <D> Tipo del DTO
     * @return PaginatedResponseDTO con los datos convertidos
     */
    public static <T, D> PaginatedResponseDTO buildPaginatedResponse(
            Page<T> page,
            Function<T, D> mapper) {

        PaginatedResponseDTO response = new PaginatedResponseDTO();

        // Mapeo secuencial - parallel streams causan ConcurrentModificationException con Hibernate
        List<D> data = page.getContent().stream()
                .map(mapper)
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