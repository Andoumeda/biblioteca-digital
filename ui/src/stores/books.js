import { defineStore } from 'pinia';
import { booksAPI, ratingsAPI } from '../api/booksService';

export const useBooksStore = defineStore('books', {
  state: () => ({
    books: [],
    ratings: {},
    loading: false,
    error: null,
    currentPage: 0,
    pageSize: 20,
    totalPages: 0,
    totalItems: 0,
  }),

  getters: {
    getBookById: (state) => (id) => {
      return state.books.find(book => book.id === id);
    },

    getBookRating: (state) => (bookId) => {
      return state.ratings[bookId] || { average: 0, count: 0 };
    }
  },

  actions: {
    async fetchBooks(page = 0, size = 20) {
      this.loading = true;
      this.error = null;

      try {
        const response = await booksAPI.getBooksByFilters({}, page, size);
        this.books = response.data.data || [];
        this.currentPage = response.data.currentPage || 0;
        this.pageSize = response.data.pageSize || size;
        this.totalPages = response.data.totalPages || 0;
        this.totalItems = response.data.totalItems || 0;
      } catch (error) {
        this.error = error.message || 'Error al cargar libros';
        console.error('Error fetching books:', error);
      } finally {
        this.loading = false;
      }
    },

    async fetchBooksByPublication(publicationId, page = 0, size = 20) {
      this.loading = true;
      this.error = null;

      try {
        const response = await booksAPI.getBooksByPublication(publicationId, page, size);
        return response.data.data || [];
      } catch (error) {
        this.error = error.message || 'Error al cargar libros de la publicación';
        console.error('Error fetching books by publication:', error);
        return [];
      } finally {
        this.loading = false;
      }
    },

    async fetchBookRating(bookId) {
      try {
        const rating = await ratingsAPI.getBookAverageRating(bookId);
        this.ratings[bookId] = rating;
        return rating;
      } catch (error) {
        console.error(`Error fetching rating for book ${bookId}:`, error);
        return { average: 0, count: 0 };
      }
    },

    async fetchMultipleBookRatings(bookIds) {
      try {
        const ratings = await Promise.all(
          bookIds.map(id => this.fetchBookRating(id))
        );
        return ratings;
      } catch (error) {
        console.error('Error fetching multiple book ratings:', error);
        return [];
      }
    },

    async createBook(bookData) {
      this.loading = true;
      this.error = null;

      try {
        const response = await booksAPI.createBook(bookData);
        await this.fetchBooks();
        return response.data;
      } catch (error) {
        this.error = error.message || 'Error al crear libro';
        console.error('Error creating book:', error);
        throw error;
      } finally {
        this.loading = false;
      }
    },

    async updateBook(id, bookData) {
      this.loading = true;
      this.error = null;

      try {
        const response = await booksAPI.updateBook(id, bookData);
        await this.fetchBooks();
        return response.data;
      } catch (error) {
        this.error = error.message || 'Error al actualizar libro';
        console.error('Error updating book:', error);
        throw error;
      } finally {
        this.loading = false;
      }
    },

    async deleteBook(id) {
      this.loading = true;
      this.error = null;

      try {
        await booksAPI.deleteBook(id);
        this.books = this.books.filter(book => book.id !== id);
      } catch (error) {
        this.error = error.message || 'Error al eliminar libro';
        console.error('Error deleting book:', error);
        throw error;
      } finally {
        this.loading = false;
      }
    }
  }
});