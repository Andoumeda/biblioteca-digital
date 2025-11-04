package com.library.api_gateway.config;

import com.library.api_gateway.filter.AuthFilter;

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

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http
            .cors(cors -> cors.disable())  // Deshabilita CORS de Spring Security porque se usa CorsWebFilter
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

                // ===== AUTH =====
                .pathMatchers("/auth/login", "/auth/register").permitAll()
                .pathMatchers(HttpMethod.PUT, "/auth/users/**").hasAnyAuthority("ADMIN", "USER")
                .pathMatchers(HttpMethod.PATCH, "/auth/users/*/promote").hasAuthority("ADMIN")
                .pathMatchers(HttpMethod.PATCH, "/auth/users/*/demote").hasAuthority("ADMIN")

                // ===== USER PROFILES =====
                .pathMatchers(HttpMethod.POST, "/user-profiles").hasAnyAuthority("ADMIN", "USER")
                .pathMatchers(HttpMethod.GET, "/user-profiles", "/user-profiles/**").hasAnyAuthority("ADMIN", "USER")
                .pathMatchers(HttpMethod.PUT, "/user-profiles/**").hasAnyAuthority("ADMIN", "USER")
                .pathMatchers(HttpMethod.DELETE, "/user-profiles/**").hasAnyAuthority("ADMIN", "USER")

                // ===== ANNOUNCEMENTS =====
                .pathMatchers(HttpMethod.POST, "/announcements").hasAuthority("ADMIN")
                .pathMatchers(HttpMethod.GET, "/announcements", "/announcements/**").hasAnyAuthority("ADMIN", "USER")
                .pathMatchers(HttpMethod.PUT, "/announcements/**").hasAuthority("ADMIN")
                .pathMatchers(HttpMethod.DELETE, "/announcements/**").hasAuthority("ADMIN")

                // ===== PUBLICATIONS =====
                .pathMatchers(HttpMethod.POST, "/publications").hasAnyAuthority("ADMIN", "USER")
                .pathMatchers(HttpMethod.GET, "/publications", "/publications/**").hasAnyAuthority("ADMIN", "USER")
                .pathMatchers(HttpMethod.PUT, "/publications/**").hasAnyAuthority("ADMIN", "USER")
                .pathMatchers(HttpMethod.DELETE, "/publications/**").hasAnyAuthority("ADMIN", "USER")
                .pathMatchers(HttpMethod.PATCH, "/publications/*/state").hasAuthority("ADMIN")

                // ===== CATEGORIES =====
                .pathMatchers(HttpMethod.POST, "/categories").hasAuthority("ADMIN")
                .pathMatchers(HttpMethod.GET, "/categories", "/categories/**").hasAnyAuthority("ADMIN", "USER")
                .pathMatchers(HttpMethod.PUT, "/categories/**").hasAuthority("ADMIN")
                .pathMatchers(HttpMethod.DELETE, "/categories/**").hasAuthority("ADMIN")

                // ===== FAVORITES =====
                .pathMatchers(HttpMethod.POST, "/favorites").hasAnyAuthority("ADMIN", "USER")
                .pathMatchers(HttpMethod.GET, "/favorites", "/favorites/**").hasAnyAuthority("ADMIN", "USER")
                .pathMatchers(HttpMethod.DELETE, "/favorites/**").hasAnyAuthority("ADMIN", "USER")

                // ===== BOOKS =====
                .pathMatchers(HttpMethod.POST, "/books").hasAnyAuthority("ADMIN", "USER")
                .pathMatchers(HttpMethod.GET, "/books", "/books/**").hasAnyAuthority("ADMIN", "USER")
                .pathMatchers(HttpMethod.PUT, "/books/**").hasAnyAuthority("ADMIN", "USER")
                .pathMatchers(HttpMethod.DELETE, "/books/**").hasAnyAuthority("ADMIN", "USER")

                // ===== AUTHORS =====
                .pathMatchers(HttpMethod.POST, "/authors").hasAnyAuthority("ADMIN", "USER")
                .pathMatchers(HttpMethod.GET, "/authors", "/authors/**").hasAnyAuthority("ADMIN", "USER")
                .pathMatchers(HttpMethod.PUT, "/authors/**").hasAnyAuthority("ADMIN", "USER")
                .pathMatchers(HttpMethod.DELETE, "/authors/**").hasAnyAuthority("ADMIN", "USER")

                // ===== RATINGS =====
                .pathMatchers(HttpMethod.POST, "/ratings").hasAnyAuthority("ADMIN", "USER")
                .pathMatchers(HttpMethod.GET, "/ratings", "/ratings/**").hasAnyAuthority("ADMIN", "USER")
                .pathMatchers(HttpMethod.PUT, "/ratings/**").hasAnyAuthority("ADMIN", "USER")
                .pathMatchers(HttpMethod.DELETE, "/ratings/**").hasAnyAuthority("ADMIN", "USER")

                // ===== POR DEFECTO - Requiere autenticación =====
                .anyExchange().authenticated()
            )

            // Agregar el filtro JWT personalizado
            .addFilterAt(authFilter, SecurityWebFiltersOrder.AUTHENTICATION)
            .build();
    }
}