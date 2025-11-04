package com.library.security.service;

import com.library.dtos.*;
import com.library.entities.Role;
import com.library.entities.UserProfile;
import com.library.security.exceptions.ForbiddenException;
import com.library.security.exceptions.NoResourceFoundException;
import com.library.security.exceptions.ResourceAlreadyExistsException;
import com.library.security.exceptions.UnauthorizedException;
import com.library.security.jwt.JwtTokenProvider;
import com.library.security.repository.RoleRepository;
import com.library.security.repository.UserRepository;
import com.library.security.repository.UserProfileRepository;
import com.library.entities.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.List;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserProfileRepository userProfileRepository;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public AuthResponseDTO registerUser(RegisterRequestDTO registerRequestDTO) {

        // Validar que los campos no sean nulos
        if (
            registerRequestDTO.getUsername() == null ||
            registerRequestDTO.getEmail() == null ||
            registerRequestDTO.getPassword() == null ||
            registerRequestDTO.getUsername().isEmpty() ||
            registerRequestDTO.getEmail().isEmpty() ||
            registerRequestDTO.getPassword().isEmpty()
        ) {
            throw new IllegalArgumentException("Usuario, email y contraseña son obligatorios");
        }

        // Verificar si el nombre de usuario
        if (userRepository.findByUsernameAndIsDeletedFalse(registerRequestDTO.getUsername()).isPresent()) {
            throw new ResourceAlreadyExistsException("El usuario: " + registerRequestDTO.getUsername() + " está en uso");
        }

        // Verificar si el email ya existe
        if (userRepository.findByEmailAndIsDeletedFalse(registerRequestDTO.getEmail()).isPresent()) {
            throw new ResourceAlreadyExistsException("Email: " + registerRequestDTO.getEmail() + " ya está en uso");
        }

        // Crear el usuario con rol USER por defecto
        Optional<Role> roleOpt = roleRepository.findByNameAndIsDeletedFalse("USER");

        if (roleOpt.isEmpty()) {
            throw new NoResourceFoundException("Rol USER no encontrado");
        }

        var role = roleOpt.get();
        String encodedPassword = passwordEncoder.encode(registerRequestDTO.getPassword());

        User user = new User();
        user.setUsername(registerRequestDTO.getUsername().toLowerCase());
        user.setEmail(registerRequestDTO.getEmail());
        user.setPassword(encodedPassword);
        user.setRole(role);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        user.setIsDeleted(false);

        userRepository.save(user);

        // Crear automáticamente el UserProfile asociado
        UserProfile userProfile = new UserProfile();
        userProfile.setUser(user);
        userProfile.setDisplayName(registerRequestDTO.getUsername()); // Usar username como displayName por defecto
        userProfile.setBio(null); // Sin biografía inicialmente
        userProfile.setProfilePicture(null); // Sin foto de perfil inicialmente
        userProfile.setCreatedAt(LocalDateTime.now());
        userProfile.setUpdatedAt(LocalDateTime.now());
        userProfile.setIsDeleted(false);

        userProfileRepository.save(userProfile);

        // Establecer la relación bidireccional
        user.setUserProfile(userProfile);

        org.springframework.security.core.userdetails.User springUser =
            new org.springframework.security.core.userdetails.User(
                user.getUsername(), user.getPassword(),
                List.of(new SimpleGrantedAuthority("USER"))
            );

        // Generar el token JWT
        UsernamePasswordAuthenticationToken authToken =
            new UsernamePasswordAuthenticationToken(springUser, null, springUser.getAuthorities());
        String jwt = jwtTokenProvider.generateToken(authToken, user.getId(), userProfile.getId());

        AuthResponseDTO responseDTO = new AuthResponseDTO();
        responseDTO.setTokenType("Bearer");
        responseDTO.setAccessToken(jwt);
        return responseDTO;
    }

    public AuthResponseDTO loginUser(LoginRequestDTO loginRequestDTO) {

        // Validar que los campos no sean nulos
        if (loginRequestDTO.getUsername() == null ||
            loginRequestDTO.getPassword() == null ||
            loginRequestDTO.getUsername().isEmpty() ||
            loginRequestDTO.getPassword().isEmpty()
        ) {
            throw new IllegalArgumentException("Usuario y contraseña obligatorios");
        }

        // Buscar el usuario por nombre de usuario
        User user = userRepository.findByUsernameAndIsDeletedFalse(loginRequestDTO.getUsername())
            .orElseThrow(() -> new NoResourceFoundException("Usuario " + loginRequestDTO.getUsername() + " no encontrado"));

        // Verificar la contraseña
        if (!passwordEncoder.matches(loginRequestDTO.getPassword(), user.getPassword())) {
            throw new UnauthorizedException("Contraseña incorrecta");
        }

        // Obtener el UserProfile asociado al usuario
        UserProfile userProfile = user.getUserProfile();
        if (userProfile == null) {
            throw new NoResourceFoundException("No se encontró el perfil de usuario asociado");
        }

        // Generar el token JWT
        org.springframework.security.core.userdetails.User springUser =
            new org.springframework.security.core.userdetails.User(
                user.getUsername(), user.getPassword(),
                List.of(new SimpleGrantedAuthority(user.getRole().getName()))
            );
        UsernamePasswordAuthenticationToken authToken =
            new UsernamePasswordAuthenticationToken(springUser, null, springUser.getAuthorities());
        String jwt = jwtTokenProvider.generateToken(authToken, user.getId(), userProfile.getId());

        AuthResponseDTO responseDTO = new AuthResponseDTO();
        responseDTO.setTokenType("Bearer");
        responseDTO.setAccessToken(jwt);
        return responseDTO;
    }

    public UserResponseDTO updateUser(Integer id, RegisterRequestDTO registerRequestDTO) {

        if (id == null || id <= 0)
            throw new IllegalArgumentException("Id de usuario inválida");

        if (
            registerRequestDTO.getUsername() == null ||
            registerRequestDTO.getEmail() == null ||
            registerRequestDTO.getPassword() == null ||
            registerRequestDTO.getUsername().isEmpty() ||
            registerRequestDTO.getEmail().isEmpty() ||
            registerRequestDTO.getPassword().isEmpty()
        ) {
            throw new IllegalArgumentException("Usuario, email y contraseña son obligatorios");
        }

        // Obtener el usuario quien realiza la solicitud desde el contexto de seguridad
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String currentUsername;

        if (principal instanceof UserDetails)
            currentUsername = ((UserDetails) principal).getUsername();
        else
            currentUsername = principal.toString();

        User currentUser = userRepository.findByUsernameAndIsDeletedFalse(currentUsername).orElseThrow(() -> new NoResourceFoundException("El usuario del contexto de seguridad actual no fue encontrado"));

        // Verificar permisos del usuario del contexto de seguridad
        boolean isAdmin = currentUser.getRole().getName().equals("ADMIN");
        boolean isOwner = currentUser.getId().equals(id);
        if (!isAdmin && !isOwner) {
            throw new ForbiddenException("No tienes permisos para actualizar este usuario");
        }

        // Proceso para actualizar el usuario especificado por la id

        User user = userRepository.findByIdAndIsDeletedFalse(id).orElseThrow(() -> new NoResourceFoundException("Usuario con la id:" + id + " no existe"));

        // Verificar si el nuevo nombre de usuario ya existe
        if (
            !user.getUsername().equals(registerRequestDTO.getUsername()) &&
            userRepository.findByUsernameAndIsDeletedFalse(registerRequestDTO.getUsername()).isPresent()
        ){
            throw new ResourceAlreadyExistsException("El usuario: " + registerRequestDTO.getUsername() + " está en uso");
        }

        // Verificar si el nuevo email ya existe
         if (
            !user.getEmail().equals(registerRequestDTO.getEmail()) &&
            userRepository.findByEmailAndIsDeletedFalse(registerRequestDTO.getEmail()).isPresent()
         ){
             throw new ResourceAlreadyExistsException("Email: " + registerRequestDTO.getEmail() + " ya está en uso");
        }

        user.setUsername(registerRequestDTO.getUsername());
        user.setEmail(registerRequestDTO.getEmail());
        user.setPassword(passwordEncoder.encode(registerRequestDTO.getPassword()));
        userRepository.save(user);

        UserResponseDTO responseDTO = new UserResponseDTO();
        responseDTO.setId(user.getId());
        responseDTO.setUsername(user.getUsername());
        responseDTO.setEmail(user.getEmail());
        responseDTO.setRole(user.getRole().getName());
        return responseDTO;
    }

    public UserResponseDTO promoteUser(Integer id) {
        if (id == null || id <= 0)
            throw new IllegalArgumentException("Id del usuario debe ser positivo");

        Role role;
        User user = userRepository.findByIdAndIsDeletedFalse(id).orElseThrow(() -> new NoResourceFoundException("Usuario con la id:" + id + " no existe"));
        role = roleRepository.findByNameAndIsDeletedFalse("ADMIN").orElseThrow(() -> new NoResourceFoundException("Rol ADMIN no encontrado"));
        user.setRole(role);
        userRepository.save(user);

        UserResponseDTO responseDTO = new UserResponseDTO();
        responseDTO.setId(user.getId());
        responseDTO.setUsername(user.getUsername());
        responseDTO.setEmail(user.getEmail());
        responseDTO.setRole(user.getRole().getName());
        return responseDTO;
    }

    public UserResponseDTO demoteUser(Integer id) {
        if (id == null || id <= 0)
            throw new IllegalArgumentException("Id del usuario debe ser positivo");

        Role role;
        User user = userRepository.findByIdAndIsDeletedFalse(id).orElseThrow(() -> new NoResourceFoundException("Usuario con la id:" + id + " no existe"));
        role = roleRepository.findByNameAndIsDeletedFalse("USER").orElseThrow(() -> new NoResourceFoundException("Rol USER no encontrado"));
        user.setRole(role);
        userRepository.save(user);

        UserResponseDTO responseDTO = new UserResponseDTO();
        responseDTO.setId(user.getId());
        responseDTO.setUsername(user.getUsername());
        responseDTO.setEmail(user.getEmail());
        responseDTO.setRole(user.getRole().getName());
        return responseDTO;
    }
}
