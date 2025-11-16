package com.library.users.mappers;

import com.library.dtos.ProfileAnnouncementResponseDTO;
import com.library.entities.ProfileAnnouncement;
import org.mapstruct.*;

@Mapper(
    componentModel = MappingConstants.ComponentModel.SPRING,
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    injectionStrategy = InjectionStrategy.CONSTRUCTOR,
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
    collectionMappingStrategy = CollectionMappingStrategy.ADDER_PREFERRED,
    builder = @Builder(disableBuilder = true),
    uses = {AnnouncementMapper.class, UserProfileMapper.class}
)
public interface ProfileAnnouncementMapper {

    /**
     * Convierte una entidad ProfileAnnouncement a ProfileAnnouncementResponseDTO
     */
    @Mapping(target = "announcement", source = "announcement")
    @Mapping(target = "userProfile", source = "userProfile")
    @Mapping(target = "announcementId", source = "announcement.id")
    @Mapping(target = "profileId", source = "userProfile.id")
    ProfileAnnouncementResponseDTO toResponseDTO(ProfileAnnouncement entity);
}
