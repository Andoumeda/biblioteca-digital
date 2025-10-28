package com.library.users.mappers;

import com.library.dtos.AnnouncementResponseDTO;
import com.library.entities.Announcement;
import org.mapstruct.*;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        collectionMappingStrategy = CollectionMappingStrategy.ADDER_PREFERRED,
        builder = @Builder(disableBuilder = true)
)
public interface AnnouncementMapper {

    /**
     * Convierte una entidad Announcement a AnnouncementResponseDTO
     */
    AnnouncementResponseDTO toResponseDTO(Announcement announcement);
}