import { booksApiClient } from './apiClient';

export const booksAPI = {
  // Obtener libros con filtros (título, publicationId, authorId)
  getBooksByFilters(filters = {}, page = 0, size = 20) {
    const params = new URLSearchParams();
    if (filters.title) params.append('title', filters.title);
    if (filters.publicationId) params.append('publicationId', filters.publicationId);
    if (filters.authorId) params.append('authorId', filters.authorId);
    params.append('page', page);
    params.append('size', size);

    return booksApiClient.get(`/api/books?${params.toString()}`);
  },

  // Obtener libro por ID
  getBookById(id) {
    return booksApiClient.get(`/api/books/${id}`);
  },

  // Crear nuevo libro
  createBook(bookData) {
    return booksApiClient.post('/api/books', bookData);
  },

  // Actualizar libro
  updateBook(id, bookData) {
    return booksApiClient.put(`/api/books/${id}`, bookData);
  },

  // Eliminar libro
  deleteBook(id) {
    return booksApiClient.delete(`/api/books/${id}`);
  },

  // Obtener libros por publicación
  getBooksByPublication(publicationId, page = 0, size = 20) {
    return this.getBooksByFilters({ publicationId }, page, size);
  },

  // Obtener libros por autor
  getBooksByAuthor(authorId, page = 0, size = 20) {
    return this.getBooksByFilters({ authorId }, page, size);
  }
};

export const ratingsAPI = {
  // Obtener ratings con filtros
  getRatingsByFilters(filters = {}, page = 0, size = 20) {
    const params = new URLSearchParams();
    if (filters.bookId) params.append('bookId', filters.bookId);
    if (filters.userProfileId) params.append('userProfileId', filters.userProfileId);
    if (filters.min) params.append('min', filters.min);
    if (filters.max) params.append('max', filters.max);
    params.append('page', page);
    params.append('size', size);

    return booksApiClient.get(`/api/ratings?${params.toString()}`);
  },

  // Obtener rating por ID
  getRatingById(id) {
    return booksApiClient.get(`/api/ratings/${id}`);
  },

  // Crear rating
  createRating(ratingData) {
    return booksApiClient.post('/api/ratings', ratingData);
  },

  // Actualizar rating
  updateRating(id, ratingData) {
    return booksApiClient.put(`/api/ratings/${id}`, ratingData);
  },

  // Eliminar rating
  deleteRating(id) {
    return booksApiClient.delete(`/api/ratings/${id}`);
  },

  // Obtener ratings de un libro
  getRatingsByBook(bookId, page = 0, size = 20) {
    return this.getRatingsByFilters({ bookId }, page, size);
  },

  // Obtener promedio de rating de un libro
  async getBookAverageRating(bookId) {
    try {
      const response = await this.getRatingsByBook(bookId, 0, 1000);
      const ratings = response.data.data || [];

      if (ratings.length === 0) {
        return { average: 0, count: 0 };
      }

      const sum = ratings.reduce((acc, rating) => acc + (rating.score || 0), 0);
      const average = sum / ratings.length;

      return {
        average: parseFloat(average.toFixed(1)),
        count: ratings.length
      };
    } catch (error) {
      console.error(`Error getting average rating for book ${bookId}:`, error);
      return { average: 0, count: 0 };
    }
  }
};

export const authorsAPI = {
  // Obtener autores (asumiendo endpoint similar)
  getAll(page = 0, size = 20) {
    return booksApiClient.get(`/api/authors?page=${page}&size=${size}`);
  },

  // Obtener autor por ID
  getById(id) {
    return booksApiClient.get(`/api/authors/${id}`);
  },

  // Crear autor
  create(authorData) {
    return booksApiClient.post('/api/authors', authorData);
  },

  // Actualizar autor
  update(id, authorData) {
    return booksApiClient.put(`/api/authors/${id}`, authorData);
  },

  // Eliminar autor
  delete(id) {
    return booksApiClient.delete(`/api/authors/${id}`);
  }
};