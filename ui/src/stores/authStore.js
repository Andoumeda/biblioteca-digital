import { defineStore } from 'pinia';
import { authAPI } from '../api/authService';
import { parseJwt, isTokenExpired } from '../utils/jwtUtils';
import { scheduleTokenRefresh, cancelTokenRefresh } from '../utils/tokenRefresh';

export const useAuthStore = defineStore('auth', {
  state: () => ({
    token: localStorage.getItem('authToken') || null,
    // No se guarda user en state, siempre se decodifica del token
    loading: false,
    error: null,
  }),

  getters: {
    /**
     * Decodifica el token actual y retorna el usuario
     */
    user: (state) => {
      if (!state.token) return null;
      return parseJwt(state.token);
    },

    /**
     * Verifica si el usuario está autenticado
     */
    isAuthenticated: (state) => {
      if (!state.token) return false;
      return !isTokenExpired(state.token);
    },

    /**
     * Retorna el username del usuario actual
     */
    currentUsername() {
      return this.user?.sub || null;
    },

    /**
     * Retorna el userId del usuario actual
     */
    currentUserId() {
      return this.user?.userId || null;
    },

    /**
     * Retorna el userProfileId del usuario actual
     */
    currentUserProfileId() {
      return this.user?.userProfileId || null;
    },

    /**
     * Retorna el rol del usuario actual
     */
    userRole() {
      return this.user?.role || null;
    },

    /**
     * Verifica si el usuario tiene rol ADMIN
     */
    isAdmin() {
      return this.user?.role === 'ADMIN';
    },

    /**
     * Verifica si el usuario tiene rol USER
     */
    isUser() {
      return this.user?.role === 'USER';
    },

    /**
     * Retorna el email del usuario (si está en el token)
     */
    userEmail() {
      return this.user?.email || this.user?.sub || null;
    },

    /**
     * Retorna el display name del usuario (si está en el token)
     */
    displayName() {
      return this.user?.displayName || this.user?.name || null;
    },
  },

  actions: {
    /**
     * Inicia sesión con username y contraseña
     */
    async login(username, password) {
      this.loading = true;
      this.error = null;

      try {
        const response = await authAPI.login(username, password);
        const { accessToken } = response.data;

        if (!accessToken) {
          throw new Error('No se recibió token del servidor');
        }

        this.setToken(accessToken);
        return { success: true, user: this.user };
      } catch (error) {
        console.error('Error en login:', error);
        this.error = error.response?.data?.message || 'Error al iniciar sesión';
        return { success: false, error: this.error };
      } finally {
        this.loading = false;
      }
    },

    /**
     * Registra un nuevo usuario
     */
    async register(userData) {
      this.loading = true;
      this.error = null;

      try {
        const response = await authAPI.register(userData);
        const { accessToken } = response.data;

        if (!accessToken) {
          throw new Error('No se recibió token del servidor');
        }

        this.setToken(accessToken);
        return { success: true, user: this.user };
      } catch (error) {
        console.error('Error en registro:', error);
        this.error = error.response?.data?.message || 'Error al registrar usuario';
        return { success: false, error: this.error };
      } finally {
        this.loading = false;
      }
    },

    /**
     * Establece el token y decodifica la información del usuario
     */
    setToken(token) {
      this.token = token;

      // Solo guardamos el token - el user se decodifica automáticamente del getter
      localStorage.setItem('authToken', token);

      // Eliminamos currentUser si existe (migración)
      localStorage.removeItem('currentUser');

      // Programar auto-refresh del token
      scheduleTokenRefresh(token, (newToken) => {
        // Callback cuando se refresca el token
        this.token = newToken;
        console.log('Token actualizado en el store');
      });
    },

    /**
     * Cierra sesión y limpia el estado
     */
    async logout() {
      try {
        if (this.token) {
          // Intentar cerrar sesión en el servidor (opcional)
          await authAPI.logout(this.token).catch(() => {
            // Ignorar errores del servidor en logout
          });
        }
      } finally {
        this.clearAuth();
      }
    },

    /**
     * Limpia toda la información de autenticación
     */
    clearAuth() {
      this.token = null;
      this.error = null;

      // Cancelar auto-refresh programado
      cancelTokenRefresh();

      localStorage.removeItem('authToken');
      localStorage.removeItem('currentUser'); // Por si quedó de versiones anteriores
    },

    /**
     * Verifica si el token ha expirado y limpia si es necesario
     */
    checkTokenExpiration() {
      if (this.token && isTokenExpired(this.token)) {
        console.warn('Token expirado, cerrando sesión...');
        this.clearAuth();
        return true;
      }
      return false;
    },

    /**
     * Inicializa el store desde localStorage
     */
    initializeAuth() {
      const token = localStorage.getItem('authToken');

      if (token) {
        try {
          this.token = token;

          // Limpiar currentUser si existe (migración de versiones anteriores)
          localStorage.removeItem('currentUser');

          // Verificar si el token está expirado
          const isValid = !this.checkTokenExpiration();

          // Si el token es válido, programar auto-refresh
          if (isValid) {
            scheduleTokenRefresh(token, (newToken) => {
              this.token = newToken;
              console.log('Token actualizado en el store');
            });
          }

          return isValid;

        } catch (error) {
          console.error('Error inicializando autenticación:', error);
          this.clearAuth();
          return false;
        }
      }

      return false;
    },
  },
});