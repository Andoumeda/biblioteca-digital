package com.library.books.mappers;

import com.library.dtos.BookResponseDTO;
import com.library.entities.Book;
import org.mapstruct.*;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        uses = {AuthorMapper.class}, // Reutilizar mapper de Author
        injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        collectionMappingStrategy = CollectionMappingStrategy.ADDER_PREFERRED,
        builder = @Builder(disableBuilder = true)
)
public interface BookMapper {

    /**
     * Convierte una entidad Book a BookResponseDTO
     */
    BookResponseDTO toResponseDTO(Book book);
}