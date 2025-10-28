package com.library.books.mappers;

import com.library.dtos.AuthorResponseDTO;
import com.library.entities.Author;
import org.mapstruct.*;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        collectionMappingStrategy = CollectionMappingStrategy.ADDER_PREFERRED,
        builder = @Builder(disableBuilder = true)
)
public interface AuthorMapper {

    /**
     * Convierte una entidad Author a AuthorResponseDTO
     */
    AuthorResponseDTO toResponseDTO(Author author);
}