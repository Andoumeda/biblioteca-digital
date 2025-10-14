// Funciones de validación para formularios

/**
 * Valida que un campo no esté vacío
 */
export const required = (value) => {
  if (typeof value === 'string') {
    return value.trim().length > 0;
  }
  return value !== null && value !== undefined;
};

/**
 * Valida longitud mínima
 */
export const minLength = (min) => (value) => {
  if (!value) return false;
  return value.length >= min;
};

/**
 * Valida longitud máxima
 */
export const maxLength = (max) => (value) => {
  if (!value) return true;
  return value.length <= max;
};

/**
 * Valida formato de email
 */
export const email = (value) => {
  if (!value) return false;
  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
  return emailRegex.test(value);
};

/**
 * Valida que sea un número
 */
export const isNumber = (value) => {
  return !isNaN(parseFloat(value)) && isFinite(value);
};

/**
 * Valida que sea un número positivo
 */
export const positiveNumber = (value) => {
  return isNumber(value) && parseFloat(value) > 0;
};

/**
 * Valida rango de números
 */
export const numberRange = (min, max) => (value) => {
  if (!isNumber(value)) return false;
  const num = parseFloat(value);
  return num >= min && num <= max;
};

/**
 * Valida URL
 */
export const url = (value) => {
  if (!value) return false;
  try {
    new URL(value);
    return true;
  } catch {
    return false;
  }
};

/**
 * Ejecuta múltiples validaciones
 */
export const validate = (value, validators) => {
  for (const validator of validators) {
    if (!validator(value)) {
      return false;
    }
  }
  return true;
};

/**
 * Validaciones específicas para publicaciones
 */
export const publicationValidators = {
  title: (value) => {
    if (!required(value)) return 'El título es requerido';
    if (!minLength(3)(value)) return 'El título debe tener al menos 3 caracteres';
    if (!maxLength(200)(value)) return 'El título no puede exceder 200 caracteres';
    return null;
  },

  description: (value) => {
    if (!maxLength(1000)(value)) return 'La descripción no puede exceder 1000 caracteres';
    return null;
  },

  userProfileId: (value) => {
    if (!required(value)) return 'El ID de usuario es requerido';
    if (!positiveNumber(value)) return 'El ID de usuario debe ser un número positivo';
    return null;
  }
};