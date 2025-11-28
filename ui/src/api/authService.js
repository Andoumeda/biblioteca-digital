import axios from 'axios';

const API_URL = import.meta.env.VITE_API_GATEWAY_URL;

// Cliente de axios para autenticación (sin interceptor de token)
const authClient = axios.create({
  baseURL: API_URL,
  headers: {
    'Content-Type': 'application/json',
  },
  timeout: 10000,
});

export const authAPI = {
  /**
   * Inicia sesión con username y contraseña
   * @param {string} username - Nombre de usuario
   * @param {string} password - Contraseña del usuario
   * @returns {Promise} Respuesta con el token JWT
   */
  login(username, password) {
    return authClient.post('/auth/login', {
      username,
      password
    });
  },

  /**
   * Registra un nuevo usuario
   * @param {Object} userData - Datos del usuario
   * @param {string} userData.username - Nombre de usuario
   * @param {string} userData.email - Email del usuario
   * @param {string} userData.password - Contraseña del usuario
   * @param {string} [userData.displayName] - Nombre a mostrar del usuario
   * @param {string} [userData.bio] - Biografía del usuario
   * @param {string} [userData.profilePicture] - URL de imagen de perfil
   * @returns {Promise} Respuesta con el token JWT
   */
  register(userData) {
    return authClient.post('/auth/register', {
      username: userData.username,
      email: userData.email,
      password: userData.password,
      displayName: userData.displayName || userData.username,
      bio: userData.bio || '',
      profilePicture: userData.profilePicture || ''
    });
  },

  /**
   * Verifica si un token es válido
   * @param {string} token - Token JWT a verificar
   * @returns {Promise} Respuesta indicando si el token es válido
   */
  validateToken(token) {
    return authClient.post('/auth/validate', null, {
      headers: {
        Authorization: `Bearer ${token}`
      }
    });
  },

  /**
   * Cierra sesión (invalida el token en el servidor si es necesario)
   * @param {string} token - Token JWT
   * @returns {Promise} Respuesta del logout
   */
  logout(token) {
    return authClient.post('/auth/logout', null, {
      headers: {
        Authorization: `Bearer ${token}`
      }
    });
  },

  /**
   * Refresca el token JWT actual
   * @param {string} token - Token JWT actual
   * @returns {Promise} Respuesta con el nuevo token JWT
   */
  refreshToken(token) {
    return authClient.post('/auth/refresh', null, {
      headers: {
        Authorization: `Bearer ${token}`
      }
    });
  }
};