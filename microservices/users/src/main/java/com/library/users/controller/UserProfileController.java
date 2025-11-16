package com.library.users.controller;

import com.library.dtos.PaginatedUserProfileResponseDTO;
import com.library.dtos.UserProfileRequestDTO;
import com.library.dtos.UserProfileResponseDTO;
import com.library.users.api.UserProfilesApi;
import com.library.users.services.UserProfileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Slf4j
public class UserProfileController implements UserProfilesApi {

    private final UserProfileService userProfileService;

    @Override
    public ResponseEntity<UserProfileResponseDTO> createUserProfile(UserProfileRequestDTO dto) {
        log.info("Recibido request POST /user-profiles con payload: {}", dto);
        try {
            UserProfileResponseDTO createdProfile = userProfileService.createProfile(dto);
            log.info("Perfil creado exitosamente con ID {}", createdProfile.getId());
            return new ResponseEntity<>(createdProfile, HttpStatus.CREATED);
        } catch (Exception e) {
            log.error("Error creando perfil: {}", e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public ResponseEntity<UserProfileResponseDTO> getUserProfileById(@PathVariable Integer id) {
        log.info("Recibido request GET /user-profiles/{}", id);
        try {
            UserProfileResponseDTO profile = userProfileService.getProfileById(id);
            log.info("Perfil obtenido exitosamente para ID {}", id);
            return ResponseEntity.ok(profile);
        } catch (Exception e) {
            log.error("Error obteniendo perfil con ID {}: {}", id, e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public ResponseEntity<PaginatedUserProfileResponseDTO> getUserProfilesByFilters(String displayName, Integer page) {
        log.info("Recibido request GET /user-profiles");
        try {
            PaginatedUserProfileResponseDTO response = userProfileService.getUserProfilesByFilters(displayName, page);
            log.info("Perfiles obtenidos exitosamente con filtros");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error obteniendo perfiles con filtros: {}", e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public ResponseEntity<UserProfileResponseDTO> updateUserProfile(Integer id, UserProfileRequestDTO dto) {
        log.info("Recibido request PUT /user-profiles/{} con payload: {}", id, dto);
        try {
            UserProfileResponseDTO updatedProfile = userProfileService.updateProfile(id, dto);
            log.info("Perfil con ID {} actualizado exitosamente", id);
            return ResponseEntity.ok(updatedProfile);
        } catch (Exception e) {
            log.error("Error actualizando perfil con ID {}: {}", id, e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public ResponseEntity<Void> deleteUserProfile(Integer id) {
        log.info("Recibido request DELETE /user-profiles/{}", id);
        try {
            userProfileService.deleteProfile(id);
            log.info("Perfil con ID {} eliminado exitosamente", id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            log.error("Error eliminando perfil con ID {}: {}", id, e.getMessage(), e);
            throw e;
        }
    }
}