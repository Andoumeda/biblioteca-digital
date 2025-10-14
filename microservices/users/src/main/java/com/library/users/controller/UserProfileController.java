package com.library.users.controller;

import com.library.dtos.UserProfileRequestDTO;
import com.library.dtos.UserProfileResponseDTO;
import com.library.users.services.UserProfileService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user-profiles")
public class UserProfileController {

    private static final Logger logger = LoggerFactory.getLogger(UserProfileController.class);

    private final UserProfileService userProfileService;

    public UserProfileController(UserProfileService userProfileService) {
        this.userProfileService = userProfileService;
    }

    @PostMapping
    public ResponseEntity<UserProfileResponseDTO> createProfile(@Valid @RequestBody UserProfileRequestDTO dto) {
        logger.info("Recibido request POST /api/user-profiles con payload: {}", dto);
        try {
            UserProfileResponseDTO createdProfile = userProfileService.createProfile(dto);
            logger.info("Perfil creado exitosamente con ID {}", createdProfile.getId());
            return new ResponseEntity<>(createdProfile, HttpStatus.CREATED);
        } catch (Exception e) {
            logger.error("Error creando perfil: {}", e.getMessage(), e);
            throw e;
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserProfileResponseDTO> getProfileById(@PathVariable Integer id) {
        logger.info("Recibido request GET /api/user-profiles/{}", id);
        try {
            UserProfileResponseDTO profile = userProfileService.getProfileById(id);
            logger.info("Perfil obtenido exitosamente para ID {}", id);
            return ResponseEntity.ok(profile);
        } catch (Exception e) {
            logger.error("Error obteniendo perfil con ID {}: {}", id, e.getMessage(), e);
            throw e;
        }
    }

    @GetMapping
    public ResponseEntity<List<UserProfileResponseDTO>> getAllProfiles() {
        logger.info("Recibido request GET /api/user-profiles");
        try {
            List<UserProfileResponseDTO> profiles = userProfileService.getAllProfiles();
            logger.info("Se obtuvieron {} perfiles", profiles.size());
            return ResponseEntity.ok(profiles);
        } catch (Exception e) {
            logger.error("Error obteniendo perfiles: {}", e.getMessage(), e);
            throw e;
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserProfileResponseDTO> updateProfile(@PathVariable Integer id,
                                                                @Valid @RequestBody UserProfileRequestDTO dto) {
        logger.info("Recibido request PUT /api/user-profiles/{} con payload: {}", id, dto);
        try {
            UserProfileResponseDTO updatedProfile = userProfileService.updateProfile(id, dto);
            logger.info("Perfil con ID {} actualizado exitosamente", id);
            return ResponseEntity.ok(updatedProfile);
        } catch (Exception e) {
            logger.error("Error actualizando perfil con ID {}: {}", id, e.getMessage(), e);
            throw e;
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProfile(@PathVariable Integer id) {
        logger.info("Recibido request DELETE /api/user-profiles/{}", id);
        try {
            userProfileService.deleteProfile(id);
            logger.info("Perfil con ID {} eliminado exitosamente", id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            logger.error("Error eliminando perfil con ID {}: {}", id, e.getMessage(), e);
            throw e;
        }
    }
}
