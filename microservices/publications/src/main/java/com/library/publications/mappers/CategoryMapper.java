package com.library.publications.mappers;

import com.library.dtos.CategoryResponseDTO;
import com.library.entities.Category;
import org.mapstruct.*;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        collectionMappingStrategy = CollectionMappingStrategy.ADDER_PREFERRED,
        builder = @Builder(disableBuilder = true)
)
public interface CategoryMapper {

    /**
     * Convierte una entidad Category a CategoryResponseDTO
     */
    CategoryResponseDTO toResponseDTO(Category category);
}