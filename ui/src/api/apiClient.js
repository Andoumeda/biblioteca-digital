import axios from 'axios';

// Base URLs para cada microservicio
const API_URLS = {
  users: import.meta.env.VITE_API_USERS_URL || 'http://localhost:8081',
  publications: import.meta.env.VITE_API_PUBLICATIONS_URL || 'http://localhost:8082',
  books: import.meta.env.VITE_API_BOOKS_URL || 'http://localhost:8083',
};

// Cliente por defecto (Publications)
const apiClient = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || API_URLS.publications,
  headers: {
    'Content-Type': 'application/json',
  },
  timeout: 10000,
});

// Cliente para Books
const booksApiClient = axios.create({
  baseURL: API_URLS.books,
  headers: {
    'Content-Type': 'application/json',
  },
  timeout: 10000,
});

// Cliente para Users
const usersApiClient = axios.create({
  baseURL: API_URLS.users,
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

apiClient.interceptors.response.use((response) => response, errorInterceptor);
booksApiClient.interceptors.response.use((response) => response, errorInterceptor);
usersApiClient.interceptors.response.use((response) => response, errorInterceptor);

export default apiClient;
export { booksApiClient, usersApiClient };