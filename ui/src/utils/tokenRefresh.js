import { authAPI } from '../api/authService';
import { parseJwt } from './jwtUtils';

let refreshTimer = null;

/**
 * Calcula cuándo debe refrescarse el token (cada 20 segundos)
 * @param {string} token - Token JWT
 * @returns {number} Milisegundos hasta el refresh (20000ms = 20 segundos)
 */
function getRefreshTimeout(token) {
  try {
    const decoded = parseJwt(token);
    if (!decoded || !decoded.exp) {
      return null;
    }

    const now = Date.now() / 1000;
    const expiresIn = decoded.exp - now;

    // Refrescar cada 20 segundos, independientemente del tiempo de expiración
    const REFRESH_INTERVAL = 20; // segundos

    // Si el token expira en menos de 20 segundos, refrescar inmediatamente
    if (expiresIn < REFRESH_INTERVAL) {
      console.log(`Token expira en ${expiresIn.toFixed(1)} segundos. Refrescando inmediatamente.`);
      return 1000; // 1 segundo de margen
    }

    console.log(`Token expira en ${expiresIn.toFixed(1)} segundos. Se refrescará en ${REFRESH_INTERVAL} segundos.`);

    return REFRESH_INTERVAL * 1000; // 20 segundos en milisegundos
  } catch (error) {
    console.error('Error calculando tiempo de refresh:', error);
    return null;
  }
}

/**
 * Programa el auto-refresh del token
 * @param {string} token - Token JWT actual
 * @param {Function} onRefresh - Callback cuando se refresca el token
 */
export function scheduleTokenRefresh(token, onRefresh) {
  // Limpiar timer anterior si existe
  if (refreshTimer) {
    clearTimeout(refreshTimer);
    refreshTimer = null;
  }

  const timeout = getRefreshTimeout(token);

  if (!timeout || timeout <= 0) {
    console.warn('Token inválido o ya expirado, no se puede programar refresh');
    return;
  }

  // Programar el refresh
  refreshTimer = setTimeout(async () => {
    try {
      console.log('Refrescando token automáticamente...');
      const response = await authAPI.refreshToken(token);
      const newToken = response.data.accessToken;

      // Guardar el nuevo token
      localStorage.setItem('authToken', newToken);

      // Llamar al callback si existe
      if (onRefresh) {
        onRefresh(newToken);
      }

      console.log('Token refrescado exitosamente de forma proactiva');

      // Programar el siguiente refresh
      scheduleTokenRefresh(newToken, onRefresh);

    } catch (error) {
      console.error('Error al refrescar token proactivamente:', error);
      // Si falla, el interceptor de axios manejará el siguiente intento
    }
  }, timeout);
}

/**
 * Cancela el auto-refresh programado
 */
export function cancelTokenRefresh() {
  if (refreshTimer) {
    clearTimeout(refreshTimer);
    refreshTimer = null;
    console.log('Auto-refresh de token cancelado');
  }
}

/**
 * Verifica si el token está próximo a expirar (menos del 30% de vida restante)
 * @param {string} token - Token JWT
 * @returns {boolean} true si está próximo a expirar
 */
export function isTokenExpiringSoon(token) {
  try {
    const decoded = parseJwt(token);
    if (!decoded || !decoded.exp) {
      return true;
    }

    const now = Date.now() / 1000;
    const expiresIn = decoded.exp - now;
    const tokenLifetime = 300; // 5 minutos en segundos (debe coincidir con jwt.expiration)

    // Considera "próximo a expirar" si queda menos del 30% del tiempo
    return (expiresIn / tokenLifetime) < 0.3;
  } catch (error) {
    return true;
  }
}