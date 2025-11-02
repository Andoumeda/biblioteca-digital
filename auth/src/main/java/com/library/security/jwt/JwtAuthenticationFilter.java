package com.library.security.jwt;

import com.library.security.exceptions.TokenExpiredException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    // Excluir las rutas que no requieren del filtro JWT
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
    String path = request.getServletPath().toLowerCase();
    return path.startsWith("/swagger") ||
           path.startsWith("/swagger-ui") ||
           path.startsWith("/v3/api-docs") ||
           path.equals("/openapi.yaml") ||
           path.equals("/auth/login") ||
           path.equals("/auth/register");
    }

    @Autowired
    private JwtTokenProvider jwtTokenProvider;
    @Autowired
    private UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // Obtiene el encabezado Authorization de la solicitud
        String authHeader = request.getHeader("Authorization");
        logger.info("Authorization header: {}", authHeader);
        String token = null;
        String username = null;

        // Extrae el token JWT del encabezado Authorization
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
            logger.info("Token extraído: {}", token);
            username = jwtTokenProvider.getUsernameFromToken(token);
            logger.info("Username extraído del token: {}", username);
        } else {
            logger.warn("No se encontró el header Authorization o no tiene el formato Bearer");
        }

        // Si el contexto de seguridad no tiene autenticación, realiza la validación del token
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            // Valida si el token ha expirado
            if (jwtTokenProvider.isTokenExpired(token)) {
                logger.warn("Token expirado");
                throw new TokenExpiredException();
            }

            // Se carga el usuario asociado al token
            logger.info("Cargando usuario: {}", username);
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            // Crea un token de autenticación y lo establece en el contexto de seguridad
            logger.info("Authorities del usuario: {}", userDetails.getAuthorities());
            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authToken);
        } else if (username == null) {
            logger.warn("No se pudo extraer el usuario del token");
        }
        filterChain.doFilter(request, response);
    }
}
