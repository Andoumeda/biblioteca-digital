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

// Interceptor para manejar errores globalmente
const errorInterceptor = (error) => {
  console.error('API Error:', error);

  // Si el token ha expirado o no es válido (401), redirigir al login
  if (error.response?.status === 401) {
    console.warn('Token expirado o inválido. Redirigiendo al login...');
    localStorage.removeItem('authToken');
    localStorage.removeItem('currentUser');

    // Redirigir al login solo si no estamos ya en esa ruta
    if (window.location.pathname !== '/login') {
      window.location.href = '/login';
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