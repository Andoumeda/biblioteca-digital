package com.library.users.mappers;

import com.library.dtos.UserProfileResponseDTO;
import com.library.entities.UserProfile;
import org.mapstruct.*;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        collectionMappingStrategy = CollectionMappingStrategy.ADDER_PREFERRED,
        builder = @Builder(disableBuilder = true)
)
public interface UserProfileMapper {

    /**
     * Convierte una entidad UserProfile a UserProfileResponseDTO
     */
    UserProfileResponseDTO toResponseDTO(UserProfile userProfile);
}