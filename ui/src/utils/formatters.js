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