package com.library.books.mappers;

import com.library.dtos.BookAuthorResponseDTO;
import com.library.entities.BookAuthor;
import org.mapstruct.*;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        builder = @Builder(disableBuilder = true),
        uses = {BookMapper.class, AuthorMapper.class}
)
public interface BookAuthorMapper {
    @Mapping(target = "author", source = "author")
    @Mapping(target = "book", source = "book")
    @Mapping(target = "authorId", source = "author.id")
    @Mapping(target = "bookId", source = "book.id")
    @Mapping(target = "contributionType", expression = "java(com.library.dtos.BookAuthorResponseDTO.ContributionTypeEnum.fromValue(bookAuthor.getContributionType()))")
    BookAuthorResponseDTO toResponseDTO(BookAuthor bookAuthor);
}

