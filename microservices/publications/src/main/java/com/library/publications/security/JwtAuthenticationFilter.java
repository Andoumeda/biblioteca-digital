package com.library.publications.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Filtro que extrae el JWT del header Authorization,
 * valida el token y extrae la información del usuario
 * para ponerla disponible en AuthContext.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        try {
            String authHeader = request.getHeader("Authorization");

            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);

                if (jwtUtil.validateToken(token)) {
                    String username = jwtUtil.extractUsername(token);
                    String role = jwtUtil.extractRole(token);
                    Integer userId = jwtUtil.extractUserId(token);
                    Integer userProfileId = jwtUtil.extractUserProfileId(token);

                    AuthContext context = new AuthContext(username, role, userId, userProfileId);
                    AuthContext.setContext(context);

                    log.debug("JWT válido para usuario: {} con rol: {}", username, role);
                } else {
                    log.warn("Token JWT inválido");
                }
            }
        } catch (Exception e) {
            log.error("Error al procesar JWT: {}", e.getMessage());
        }

        try {
            filterChain.doFilter(request, response);
        } finally {
            // Limpiar el contexto después de procesar la petición
            AuthContext.clear();
        }
    }
}