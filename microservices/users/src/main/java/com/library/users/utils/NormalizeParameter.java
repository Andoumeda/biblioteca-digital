package com.library.users.utils;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;

public class NormalizeParameter {

    public static String normalizeString(String value) {
        if (value == null || "-".equals(value.trim())) {
            return null;
        }
        return value.trim();
    }

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
     * Normalizar parámetros Boolean representados como String: "true", "false", "-"
     * Devuelve Boolean.TRUE, Boolean.FALSE o null (si es "-")
     */
    public static Boolean normalizeBoolean(String value) {
        if (value == null || value.trim().isEmpty() || "-".equals(value.trim())) {
            return null;
        }
        String normalized = value.trim().toLowerCase();
        if ("true".equals(normalized)) {
            return Boolean.TRUE;
        }
        if ("false".equals(normalized)) {
            return Boolean.FALSE;
        }
        // Si el valor no es reconocible, también devolvemos null
        return null;
    }

    /**
     * Normalizar parámetros de fecha: convierte fecha especiale a null
     * Valor especial:"0000-01-01"
     */
    public static LocalDateTime normalizeDateTime(OffsetDateTime date) {
        if (date == null) {
            return null;
        }

        if (date.getYear() == 0 && date.getMonthValue() == 1 && date.getDayOfMonth() == 1 &&
            date.getHour() == 0 && date.getMinute() == 0 && date.getSecond() == 0) {
            return null;
        }

        return date.toLocalDateTime();
    }
}
