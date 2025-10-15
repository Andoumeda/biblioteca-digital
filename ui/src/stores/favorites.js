import { defineStore } from 'pinia';
import { favoritesAPI } from '../api/publicationsService';

export const useFavoritesStore = defineStore('favorites', {
  state: () => ({
    favorites: [],
    favoritesCount: {},
    loading: false,
    error: null,
  }),

  getters: {
    getFavoriteCountByPublication: (state) => (publicationId) => {
      return state.favoritesCount[publicationId] || 0;
    },

    isPublicationFavorited: (state) => (publicationId, userId) => {
      return state.favorites.some(
        fav => fav.publicationId === publicationId && fav.userProfileId === userId
      );
    }
  },

  actions: {
    async fetchFavorites() {
      this.loading = true;
      this.error = null;

      try {
        const response = await favoritesAPI.getAllFavorites();
        this.favorites = response.data || [];

        // Calcular el conteo de favoritos por publicación
        this.favoritesCount = {};
        this.favorites.forEach(fav => {
          if (fav.publicationId) {
            this.favoritesCount[fav.publicationId] =
              (this.favoritesCount[fav.publicationId] || 0) + 1;
          }
        });
      } catch (error) {
        this.error = error.message || 'Error al cargar favoritos';
        console.error('Error fetching favorites:', error);
      } finally {
        this.loading = false;
      }
    },

    async addFavorite(publicationId, userProfileId) {
      try {
        const response = await favoritesAPI.createFavorite({
          publicationId,
          userProfileId
        });

        // Actualizar el estado local
        this.favorites.push(response.data);
        this.favoritesCount[publicationId] =
          (this.favoritesCount[publicationId] || 0) + 1;

        return response.data;
      } catch (error) {
        this.error = error.message || 'Error al agregar favorito';
        console.error('Error adding favorite:', error);
        throw error;
      }
    },

    async removeFavorite(favoriteId, publicationId) {
      try {
        await favoritesAPI.deleteFavorite(favoriteId);

        // Actualizar el estado local
        this.favorites = this.favorites.filter(fav => fav.id !== favoriteId);
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