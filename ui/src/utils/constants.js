// Constantes de la aplicación

// ID del usuario actual (hardcodeado)
export const CURRENT_USER_ID = 1;
export const CURRENT_USER_PROFILE_ID = 1;

// Imagen por defecto
export const DEFAULT_BOOK_COVER = '/programming-book-cover.jpg';

// Estados de publicación
export const PUBLICATION_STATES = {
  APPROVED: 'APPROVED',
  PENDING: 'PENDING',
  REJECTED: 'REJECTED'
};

// Configuración de paginación
export const PAGINATION = {
  DEFAULT_PAGE: 0,
  DEFAULT_SIZE: 20,
  MAX_SIZE: 100
};

// Timeouts
export const TIMEOUTS = {
  SEARCH_DEBOUNCE: 500, // ms
  API_TIMEOUT: 10000, // ms
  POLL_INTERVAL: 30000 // ms
};

// Mensajes de error
export const ERROR_MESSAGES = {
  NETWORK_ERROR: 'Error de conexión. Por favor, verifica tu conexión a internet.',
  SERVER_ERROR: 'Error del servidor. Por favor, intenta más tarde.',
  NOT_FOUND: 'El recurso solicitado no fue encontrado.',
  UNAUTHORIZED: 'No tienes autorización para realizar esta acción.',
  VALIDATION_ERROR: 'Los datos proporcionados no son válidos.'
};

// URLs de la API
export const API_ENDPOINTS = {
  PUBLICATIONS: '/publications',
  CATEGORIES: '/categories',
  FAVORITES: '/favorites',
  USERS: '/users'
};

// Configuración de la aplicación
export const APP_CONFIG = {
  NAME: 'DigiBooks Source',
  VERSION: '1.0.0',
  DESCRIPTION: 'Biblioteca Digital'
};
// Utilidades para formatear datos de la API

/**
 * Formatea una fecha ISO a formato legible en español
 */
export const formatDate = (dateString) => {
  if (!dateString) return 'Fecha desconocida';

  const date = new Date(dateString);
  return new Intl.DateTimeFormat('es-ES', {
    year: 'numeric',
    month: 'long',
    day: 'numeric',
  }).format(date);
};

/**
 * Formatea una fecha con hora
 */
export const formatDateTime = (dateString) => {
  if (!dateString) return 'Fecha desconocida';

  const date = new Date(dateString);
  return new Intl.DateTimeFormat('es-ES', {
    year: 'numeric',
    month: 'long',
    day: 'numeric',
    hour: '2-digit',
    minute: '2-digit'
  }).format(date);
};

/**
 * Trunca texto a una longitud máxima
 */
export const truncate = (text, maxLength = 100) => {
  if (!text) return '';
  return text.length > maxLength
    ? text.substring(0, maxLength) + '...'
    : text;
};

/**
 * Obtiene el label del estado de una publicación
 */
export const getStateLabel = (state) => {
  const labels = {
    APPROVED: '✅ Aprobada',
    PENDING: '⏳ Pendiente',
    REJECTED: '❌ Rechazada'
  };
  return labels[state] || state;
};

/**
 * Obtiene la clase CSS para el estado de una publicación
 */
export const getStateClass = (state) => {
  const classes = {
    APPROVED: 'state-approved',
    PENDING: 'state-pending',
    REJECTED: 'state-rejected'
  };
  return classes[state] || '';
};

/**
 * Formatea un número con separadores de miles
 */
export const formatNumber = (num) => {
  if (!num) return '0';
  return new Intl.NumberFormat('es-ES').format(num);
};

/**
 * Calcula el tiempo relativo (hace 5 minutos, hace 2 días, etc.)
 */
export const getRelativeTime = (dateString) => {
  if (!dateString) return '';

  const date = new Date(dateString);
  const now = new Date();
  const seconds = Math.floor((now - date) / 1000);

  if (seconds < 60) return 'hace un momento';
  if (seconds < 3600) return `hace ${Math.floor(seconds / 60)} minutos`;
  if (seconds < 86400) return `hace ${Math.floor(seconds / 3600)} horas`;
  if (seconds < 604800) return `hace ${Math.floor(seconds / 86400)} días`;

  return formatDate(dateString);
};

/**
 * Valida una URL
 */
export const isValidUrl = (string) => {
  try {
    new URL(string);
    return true;
  } catch (_) {
    return false;
  }
};

/**
 * Genera un color aleatorio basado en un string (útil para avatares)
 */
export const stringToColor = (str) => {
  let hash = 0;
  for (let i = 0; i < str.length; i++) {
    hash = str.charCodeAt(i) + ((hash << 5) - hash);
  }

  const h = hash % 360;
  return `hsl(${h}, 65%, 50%)`;
};

/**
 * Debounce function para búsquedas
 */
export const debounce = (func, wait = 500) => {
  let timeout;
  return function executedFunction(...args) {
    const later = () => {
      clearTimeout(timeout);
      func(...args);
    };
    clearTimeout(timeout);
    timeout = setTimeout(later, wait);
  };
};