package com.library.books.security;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Clase para almacenar la información del usuario autenticado
 * extraída del JWT en cada petición.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthContext {
    private String username;
    private String role;
    private Integer userId;
    private Integer userProfileId;

    /**
     * ThreadLocal para almacenar el contexto de autenticación por petición
     */
    private static final ThreadLocal<AuthContext> contextHolder = new ThreadLocal<>();

    public static void setContext(AuthContext context) {
        contextHolder.set(context);
    }

    public static AuthContext getContext() {
        return contextHolder.get();
    }

    public static void clear() {
        contextHolder.remove();
    }

    /**
     * Verifica si el usuario tiene rol de ADMIN
     */
    public boolean isAdmin() {
        return "ADMIN".equals(role);
    }

    /**
     * Verifica si el usuario tiene rol de USER
     */
    public boolean isUser() {
        return "USER".equals(role);
    }
}