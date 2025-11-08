package com.library.api_gateway.config;

import com.library.api_gateway.filter.AuthFilter;

import com.library.api_gateway.jwt.JwtAccessDeniedHandler;
import com.library.api_gateway.jwt.JwtAuthenticationEntryPoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity
public class GatewayConfig {

    @Autowired
    private AuthFilter authFilter;

    @Autowired
    private JwtAuthenticationEntryPoint authenticationEntryPoint;

    @Autowired
    private JwtAccessDeniedHandler accessDeniedHandler;

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http
            .cors(cors -> cors.disable())
            .csrf(ServerHttpSecurity.CsrfSpec::disable)
            .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
            .formLogin(ServerHttpSecurity.FormLoginSpec::disable)

            .authorizeExchange(exchanges -> exchanges
                // ===== RUTAS PÚBLICAS =====
                .pathMatchers("/swagger/**").permitAll()
                .pathMatchers("/swagger-ui/**").permitAll()
                .pathMatchers("/swagger-ui.html").permitAll()
                .pathMatchers("/v3/api-docs/**").permitAll()
                .pathMatchers("/webjars/**").permitAll()
                .pathMatchers("/").permitAll()

                // ===== AUTH =====
                .pathMatchers("/auth/login", "/auth/register").permitAll()
                .pathMatchers(HttpMethod.PUT, "/auth/users/**").hasAnyRole("ADMIN", "USER")
                .pathMatchers(HttpMethod.PATCH, "/auth/users/*/promote").hasRole("ADMIN")
                .pathMatchers(HttpMethod.PATCH, "/auth/users/*/demote").hasRole("ADMIN")

                // ===== USER PROFILES =====
                .pathMatchers(HttpMethod.POST, "/user-profiles").hasAnyRole("ADMIN", "USER")
                .pathMatchers(HttpMethod.GET, "/user-profiles", "/user-profiles/**").hasAnyRole("ADMIN", "USER")
                .pathMatchers(HttpMethod.PUT, "/user-profiles/**").hasAnyRole("ADMIN", "USER")
                .pathMatchers(HttpMethod.DELETE, "/user-profiles/**").hasAnyRole("ADMIN", "USER")

                // ===== ANNOUNCEMENTS =====
                .pathMatchers(HttpMethod.POST, "/announcements").hasRole("ADMIN")
                .pathMatchers(HttpMethod.GET, "/announcements", "/announcements/**").hasAnyRole("ADMIN", "USER")
                .pathMatchers(HttpMethod.PUT, "/announcements/**").hasRole("ADMIN")
                .pathMatchers(HttpMethod.DELETE, "/announcements/**").hasRole("ADMIN")

                // ===== PUBLICATIONS =====
                .pathMatchers(HttpMethod.POST, "/publications").hasAnyRole("ADMIN", "USER")
                .pathMatchers(HttpMethod.GET, "/publications", "/publications/**").hasAnyRole("ADMIN", "USER")
                .pathMatchers(HttpMethod.PUT, "/publications/**").hasAnyRole("ADMIN", "USER")
                .pathMatchers(HttpMethod.DELETE, "/publications/**").hasAnyRole("ADMIN", "USER")
                .pathMatchers(HttpMethod.PATCH, "/publications/*/state").hasRole("ADMIN")

                // ===== CATEGORIES =====
                .pathMatchers(HttpMethod.POST, "/categories").hasRole("ADMIN")
                .pathMatchers(HttpMethod.GET, "/categories", "/categories/**").hasAnyRole("ADMIN", "USER")
                .pathMatchers(HttpMethod.PUT, "/categories/**").hasRole("ADMIN")
                .pathMatchers(HttpMethod.DELETE, "/categories/**").hasRole("ADMIN")

                // ===== FAVORITES =====
                .pathMatchers(HttpMethod.POST, "/favorites").hasAnyRole("ADMIN", "USER")
                .pathMatchers(HttpMethod.GET, "/favorites", "/favorites/**").hasAnyRole("ADMIN", "USER")
                .pathMatchers(HttpMethod.DELETE, "/favorites/**").hasAnyRole("ADMIN", "USER")

                // ===== BOOKS =====
                .pathMatchers(HttpMethod.POST, "/books").hasAnyRole("ADMIN", "USER")
                .pathMatchers(HttpMethod.GET, "/books", "/books/**").hasAnyRole("ADMIN", "USER")
                .pathMatchers(HttpMethod.PUT, "/books/**").hasAnyRole("ADMIN", "USER")
                .pathMatchers(HttpMethod.DELETE, "/books/**").hasAnyRole("ADMIN", "USER")

                // ===== AUTHORS =====
                .pathMatchers(HttpMethod.POST, "/authors").hasAnyRole("ADMIN", "USER")
                .pathMatchers(HttpMethod.GET, "/authors", "/authors/**").hasAnyRole("ADMIN", "USER")
                .pathMatchers(HttpMethod.PUT, "/authors/**").hasAnyRole("ADMIN", "USER")
                .pathMatchers(HttpMethod.DELETE, "/authors/**").hasAnyRole("ADMIN", "USER")

                // ===== RATINGS =====
                .pathMatchers(HttpMethod.POST, "/ratings").hasAnyRole("ADMIN", "USER")
                .pathMatchers(HttpMethod.GET, "/ratings", "/ratings/**").hasAnyRole("ADMIN", "USER")
                .pathMatchers(HttpMethod.PUT, "/ratings/**").hasAnyRole("ADMIN", "USER")
                .pathMatchers(HttpMethod.DELETE, "/ratings/**").hasAnyRole("ADMIN", "USER")

                // ===== POR DEFECTO - Requiere autenticación =====
                .anyExchange().authenticated()
            )

            // Manejo de errores personalizado
            .exceptionHandling(exceptionHandlingSpec -> exceptionHandlingSpec
                .authenticationEntryPoint(authenticationEntryPoint)
                .accessDeniedHandler(accessDeniedHandler)
            )
            // Agregar el filtro JWT personalizado
            .addFilterAt(authFilter, SecurityWebFiltersOrder.AUTHENTICATION)
            .build();
    }
}