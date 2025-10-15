package com.library.publications.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgumentException(
            IllegalArgumentException ex, WebRequest request) {

        HttpStatus status;
        String error;
        String message = ex.getMessage();

        // Determinar el código de estado basado en palabras clave en el mensaje
        if (message.contains("no encontrad") || message.contains("No se encontró")) {
            status = HttpStatus.NOT_FOUND;
            error = "Recurso no encontrado";
        } else if (message.contains("ya existe") || message.contains("duplicad") || message.contains("ya está")) {
            status = HttpStatus.CONFLICT;
            error = "Recurso duplicado";
        } else if (message.contains("vací") || message.contains("No se encontraron")) {
            status = HttpStatus.NO_CONTENT;
            error = "Resultado vacío";
        } else {
            status = HttpStatus.BAD_REQUEST;
            error = "Solicitud incorrecta";
        }

        Map<String, Object> errorDetails = new HashMap<>();
        errorDetails.put("path", request.getDescription(false).replace("uri=", ""));
        errorDetails.put("error", error);
        errorDetails.put("message", message);
        errorDetails.put("timestamp", LocalDateTime.now());
        errorDetails.put("status", status.value());

        return new ResponseEntity<>(errorDetails, status);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationExceptions(
            MethodArgumentNotValidException ex, WebRequest request) {
        Map<String, Object> errorDetails = new HashMap<>();
        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage())
        );

        errorDetails.put("path", request.getDescription(false).replace("uri=", ""));
        errorDetails.put("error", "Validación fallida");
        errorDetails.put("errors", errors);
        errorDetails.put("message", "Datos de entrada no válidos");
        errorDetails.put("timestamp", LocalDateTime.now());
        errorDetails.put("status", HttpStatus.BAD_REQUEST.value());

        return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGlobalException(
            Exception ex, WebRequest request) {
        Map<String, Object> errorDetails = new HashMap<>();
        errorDetails.put("path", request.getDescription(false).replace("uri=", ""));
        errorDetails.put("error", "Error interno del servidor");
        errorDetails.put("message", ex.getMessage());
        errorDetails.put("timestamp", LocalDateTime.now());
        errorDetails.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());

        return new ResponseEntity<>(errorDetails, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}