package com.library.users.services;

import com.library.dtos.UserProfileRequestDTO;
import com.library.dtos.UserProfileResponseDTO;
import com.library.entities.User;
import com.library.entities.UserProfile;
import com.library.users.exception.DuplicateResourceException;
import com.library.users.exception.ResourceNotFoundException;
import com.library.users.repository.UserProfileRepository;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserProfileServiceImpl implements UserProfileService {

    private static final Logger logger = LoggerFactory.getLogger(UserProfileServiceImpl.class);

    private final UserProfileRepository repository;
    private final ModelMapper modelMapper;

    @PersistenceContext
    private EntityManager entityManager;

    public UserProfileServiceImpl(UserProfileRepository repository, ModelMapper modelMapper) {
        this.repository = repository;
        this.modelMapper = modelMapper;
    }

    @Override
    public UserProfileResponseDTO createProfile(UserProfileRequestDTO dto) {
        logger.info("Intentando crear perfil para el usuario ID {}", dto.getUserId());

        // Validar si ya existe un perfil para este usuario
        if (repository.existsByUserId(Long.valueOf(dto.getUserId()))) {
            logger.error("Ya existe un perfil para el usuario con ID {}", dto.getUserId());
            throw new DuplicateResourceException("El usuario con ID " + dto.getUserId() + " ya tiene un perfil.");
        }

        // Verificar que el usuario realmente exista
        User user = entityManager.find(User.class, dto.getUserId());
        if (user == null) {
            logger.error("No se encontró el usuario con ID {}", dto.getUserId());
            throw new ResourceNotFoundException("No se encontró el usuario con ID " + dto.getUserId());
        }

        // Crear perfil
        UserProfile profile = new UserProfile();
        profile.setUser(user);
        profile.setDisplayName(dto.getDisplayName());
        profile.setBio(dto.getBio());
        profile.setProfilePicture(dto.getProfilePicture());

        repository.save(profile);

        logger.info("Perfil creado exitosamente con ID {}", profile.getId());
        return modelMapper.map(profile, UserProfileResponseDTO.class);
    }

    @Override
    public UserProfileResponseDTO getProfileById(Integer id) {
        logger.info("Buscando perfil con ID {}", id);
        UserProfile profile = repository.findById(id)
                .filter(p -> !Boolean.TRUE.equals(p.getIsDeleted()))
                .orElseThrow(() -> {
                    logger.error("No se encontró el perfil con ID {}", id);
                    return new ResourceNotFoundException("UserProfile no encontrado con id " + id);
                });
        return modelMapper.map(profile, UserProfileResponseDTO.class);
    }

    @Override
    public List<UserProfileResponseDTO> getAllProfiles() {
        logger.info("Obteniendo todos los perfiles activos");
        return repository.findAll().stream()
                .filter(p -> !Boolean.TRUE.equals(p.getIsDeleted()))
                .map(p -> modelMapper.map(p, UserProfileResponseDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public UserProfileResponseDTO updateProfile(Integer id, UserProfileRequestDTO dto) {
        logger.info("Actualizando perfil con ID {}", id);
        UserProfile profile = repository.findById(id)
                .filter(p -> !Boolean.TRUE.equals(p.getIsDeleted()))
                .orElseThrow(() -> {
                    logger.error("Intento de actualizar un perfil inexistente con ID {}", id);
                    return new ResourceNotFoundException("UserProfile no encontrado con id " + id);
                });

        profile.setDisplayName(dto.getDisplayName());
        profile.setBio(dto.getBio());
        profile.setProfilePicture(dto.getProfilePicture());
        repository.save(profile);

        logger.info("Perfil con ID {} actualizado correctamente", id);
        return modelMapper.map(profile, UserProfileResponseDTO.class);
    }

    @Override
    public void deleteProfile(Integer id) {
        logger.info("Eliminando perfil con ID {}", id);
        UserProfile profile = repository.findById(id)
                .filter(p -> !Boolean.TRUE.equals(p.getIsDeleted()))
                .orElseThrow(() -> {
                    logger.error("Intento de eliminar un perfil inexistente con ID {}", id);
                    return new ResourceNotFoundException("UserProfile no encontrado con id " + id);
                });

        profile.setIsDeleted(true);
        repository.save(profile);
        logger.info("Perfil con ID {} marcado como eliminado", id);
    }
}
