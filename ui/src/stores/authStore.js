import { defineStore } from 'pinia';
import { authAPI } from '../api/authService';
import { parseJwt, isTokenExpired } from '../utils/jwtUtils';

export const useAuthStore = defineStore('auth', {
  state: () => ({
    token: localStorage.getItem('authToken') || null,
    user: JSON.parse(localStorage.getItem('currentUser') || 'null'),
    loading: false,
    error: null,
  }),

  getters: {
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
    currentUsername: (state) => state.user?.sub || null,

    /**
     * Retorna el userId del usuario actual
     */
    currentUserId: (state) => state.user?.userId || null,

    /**
     * Retorna el userProfileId del usuario actual
     */
    currentUserProfileId: (state) => state.user?.userProfileId || null,

    /**
     * Retorna el rol del usuario actual
     */
    userRole: (state) => state.user?.role || null,

    /**
     * Verifica si el usuario tiene rol ADMIN
     */
    isAdmin: (state) => {
      return state.user?.role === 'ADMIN';
    },

    /**
     * Verifica si el usuario tiene rol USER
     */
    isUser: (state) => {
      return state.user?.role === 'USER';
    },

    /**
     * Retorna el email del usuario (si está en el token)
     */
    userEmail: (state) => state.user?.email || state.user?.sub || null,

    /**
     * Retorna el display name del usuario (si está en el token)
     */
    displayName: (state) => state.user?.displayName || state.user?.name || null,
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
      this.user = parseJwt(token);

      // Guardar en localStorage
      localStorage.setItem('authToken', token);
      localStorage.setItem('currentUser', JSON.stringify(this.user));
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
      this.user = null;
      this.error = null;

      localStorage.removeItem('authToken');
      localStorage.removeItem('currentUser');
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
      const user = localStorage.getItem('currentUser');

      if (token && user) {
        try {
          this.token = token;
          this.user = JSON.parse(user);

          // Verificar si el token está expirado
          return !this.checkTokenExpiration();

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