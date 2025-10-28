package com.library.books.mappers;

import com.library.dtos.RatingResponseDTO;
import com.library.entities.Rating;
import org.mapstruct.*;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        uses = {BookMapper.class}, // Reutilizar mapper de Book
        injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        collectionMappingStrategy = CollectionMappingStrategy.ADDER_PREFERRED,
        builder = @Builder(disableBuilder = true)
)
public interface RatingMapper {

    /**
     * Convierte una entidad Rating a RatingResponseDTO
     */
    RatingResponseDTO toResponseDTO(Rating rating);
}