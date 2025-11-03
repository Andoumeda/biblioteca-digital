/**
 * Decodifica un token JWT y retorna su payload
 * @param {string} token - Token JWT a decodificar
 * @returns {Object|null} Payload del token o null si hay error
 */
export function parseJwt(token) {
  try {
    const base64Url = token.split('.')[1];
    const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
    const jsonPayload = decodeURIComponent(
      atob(base64)
        .split('')
        .map(c => '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2))
        .join('')
    );
    return JSON.parse(jsonPayload);
  } catch (error) {
    console.error('Error parsing JWT:', error);
    return null;
  }
}

/**
 * Verifica si un token JWT ha expirado
 * @param {string} token - Token JWT a verificar
 * @returns {boolean} true si el token ha expirado
 */
export function isTokenExpired(token) {
  try {
    const decoded = parseJwt(token);
    if (!decoded || !decoded.exp) {
      return true;
    }
    const now = Date.now() / 1000;
    return now >= decoded.exp;
  } catch (error) {
    return true;
  }
}

/**
 * Extrae el username del token JWT
 * @param {string} token - Token JWT
 * @returns {string|null} Username o null
 */
export function getUsernameFromToken(token) {
  const decoded = parseJwt(token);
  return decoded?.sub || null;
}

/**
 * Extrae el rol del token JWT
 * @param {string} token - Token JWT
 * @returns {string|null} Rol del usuario o null
 */
export function getRoleFromToken(token) {
  const decoded = parseJwt(token);
  return decoded?.role || null;
}

/**
 * Extrae el userId del token JWT
 * @param {string} token - Token JWT
 * @returns {number|null} User ID o null
 */
export function getUserIdFromToken(token) {
  const decoded = parseJwt(token);
  return decoded?.userId || null;
}