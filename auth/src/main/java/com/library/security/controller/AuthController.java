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

    @Override
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('USER')")
    public ResponseEntity<UserResponseDTO> updateUser(Integer id, RegisterRequestDTO registerRequestDTO) {
        UserResponseDTO response = userService.updateUser(id, registerRequestDTO);
        return ResponseEntity.ok(response);
    }

    @Override
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<UserResponseDTO> promoteUser(Integer id) {
        UserResponseDTO response = userService.promoteUser(id);
        return ResponseEntity.ok(response);
    }

    @Override
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<UserResponseDTO> demoteUser(Integer id) {
        UserResponseDTO response = userService.demoteUser(id);
        return ResponseEntity.ok(response);
    }
}