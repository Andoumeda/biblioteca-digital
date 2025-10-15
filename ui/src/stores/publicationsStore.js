import { defineStore } from 'pinia';
import { publicationsAPI, categoriesAPI, favoritesAPI } from '../api/publicationsService';

export const usePublicationsStore = defineStore('publications', {
  state: () => ({
    publications: [],
    categories: [],
    favoritesCount: {},
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
      this.selectedCategory = null; // Limpiar filtro de categoría
      this.searchQuery = ''; // Limpiar búsqueda

      try {
        const response = await publicationsAPI.getPublications(page, size);

        // La API devuelve { data: [...], pageSize, totalItems, currentPage, totalPages }
        this.publications = response.data.data || [];
        this.currentPage = response.data.currentPage || 0;
        this.pageSize = response.data.pageSize || size;
        this.totalPages = response.data.totalPages || 0;
        this.totalItems = response.data.totalItems || 0;
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
        this.totalItems = response.data.totalItems || 0;
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
      this.searchQuery = ''; // Limpiar búsqueda al cambiar de estado

      try {
        const response = await publicationsAPI.getByState(state, page, size);

        this.publications = response.data.data || [];
        this.currentPage = response.data.currentPage || 0;
        this.pageSize = response.data.pageSize || size;
        this.totalPages = response.data.totalPages || 0;
        this.totalItems = response.data.totalItems || 0;
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
        this.categories = response.data.data || [];
        this.currentPage = response.data.currentPage || 0;
        this.pageSize = response.data.pageSize || size;
        this.totalPages = response.data.totalPages || 0;
        this.totalItems = response.data.totalItems || 0;
      } catch (error) {
        console.error('Error fetching categories:', error);
      }
    },

    // Obtener conteo de favoritos para múltiples publicaciones
    async fetchFavoritesCount(publicationIds) {
      try {
        // Obtener favoritos para cada publicación en paralelo
        const counts = await Promise.all(
          publicationIds.map(async (pubId) => {
            const count = await favoritesAPI.countByPublication(pubId);
            return { pubId, count };
          })
        );

        // Actualizar el estado con los conteos
        counts.forEach(({ pubId, count }) => {
          this.favoritesCount[pubId] = count;
        });
      } catch (error) {
        console.error('Error fetching favorites count:', error);
      }
    },

    // Obtener favoritos para las publicaciones actuales
    async fetchCurrentPublicationsFavorites() {
      const publicationIds = this.publications.map(pub => pub.id);
      if (publicationIds.length > 0) {
        await this.fetchFavoritesCount(publicationIds);
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

    async updatePublicationState(publicationId, newState) {
      try {
        const response = await publicationsAPI.updateState(publicationId, newState.toUpperCase());

        // Actualizar la publicación en el estado local
        const index = this.publications.findIndex(pub => pub.id === publicationId);
        if (index !== -1) {
          this.publications[index].state = newState.toUpperCase();
        }

        return response.data;
      } catch (error) {
        this.error = error.message || 'Error al actualizar el estado de la publicación';
        console.error('Error updating publication state:', error);
        throw error;
      }
    },
  },
});