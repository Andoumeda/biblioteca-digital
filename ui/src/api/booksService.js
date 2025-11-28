import { booksApiClient } from './apiClient';

export const booksAPI = {
  // Obtener libros con filtros (título, publicationId, authorId)
  getBooksByFilters(filters = {}, page = 0) {
    const publicationId = filters.publicationId || 0;
    const authorId = filters.authorId || 0;
    const title = filters.title || '-';

    return booksApiClient.get(`/books/publication/${publicationId}/author/${authorId}/title/${title}/page/${page}`);
  },

  // Obtener libro por ID
  getBookById(id) {
    return booksApiClient.get(`/books/${id}`);
  },

  // Crear nuevo libro
  createBook(bookData) {
    return booksApiClient.post('/books', bookData);
  },

  // Actualizar libro
  updateBook(id, bookData) {
    return booksApiClient.put(`/books/${id}`, bookData);
  },

  // Eliminar libro
  deleteBook(id) {
    return booksApiClient.delete(`/books/${id}`);
  },

  // Obtener libros por publicación
  getBooksByPublication(publicationId, page = 0) {
    return this.getBooksByFilters({ publicationId }, page);
  },

  // Obtener libros por autor
  getBooksByAuthor(authorId, page = 0) {
    return this.getBooksByFilters({ authorId }, page);
  },

  // Búsqueda por título
  searchByTitle(title, page = 0) {
    return this.getBooksByFilters({ title }, page);
  },

  // Crear relación libro-autor (delega a bookAuthorsAPI)
  createBookAuthor(data) {
    return booksApiClient.post('/book_authors', data);
  }
};

export const ratingsAPI = {
  // Obtener ratings con filtros
  getRatingsByFilters(filters = {}, page = 0) {
    const bookId = filters.bookId || 0;
    const userProfileId = filters.userProfileId || 0;
    const min = filters.min || 0;
    const max = filters.max || 0;

    return booksApiClient.get(`/ratings/book/${bookId}/userprofile/${userProfileId}/valoration/${min}/${max}/page/${page}`);
  },

  // Obtener rating por ID
  getRatingById(id) {
    return booksApiClient.get(`/ratings/${id}`);
  },

  // Crear rating
  createRating(ratingData) {
    return booksApiClient.post('/ratings', ratingData);
  },

  // Actualizar rating
  updateRating(id, ratingData) {
    return booksApiClient.put(`/ratings/${id}`, ratingData);
  },

  // Eliminar rating
  deleteRating(id) {
    return booksApiClient.delete(`/ratings/${id}`);
  },

  // Obtener ratings de un libro
  getRatingsByBook(bookId, page = 0) {
    return this.getRatingsByFilters({ bookId }, page);
  },

  // Obtener ratings de un usuario
  getRatingsByUserProfile(userProfileId, page = 0) {
    return this.getRatingsByFilters({ userProfileId }, page);
  },

  // Obtener ratings por rango de valoración
  getRatingsByRange(min, max, page = 0) {
    return this.getRatingsByFilters({ min, max }, page);
  },

  // Obtener promedio de rating de un libro
  async getBookAverageRating(bookId) {
    try {
      const response = await this.getRatingsByBook(bookId, 0);
      console.log('Ratings response for book', bookId, ':', response.data);

      const ratings = response.data?.data || [];

      if (ratings.length === 0) {
        return { average: 0, count: 0 };
      }

      const sum = ratings.reduce((acc, rating) => acc + (rating.valoration || 0), 0);
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
  // Obtener autores con filtros
  getAuthorsByFilters(filters = {}, page = 0) {
    const bookId = filters.bookId || 0;
    const fullname = filters.fullname || '-';
    const minDate = filters.minDate || '0000-01-01';
    const maxDate = filters.maxDate || '0000-01-01';
    const nationality = filters.nationality || '-';

    return booksApiClient.get(`/authors/book/${bookId}/fullname/${fullname}/birthdate/${minDate}/${maxDate}/nationality/${nationality}/page/${page}`);
  },

  // Obtener autor por ID
  getById(id) {
    return booksApiClient.get(`/authors/${id}`);
  },

  // Crear autor
  create(authorData) {
    return booksApiClient.post('/authors', authorData);
  },

  // Actualizar autor
  update(id, authorData) {
    return booksApiClient.put(`/authors/${id}`, authorData);
  },

  // Eliminar autor
  delete(id) {
    return booksApiClient.delete(`/authors/${id}`);
  },

  // Obtener autores por libro
  getAuthorsByBook(bookId, page = 0) {
    return this.getAuthorsByFilters({ bookId }, page);
  },

  // Buscar autores por nombre
  searchByFullname(fullname, page = 0) {
    return this.getAuthorsByFilters({ fullname }, page);
  },

  // Buscar autores por nacionalidad
  searchByNationality(nationality, page = 0) {
    return this.getAuthorsByFilters({ nationality }, page);
  },

  // Buscar autores por rango de fechas de nacimiento
  searchByBirthDateRange(minDate, maxDate, page = 0) {
    return this.getAuthorsByFilters({ minDate, maxDate }, page);
  },

  // Obtener todos los autores (sin filtros)
  getAll(page = 0) {
    return this.getAuthorsByFilters({}, page);
  }
};

export const bookAuthorsAPI = {
  // Obtener relaciones libro-autor con filtros
  getByFilters({ bookId = 0, authorId = 0, contribution = '-', page = 0 }) {
    return booksApiClient.get(
      `/book_authors/book/${bookId}/author/${authorId}/contribution/${contribution}/page/${page}`
    );
  },

  // Obtener relación por ID
  getById(id) {
    return booksApiClient.get(`/book_authors/${id}`);
  },

  // Crear relación libro-autor
  create(data) {
    return booksApiClient.post('/book_authors', data);
  },

  // Actualizar relación libro-autor
  update(id, data) {
    return booksApiClient.put(`/book_authors/${id}`, data);
  },

  // Eliminar relación libro-autor
  delete(id) {
    return booksApiClient.delete(`/book_authors/${id}`);
  },
};
