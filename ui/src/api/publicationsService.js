import { publicationsApiClient } from './apiClient';

export const publicationsAPI = {
  // Obtener publicaciones paginadas
  getPublications(page = 0, size = 20) {
    return publicationsApiClient.get(`/publications/page/${page}/size/${size}`);
  },

  // Buscar por título
  searchByTitle(title, page = 0, size = 20) {
    return publicationsApiClient.get(`/publications/title/${title}/page/${page}/size/${size}`);
  },

  // Buscar por descripción
  searchByDescription(description, page = 0, size = 20) {
    return publicationsApiClient.get(`/publications/description/${description}/page/${page}/size/${size}`);
  },

  // Buscar por estado (APPROVED, PENDING, REJECTED)
  getByState(state, page = 0, size = 20) {
    return publicationsApiClient.get(`/publications/state/${state}/page/${page}/size/${size}`);
  },

  // Buscar por usuario
  getByUser(userId, page = 0, size = 20) {
    return publicationsApiClient.get(`/publications/user/${userId}/page/${page}/size/${size}`);
  },

  // Buscar por categoría
  getByCategory(categoryId, page = 0, size = 20) {
    return publicationsApiClient.get(`/publications/category/${categoryId}/page/${page}/size/${size}`);
  },

  // Crear nueva publicación
  createPublication(publicationData) {
    return publicationsApiClient.post('/publications', publicationData);
  },

  // Actualizar estado de publicación
  updateState(publicationId, newState) {
    return publicationsApiClient.put(`/publications/${publicationId}/state/${newState}`);
  },

  // Obtener publicación por ID
  getById(publicationId) {
    return publicationsApiClient.get(`/publications/${publicationId}`);
  },

  // Actualizar publicación
  update(publicationId, publicationData) {
    return publicationsApiClient.put(`/publications/${publicationId}`, publicationData);
  },

  // Eliminar publicación
  delete(publicationId) {
    return publicationsApiClient.delete(`/publications/${publicationId}`);
  },
};

export const categoriesAPI = {
  // Obtener categorías paginadas
  getAll(page = 0, size = 20) {
    return publicationsApiClient.get(`/categories/page/${page}/size/${size}`);
  },

  // Obtener categoría por ID
  getById(id) {
    return publicationsApiClient.get(`/categories/${id}`);
  },

  // Buscar por nombre
  searchByName(name, page = 0, size = 20) {
    return publicationsApiClient.get(`/categories/name/${name}/page/${page}/size/${size}`);
  },

  // Crear categoría
  create(categoryData) {
    return publicationsApiClient.post('/categories', categoryData);
  },

  // Actualizar categoría
  update(id, categoryData) {
    return publicationsApiClient.put(`/categories/${id}`, categoryData);
  },

  // Eliminar categoría
  delete(id) {
    return publicationsApiClient.delete(`/categories/${id}`);
  },
};

export const favoritesAPI = {
  // Obtener todos los favoritos (para contar)
  getAllFavorites(page = 0, size = 90) {
    return publicationsApiClient.get(`/favorites/page/${page}/size/${size}`);
  },

  // Obtener favoritos de un usuario
  getByUser(userId, page = 0, size = 20) {
    return publicationsApiClient.get(`/favorites/user/${userId}/page/${page}/size/${size}`);
  },

  // Obtener favoritos de una publicación
  getByPublication(publicationId, page = 0, size = 20) {
    return publicationsApiClient.get(`/favorites/publication/${publicationId}/page/${page}/size/${size}`);
  },

  // Contar favoritos de una publicación (solo obtiene totalItems)
  async countByPublication(publicationId) {
    try {
      const response = await publicationsApiClient.get(`/favorites/publication/${publicationId}/page/0/size/1`);
      return response.data.totalItems || 0;
    } catch (error) {
      console.error(`Error counting favorites for publication ${publicationId}:`, error);
      return 0;
    }
  },

  // Agregar a favoritos
  createFavorite(favoriteData) {
    return publicationsApiClient.post('/favorites', favoriteData);
  },

  // Eliminar de favoritos
  deleteFavorite(id) {
    return publicationsApiClient.delete(`/favorites/${id}`);
  },
};