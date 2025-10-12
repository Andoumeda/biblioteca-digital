import { defineStore } from 'pinia';
import { publicationsAPI, categoriesAPI } from '../api/publicationsService';

export const usePublicationsStore = defineStore('publications', {
  state: () => ({
    publications: [],
    categories: [],
    currentPage: 0,
    pageSize: 20,
    totalPages: 0,
    totalItems: 0,
    loading: false,
    error: null,
    searchQuery: '',
    selectedCategory: null,
    filter: 'all', // 'all', 'approved', 'pending'
  }),

  getters: {
    // Filtrar publicaciones según el filtro activo
    filteredPublications: (state) => {
      let filtered = state.publications;

      // Filtrar por query de búsqueda
      if (state.searchQuery) {
        const query = state.searchQuery.toLowerCase();
        filtered = filtered.filter((pub) =>
          pub.title?.toLowerCase().includes(query) ||
          pub.description?.toLowerCase().includes(query) ||
          pub.userProfile?.user?.username?.toLowerCase().includes(query)
        );
      }

      // Filtrar por categoría
      if (state.selectedCategory) {
        filtered = filtered.filter((pub) =>
          pub.categories?.some((cat) => cat.id === state.selectedCategory)
        );
      }

      return filtered;
    },

    hasMore: (state) => state.currentPage < state.totalPages - 1,
  },

  actions: {
    async fetchPublications(page = 0, size = 20) {
      this.loading = true;
      this.error = null;

      try {
        const response = await publicationsAPI.getPublications(page, size);
        console.log('API Response:', response.data); // Debug

        // La API devuelve { data: [...], pageSize, totalItems, currentPage, totalPages }
        this.publications = response.data.data || [];
        this.currentPage = response.data.currentPage || 0;
        this.pageSize = response.data.pageSize || size;
        this.totalPages = response.data.totalPages || 0;
        this.totalElements = response.data.totalItems || 0;
      } catch (error) {
        this.error = error.message || 'Error al cargar las publicaciones';
        console.error('Error fetching publications:', error);
      } finally {
        this.loading = false;
      }
    },

    async searchPublications(query, page = 0, size = 20) {
      this.loading = true;
      this.error = null;
      this.searchQuery = query;

      try {
        const response = await publicationsAPI.searchByTitle(query, page, size);

        this.publications = response.data.data || [];
        this.currentPage = response.data.currentPage || 0;
        this.pageSize = response.data.pageSize || size;
        this.totalPages = response.data.totalPages || 0;
        this.totalElements = response.data.totalItems || 0;
      } catch (error) {
        this.error = error.message || 'Error al buscar publicaciones';
        console.error('Error searching publications:', error);
      } finally {
        this.loading = false;
      }
    },

    async fetchPublicationsByState(state, page = 0, size = 20) {
      this.loading = true;
      this.error = null;

      try {
        const response = await publicationsAPI.getByState(state, page, size);

        this.publications = response.data.data || [];
        this.currentPage = response.data.currentPage || 0;
        this.pageSize = response.data.pageSize || size;
        this.totalPages = response.data.totalPages || 0;
        this.totalElements = response.data.totalItems || 0;
      } catch (error) {
        this.error = error.message || 'Error al cargar publicaciones por estado';
        console.error('Error fetching publications by state:', error);
      } finally {
        this.loading = false;
      }
    },

    async fetchPublicationsByCategory(categoryId, page = 0, size = 20) {
      this.loading = true;
      this.error = null;
      this.selectedCategory = categoryId;

      try {
        const response = await publicationsAPI.getByCategory(categoryId, page, size);

        this.publications = response.data.data || [];
        this.currentPage = response.data.currentPage || 0;
        this.pageSize = response.data.pageSize || size;
        this.totalPages = response.data.totalPages || 0;
        this.totalItems = response.data.totalItems || 0;
      } catch (error) {
        this.error = error.message || 'Error al cargar publicaciones por categoría';
        console.error('Error fetching publications by category:', error);
      } finally {
        this.loading = false;
      }
    },

    async createPublication(publicationData) {
      this.loading = true;
      this.error = null;

      try {
        const response = await publicationsAPI.createPublication(publicationData);
        // Recargar publicaciones después de crear una nueva
        await this.fetchPublications();
        return response.data;
      } catch (error) {
        this.error = error.message || 'Error al crear publicación';
        console.error('Error creating publication:', error);
        throw error;
      } finally {
        this.loading = false;
      }
    },

    async fetchCategories() {
      try {
        const response = await categoriesAPI.getAll();
        this.categories = response.data || [];
      } catch (error) {
        console.error('Error fetching categories:', error);
      }
    },

    setSearchQuery(query) {
      this.searchQuery = query;
    },

    setFilter(filter) {
      this.filter = filter;

      // Cargar publicaciones según el filtro
      if (filter === 'approved') {
        this.fetchPublicationsByState('APPROVED');
      } else {
        this.fetchPublications();
      }
    },

    clearFilter() {
      this.selectedCategory = null;
      this.searchQuery = '';
      this.filter = 'all';
    },

    nextPage() {
      if (this.hasMore) {
        this.fetchPublications(this.currentPage + 1, this.pageSize);
      }
    },

    previousPage() {
      if (this.currentPage > 0) {
        this.fetchPublications(this.currentPage - 1, this.pageSize);
      }
    },
  },
});