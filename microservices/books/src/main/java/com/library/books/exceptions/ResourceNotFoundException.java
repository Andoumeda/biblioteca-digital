package com.library.books.exceptions;

public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String resourceName, String fieldName, Object fieldValue) {
        super(String.format("%s no encontrada con %s : '%s'", resourceName, fieldName, fieldValue));
    }
}
