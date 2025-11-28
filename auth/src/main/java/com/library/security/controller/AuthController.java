package com.library.security.controller;

import com.library.api.AuthApi;
import com.library.dtos.*;
import com.library.security.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
public class AuthController implements AuthApi {

    @Autowired
    private UserService userService;

    @Override
    public ResponseEntity<AuthResponseDTO> login(LoginRequestDTO loginRequestDTO) {
        AuthResponseDTO response = userService.loginUser(loginRequestDTO);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<AuthResponseDTO> registerUser(RegisterRequestDTO registerRequestDTO) {
        AuthResponseDTO response = userService.registerUser(registerRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Refresh token endpoint - generates a new token from a valid existing token
     */
    @PostMapping("/auth/refresh")
    public ResponseEntity<AuthResponseDTO> refreshToken(@RequestHeader("Authorization") String authHeader) {
        try {
            // Extract token from Bearer header
            String token = authHeader.replace("Bearer ", "");
            AuthResponseDTO response = userService.refreshToken(token);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @Override
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<UserResponseDTO> updateUser(Integer id, UpdateRequestDTO updateRequestDTO) {
        UserResponseDTO response = userService.updateUser(id, updateRequestDTO);
        return ResponseEntity.ok(response);
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponseDTO> promoteUser(Integer id) {
        UserResponseDTO response = userService.promoteUser(id);
        return ResponseEntity.ok(response);
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponseDTO> demoteUser(Integer id) {
        UserResponseDTO response = userService.demoteUser(id);
        return ResponseEntity.ok(response);
    }
}