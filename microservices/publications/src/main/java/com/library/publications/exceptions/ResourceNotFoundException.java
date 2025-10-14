package com.library.publications.exceptions;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String resource, String field, Object value) {
        super(String.format("%s no encontrado/a con %s: %s", resource, field, value));
    }
}