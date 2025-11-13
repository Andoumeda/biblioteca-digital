package com.library.publications.mappers;

import com.library.dtos.PublicationCategoryResponseDTO;
import com.library.entities.PublicationCategory;
import org.mapstruct.*;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        builder = @Builder(disableBuilder = true),
        uses = {PublicationMapper.class, CategoryMapper.class}
)
public interface PublicationCategoryMapper {
    @Mapping(target = "publication", source = "publication")
    @Mapping(target = "category", source = "category")
    @Mapping(target = "publicationId", source = "publication.id")
    @Mapping(target = "categoryId", source = "category.id")
    @Mapping(target = "relevanceScore", source = "relevanceScore")
    PublicationCategoryResponseDTO toResponseDTO(PublicationCategory publicationCategory);
}
