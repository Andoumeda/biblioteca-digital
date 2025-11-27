import { defineStore } from 'pinia';
import { publicationsAPI, categoriesAPI, favoritesAPI, publicationCategoriesAPI } from '../api/publicationsService';

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
    filteredPublications: (state) => {
      let filtered = state.publications;
      if (state.searchQuery) {
        const query = state.searchQuery.toLowerCase();
        filtered = filtered.filter((pub) =>
          pub.title?.toLowerCase().includes(query) ||
          pub.description?.toLowerCase().includes(query) ||
          pub.userProfile?.user?.username?.toLowerCase().includes(query)
        );
      }
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
      this.selectedCategory = null;
      this.searchQuery = '';
      try {
        const response = await publicationsAPI.getPublications(page, size);
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
    async fetchPublicationsByState(state, page = 0, size = 20) {
      this.loading = true;
      this.error = null;
      this.searchQuery = '';
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
    async fetchPublicationsByCategoryRelevance(categoryId, page = 0) {
      this.loading = true;
      this.error = null;
      this.selectedCategory = categoryId;
      try {
        const response = await publicationCategoriesAPI.getByFilters({ categoryId, page });
        this.publications = response.data.data || [];
        this.currentPage = response.data.currentPage || 0;
        this.pageSize = response.data.pageSize || 20;
        this.totalPages = response.data.totalPages || 0;
        this.totalItems = response.data.totalItems || 0;
      } catch (error) {
        this.error = error.message || 'Error al cargar publicaciones por categoría';
        console.error('Error fetching publications by category relevance:', error);
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
    async fetchCategories() {
      try {
        const response = await categoriesAPI.getAll();
        this.categories = response.data.data || [];
      } catch (error) {
        console.error('Error fetching categories:', error);
      }
    },
    async fetchFavoritesCount(publicationIds) {
      try {
        const counts = await Promise.all(
          publicationIds.map(async (pubId) => {
            const count = await favoritesAPI.countByPublication(pubId);
            return { pubId, count };
          })
        );
        counts.forEach(({ pubId, count }) => {
          this.favoritesCount[pubId] = count;
        });
      } catch (error) {
        console.error('Error fetching favorites count:', error);
      }
    },
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
    async createPublication(publicationData) {
      this.loading = true;
      this.error = null;
      try {
        const response = await publicationsAPI.createPublication(publicationData);
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
    async updatePublicationState(publicationId, newState) {
      try {
        const response = await publicationsAPI.updateState(publicationId, newState.toUpperCase());
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
