package com.library.security.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;
import java.util.List;


@Configuration
public class CorsConfig {

    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        // Permitir credenciales
        config.setAllowCredentials(true);

        // Permitir orígenes específicos
        config.setAllowedOrigins(List.of("http://localhost:8080","http://localhost:8085"));


        // Permitir todos los headers
        config.addAllowedHeader("*");

        // Permitir todos los métodos HTTP
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));

        // Exponer headers
        config.setExposedHeaders(Arrays.asList("Authorization", "Content-Type"));

        // Solo habilitar CORS para el endpoint de documentación
        source.registerCorsConfiguration("/openapi.yaml", config);
        return new CorsFilter(source);
    }
}