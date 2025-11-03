package com.library.api_gateway.filter;

import com.library.api_gateway.util.JwtUtil;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * Este filtro extrae el token JWT, lo valida y establece la autenticación
 * en el contexto de seguridad de Spring Security.
 */
@Component
public class AuthFilter implements WebFilter {

    private static final Logger logger = LoggerFactory.getLogger(AuthFilter.class);

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);

            try {
                if (jwtUtil.validateToken(token)) {
                    String username = jwtUtil.extractUsername(token);
                    String role = jwtUtil.extractRole(token);
                    Integer userId = jwtUtil.extractUserId(token);
                    Integer userProfileId = jwtUtil.extractUserProfileId(token);

                    // Crear authority de Spring Security con el rol (agregar prefijo ROLE_)
                    SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + role);

                    // Crear objeto de autenticación
                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(username, null, List.of(authority));

                    // Agregar headers para los microservicios
                    ServerHttpRequest.Builder requestBuilder = exchange.getRequest().mutate()
                            .header("X-User-Name", username)
                            .header("X-User-Role", role);

                    // Agregar userId solo si existe en el token
                    if (userId != null) {
                        requestBuilder.header("X-User-Id", userId.toString());
                    }

                    // Agregar userProfileId solo si existe en el token
                    if (userProfileId != null) {
                        requestBuilder.header("X-User-Profile-Id", userProfileId.toString());
                    }

                    ServerHttpRequest modifiedRequest = requestBuilder.build();

                    ServerWebExchange modifiedExchange = exchange.mutate()
                            .request(modifiedRequest)
                            .build();

                    // Establecer autenticación en el contexto y continuar
                    return chain.filter(modifiedExchange)
                            .contextWrite(ReactiveSecurityContextHolder.withAuthentication(authentication));
                }
            } catch (ExpiredJwtException e) {
                logger.warn("Token JWT expirado: {}", e.getMessage());
            } catch (SignatureException e) {
                logger.error("Token JWT con firma inválida: {}", e.getMessage());
            } catch (MalformedJwtException e) {
                logger.warn("Token JWT malformado: {}", e.getMessage());
            } catch (IllegalArgumentException e) {
                logger.debug("Token JWT inválido (vacío o nulo): {}", e.getMessage());
            } catch (Exception e) {
                logger.error("Error inesperado al procesar token JWT: {}", e.getMessage(), e);
            }
        }

        // Si no hay token válido, continuar sin autenticación
        // Spring Security manejará automáticamente el 401 Unauthorized
        // para rutas que requieren autenticación
        return chain.filter(exchange);
    }
}