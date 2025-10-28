package com.library.publications.mappers;

import com.library.dtos.FavoriteResponseDTO;
import com.library.entities.Favorite;
import org.mapstruct.*;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        uses = {PublicationMapper.class, CategoryMapper.class}, // Reutilizar mappers
        injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        collectionMappingStrategy = CollectionMappingStrategy.ADDER_PREFERRED,
        builder = @Builder(disableBuilder = true)
)
public interface FavoriteMapper {

    /**
     * Convierte una entidad Favorite a FavoriteResponseDTO
     */
    FavoriteResponseDTO toResponseDTO(Favorite favorite);
}