import { defineStore } from 'pinia';
import { favoritesAPI } from '../api/publicationsService';

export const useFavoritesStore = defineStore('favorites', {
  state: () => ({
    favorites: [],
    favoritesCount: {},
    totalFavorites: 0,
    loading: false,
    error: null,
  }),

  getters: {
    getFavoriteCountByPublication: (state) => (publicationId) => {
      return state.favoritesCount[publicationId] || 0;
    },

    isPublicationFavorited: (state) => (publicationId, userId) => {
      return state.favorites.some(
        fav => fav.publication?.id === publicationId && fav.userProfile?.userId === userId
      );
    },

    isFavorite: (state) => (id, type = 'publication') => {
      if (type === 'publication') {
        return state.favorites.some(fav => fav.publication?.id === id);
      }
    }
  },

  actions: {
    async fetchFavorites() {
      this.loading = true;
      this.error = null;

      try {
        const response = await favoritesAPI.getAllFavorites(0, 90);

        // La API devuelve una estructura paginada: { data: [], totalItems: X }
        this.favorites = response.data?.data || [];
        this.totalFavorites = response.data?.totalItems || 0;

        // Calcular el conteo de favoritos por publicación
        this.favoritesCount = {};
        this.favorites.forEach(fav => {
          const pubId = fav.publication?.id;
          if (pubId) {
            this.favoritesCount[pubId] = (this.favoritesCount[pubId] || 0) + 1;
          }
        });
      } catch (error) {
        this.error = error.message || 'Error al cargar favoritos';
        console.error('Error fetching favorites:', error);
      } finally {
        this.loading = false;
      }
    },

    async addFavorite(publicationId, type = 'publication') {
      try {
        // Get current user profile ID (you may need to get this from auth store)
        const userProfileId = 1; // TODO: Get from auth store

        const response = await favoritesAPI.createFavorite({
          publicationId,
          userProfileId
        });

        // Actualizar el estado local
        this.favorites.push(response.data);
        this.totalFavorites++;
        this.favoritesCount[publicationId] =
          (this.favoritesCount[publicationId] || 0) + 1;

        return response.data;
      } catch (error) {
        this.error = error.message || 'Error al agregar favorito';
        console.error('Error adding favorite:', error);
        throw error;
      }
    },

    async removeFavorite(publicationId, type = 'publication') {
      try {
        // Find the favorite by publication ID
        const favorite = this.favorites.find(fav => fav.publication?.id === publicationId);

        if (!favorite) {
          console.warn('Favorite not found for publication:', publicationId);
          return;
        }

        await favoritesAPI.deleteFavorite(favorite.id);

        // Actualizar el estado local
        this.favorites = this.favorites.filter(fav => fav.id !== favorite.id);
        this.totalFavorites = Math.max(0, this.totalFavorites - 1);
        if (this.favoritesCount[publicationId] > 0) {
          this.favoritesCount[publicationId]--;
        }
      } catch (error) {
        this.error = error.message || 'Error al eliminar favorito';
        console.error('Error removing favorite:', error);
        throw error;
      }
    }
  }
});