package com.library.publications.mappers;

import com.library.dtos.PublicationResponseDTO;
import com.library.entities.Publication;
import org.mapstruct.*;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        uses = {CategoryMapper.class}, // Reutilizar mappers existentes
        injectionStrategy = InjectionStrategy.CONSTRUCTOR, // Inyección más rápida
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        collectionMappingStrategy = CollectionMappingStrategy.ADDER_PREFERRED,
        builder = @Builder(disableBuilder = true) // Deshabilitar builders innecesarios
)
public interface PublicationMapper {

    /**
     * Convierte una entidad Publication a PublicationResponseDTO
     */
    PublicationResponseDTO toResponseDTO(Publication publication);
}