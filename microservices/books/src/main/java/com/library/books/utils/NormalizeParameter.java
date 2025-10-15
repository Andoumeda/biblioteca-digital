package com.library.books.utils;

import java.time.LocalDate;

public class NormalizeParameter {

    /**
     * Normalizar parámetros Integer: convierte valores especiales a null
     * Valores especiales: null o igual a 0
     */
    public static Integer normalizeInteger(Integer value) {
        if (value == null || value == 0) {
            return null;
        }
        return value;
    }

    /**
     * Normalizar parámetros String: convierte valores especiales a null
     * Valores especiales: "-" o cadenas vacías
     */
    public static String normalizeString(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }

        String normalized = value.trim().toLowerCase();

        // Considerar valores especiales como null
        if ("-".equals(normalized)) {
            return null;
        }

        return value.trim();
    }

    /**
     * Normalizar parámetros de fecha: convierte fecha especiale a null
     * Valor especial:"0000-01-01"
     */
    public static LocalDate normalizeDate(LocalDate date) {
        // Si la fecha es 0000-01-01, la consideramos como "sin fecha"
        if (date.getYear() == 0 && date.getMonthValue() == 1 && date.getDayOfMonth() == 1) {
            return null;
        }

        return date;
    }
}
