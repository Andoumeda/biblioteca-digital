import { defineStore } from 'pinia';
import { announcementsAPI } from '../api/usersService';

export const useAnnouncementsStore = defineStore('announcements', {
  state: () => ({
    announcements: [],
    currentPage: 0,
    pageSize: 10,
    totalPages: 0,
    totalItems: 0,
    loading: false,
    error: null,
  }),

  getters: {
    hasMore: (state) => state.currentPage < state.totalPages - 1,
  },

  actions: {
    async fetchAnnouncements(page = 0, size = 10) {
      this.loading = true;
      this.error = null;

      try {
        const response = await announcementsAPI.getAll(page, size);

        if (response.data) {
          this.announcements = response.data.data || [];
          this.totalItems = response.data.totalItems || 0;
          this.totalPages = response.data.totalPages || 0;
          this.currentPage = page;
          this.pageSize = size;
        }
      } catch (error) {
        this.error = error.message || 'Error al cargar anuncios';
        console.error('Error fetching announcements:', error);
      } finally {
        this.loading = false;
      }
    },

    async fetchAnnouncementById(id) {
      this.loading = true;
      this.error = null;

      try {
        const response = await announcementsAPI.getById(id);
        return response.data;
      } catch (error) {
        this.error = error.message || 'Error al cargar anuncio';
        console.error('Error fetching announcement:', error);
        throw error;
      } finally {
        this.loading = false;
      }
    },

    async createAnnouncement(announcementData) {
      this.loading = true;
      this.error = null;

      try {
        const response = await announcementsAPI.create(announcementData);
        await this.fetchAnnouncements(this.currentPage, this.pageSize);
        return response.data;
      } catch (error) {
        this.error = error.message || 'Error al crear anuncio';
        console.error('Error creating announcement:', error);
        throw error;
      } finally {
        this.loading = false;
      }
    },

    async updateAnnouncement(id, announcementData) {
      this.loading = true;
      this.error = null;

      try {
        const response = await announcementsAPI.update(id, announcementData);
        await this.fetchAnnouncements(this.currentPage, this.pageSize);
        return response.data;
      } catch (error) {
        this.error = error.message || 'Error al actualizar anuncio';
        console.error('Error updating announcement:', error);
        throw error;
      } finally {
        this.loading = false;
      }
    },

    async deleteAnnouncement(id) {
      this.loading = true;
      this.error = null;

      try {
        await announcementsAPI.delete(id);
        await this.fetchAnnouncements(this.currentPage, this.pageSize);
      } catch (error) {
        this.error = error.message || 'Error al eliminar anuncio';
        console.error('Error deleting announcement:', error);
        throw error;
      } finally {
        this.loading = false;
      }
    },

    nextPage() {
      if (this.hasMore) {
        this.fetchAnnouncements(this.currentPage + 1, this.pageSize);
      }
    },

    previousPage() {
      if (this.currentPage > 0) {
        this.fetchAnnouncements(this.currentPage - 1, this.pageSize);
      }
    },
  },
});

