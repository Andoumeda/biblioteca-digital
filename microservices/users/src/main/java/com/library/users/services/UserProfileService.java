package com.library.users.services;

import com.library.dtos.PaginatedUserProfileResponseDTO;
import com.library.dtos.UserProfileRequestDTO;
import com.library.dtos.UserProfileResponseDTO;
import com.library.entities.User;
import com.library.entities.UserProfile;
import com.library.users.config.PaginationConfig;
import com.library.users.exception.BadRequestException;
import com.library.users.exception.DuplicateResourceException;
import com.library.users.exception.ResourceNotFoundException;
import com.library.users.mappers.UserProfileMapper;
import com.library.users.repository.UserProfileRepository;
import com.library.users.utils.NormalizeParameter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.CacheEvict;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class UserProfileService {

    private final UserProfileRepository repository;
    private final UserProfileMapper userProfileMapper;
    private final PaginationConfig paginationConfig;

    @PersistenceContext
    private EntityManager entityManager;

    @CachePut(value = "userProfiles", key = "#result.id")
    public UserProfileResponseDTO createProfile(UserProfileRequestDTO dto) {
        log.info("Intentando crear perfil para el usuario ID {}", dto.getUserId());

        // Validar si ya existe un perfil para este usuario
        if (repository.existsByUserId(Long.valueOf(dto.getUserId()))) {
            log.error("Ya existe un perfil para el usuario con ID {}", dto.getUserId());
            throw new DuplicateResourceException("El usuario con ID " + dto.getUserId() + " ya tiene un perfil.");
        }

        // Verificar que el usuario realmente exista
        User user = entityManager.find(User.class, dto.getUserId());
        if (user == null) {
            log.error("No se encontró el usuario con ID {}", dto.getUserId());
            throw new ResourceNotFoundException("No se encontró el usuario con ID " + dto.getUserId());
        }

        // Crear perfil
        UserProfile profile = new UserProfile();
        profile.setUser(user);
        profile.setDisplayName(dto.getDisplayName());
        profile.setBio(dto.getBio());
        profile.setProfilePicture(dto.getProfilePicture());

        repository.save(profile);

        log.info("Perfil creado exitosamente con ID {}", profile.getId());
        return userProfileMapper.toResponseDTO(profile);
    }

    @Cacheable(value = "userProfiles", key = "#id")
    public UserProfileResponseDTO getProfileById(Integer id) {
        log.info("Buscando perfil con ID {}", id);
        UserProfile profile = repository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> {
                    log.error("No se encontró el perfil con ID {}", id);
                    return new ResourceNotFoundException("UserProfile no encontrado con id " + id);
                });
        return userProfileMapper.toResponseDTO(profile);
    }

    @CachePut(value = "userProfiles", key = "#id")
    public UserProfileResponseDTO updateProfile(Integer id, UserProfileRequestDTO dto) {
        log.info("Actualizando perfil con ID {}", id);
        UserProfile profile = repository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> {
                    log.error("Intento de actualizar un perfil inexistente con ID {}", id);
                    return new ResourceNotFoundException("UserProfile no encontrado con id " + id);
                });

        profile.setDisplayName(dto.getDisplayName());
        profile.setBio(dto.getBio());
        profile.setProfilePicture(dto.getProfilePicture());
        repository.save(profile);

        log.info("Perfil con ID {} actualizado correctamente", id);
        return userProfileMapper.toResponseDTO(profile);
    }

    @CacheEvict(value = "userProfiles", key = "#id")
    public void deleteProfile(Integer id) {
        log.info("Eliminando perfil con ID {}", id);
        UserProfile profile = repository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> {
                    log.error("Intento de eliminar un perfil inexistente con ID {}", id);
                    return new ResourceNotFoundException("UserProfile no encontrado con id " + id);
                });

        // Soft delete del perfil
        profile.setIsDeleted(true);
        repository.save(profile);

        // Soft delete del usuario relacionado
        User user = repository.findActiveUserByProfileId(id);
        if (user != null) {
            user.setIsDeleted(true);
            entityManager.merge(user);
        }

        log.info("Perfil y usuario con ID {} marcados como eliminados", id);
    }

    public PaginatedUserProfileResponseDTO getUserProfilesByFilters(String displayName, Integer page) {
        log.info("Valor de displayName que llega al repository: {}", displayName);
        log.info("Tipo de displayName: {}", displayName == null ? "null" : displayName.getClass().getName());
        if (page == null || page < 0) {
            log.error("Error al buscar anuncios: Número de página inválido: {}", page);
            throw new BadRequestException("El número de página debe ser mayor o igual a 0");
        }

        String normalizedDisplayName = NormalizeParameter.normalizeString(displayName);

        if (normalizedDisplayName != null && normalizedDisplayName.trim().isEmpty()) {
            log.error("Error al buscar perfiles: El nombre para mostrar no puede estar vacío");
            throw new BadRequestException("El nombre para mostrar no puede estar vacío");
        }

        Pageable pageable = PageRequest.of(page, paginationConfig.getPageSize());
        Page<UserProfile> pageResult;
        if (normalizedDisplayName == null)
            pageResult = repository.findByIsDeletedFalse(pageable);
        else
            pageResult = repository.findByDisplayNameLike(normalizedDisplayName, pageable);

        List<UserProfileResponseDTO> dtos = pageResult.getContent().stream()
                .map(userProfileMapper::toResponseDTO)
                .collect(Collectors.toList());

        PaginatedUserProfileResponseDTO response = new PaginatedUserProfileResponseDTO();

        response.setData(dtos);
        response.setPageSize(pageResult.getSize());
        response.setTotalItems((int) pageResult.getTotalElements());
        response.setCurrentPage(pageResult.getNumber());
        response.setTotalPages(pageResult.getTotalPages());
        response.setNext(pageResult.hasNext());
        response.setPrev(pageResult.hasPrevious());

        return response;
    }
}