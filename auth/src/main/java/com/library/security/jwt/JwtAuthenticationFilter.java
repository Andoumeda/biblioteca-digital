package com.library.security.jwt;

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
        String token = null;
        String username = null;

        // Extrae el token JWT del encabezado Authorization
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);

            // Validar el token antes de extraer el username
            if (jwtTokenProvider.isTokenExpired(token)) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json");
                response.getWriter().write("{\"error\": \"Token expirado\"}");
                return;
            }
            username = jwtTokenProvider.getUsernameFromToken(token);
        }

        // Si el contexto de seguridad no tiene autenticación, realiza la validación del token
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            // Se carga el usuario asociado al token
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            // Crea un token de autenticación y lo establece en el contexto de seguridad
            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authToken);
        }
        filterChain.doFilter(request, response);
    }
}
