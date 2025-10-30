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

// Interceptor para manejar errores globalmente en todos los clientes
const errorInterceptor = (error) => {
  console.error('API Error:', error);
  return Promise.reject(error);
};

publicationsApiClient.interceptors.response.use((response) => response, errorInterceptor);
booksApiClient.interceptors.response.use((response) => response, errorInterceptor);
usersApiClient.interceptors.response.use((response) => response, errorInterceptor);

export { publicationsApiClient, booksApiClient, usersApiClient };