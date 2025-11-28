import axios from 'axios';

const API_URL = import.meta.env.VITE_API_GATEWAY_URL;

// Cliente para Publications
const publicationsApiClient = axios.create({
  baseURL: API_URL,
  headers: {
    'Content-Type': 'application/json',
  },
  timeout: 10000,
});

// Cliente para Books
const booksApiClient = axios.create({
  baseURL: API_URL,
  headers: {
    'Content-Type': 'application/json',
  },
  timeout: 10000,
});

// Cliente para Users
const usersApiClient = axios.create({
  baseURL: API_URL,
  headers: {
    'Content-Type': 'application/json',
  },
  timeout: 10000,
});

// Interceptor para agregar el token JWT a todas las peticiones
const requestInterceptor = (config) => {
  const token = localStorage.getItem('authToken');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
};

// Variable para evitar múltiples intentos de refresh simultáneos
let isRefreshing = false;
let failedQueue = [];

const processQueue = (error, token = null) => {
  failedQueue.forEach(prom => {
    if (error) {
      prom.reject(error);
    } else {
      prom.resolve(token);
    }
  });
  failedQueue = [];
};

// Interceptor para manejar errores globalmente con auto-refresh
const errorInterceptor = async (error) => {
  const originalRequest = error.config;

  // Si el token ha expirado o no es válido (401) y no hemos intentado refrescar aún
  if (error.response?.status === 401 && !originalRequest._retry) {

    // Si ya está en proceso de refresh, agregar a la cola
    if (isRefreshing) {
      return new Promise((resolve, reject) => {
        failedQueue.push({ resolve, reject });
      }).then(token => {
        originalRequest.headers['Authorization'] = 'Bearer ' + token;
        return axios(originalRequest);
      }).catch(err => {
        return Promise.reject(err);
      });
    }

    originalRequest._retry = true;
    isRefreshing = true;

    const token = localStorage.getItem('authToken');

    if (!token) {
      // No hay token, redirigir al login
      if (window.location.pathname !== '/login') {
        window.location.href = '/login';
      }
      return Promise.reject(error);
    }

    try {
      // Intentar refrescar el token
      const response = await axios.post(`${API_URL}/auth/refresh`, null, {
        headers: {
          Authorization: `Bearer ${token}`
        }
      });

      const newToken = response.data.accessToken;

      // Guardar el nuevo token
      localStorage.setItem('authToken', newToken);

      // Actualizar el header de la petición original
      originalRequest.headers['Authorization'] = 'Bearer ' + newToken;

      // Procesar cola de peticiones fallidas
      processQueue(null, newToken);
      isRefreshing = false;

      console.log('Token refrescado exitosamente');

      // Reintentar la petición original con el nuevo token
      return axios(originalRequest);

    } catch (refreshError) {
      // Si el refresh falla, limpiar y redirigir al login
      console.warn('No se pudo refrescar el token. Redirigiendo al login...');
      processQueue(refreshError, null);
      isRefreshing = false;

      localStorage.removeItem('authToken');
      localStorage.removeItem('currentUser');

      if (window.location.pathname !== '/login') {
        window.location.href = '/login';
      }

      return Promise.reject(refreshError);
    }
  }

  return Promise.reject(error);
};

// Aplicar interceptores a todos los clientes
publicationsApiClient.interceptors.request.use(requestInterceptor);
booksApiClient.interceptors.request.use(requestInterceptor);
usersApiClient.interceptors.request.use(requestInterceptor);

publicationsApiClient.interceptors.response.use((response) => response, errorInterceptor);
booksApiClient.interceptors.response.use((response) => response, errorInterceptor);
usersApiClient.interceptors.response.use((response) => response, errorInterceptor);

export { publicationsApiClient, booksApiClient, usersApiClient };