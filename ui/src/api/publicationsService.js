import apiClient from './apiClient';

export const publicationsAPI = {
  // Obtener publicaciones paginadas
  getPublications(page = 0, size = 20) {
    return apiClient.get(`/publications/page/${page}/size/${size}`);
  },

  // Buscar por título
  searchByTitle(title, page = 0, size = 20) {
    return apiClient.get(`/publications/title/${title}/page/${page}/size/${size}`);
  },

  // Buscar por descripción
  searchByDescription(description, page = 0, size = 20) {
    return apiClient.get(`/publications/description/${description}/page/${page}/size/${size}`);
  },

  // Buscar por estado (APPROVED, PENDING, REJECTED)
  getByState(state, page = 0, size = 20) {
    return apiClient.get(`/publications/state/${state}/page/${page}/size/${size}`);
  },

  // Buscar por usuario
  getByUser(userId, page = 0, size = 20) {
    return apiClient.get(`/publications/user/${userId}/page/${page}/size/${size}`);
  },

  // Buscar por categoría
  getByCategory(categoryId, page = 0, size = 20) {
    return apiClient.get(`/publications/category/${categoryId}/page/${page}/size/${size}`);
  },

  // Crear nueva publicación
  createPublication(publicationData) {
    return apiClient.post('/publications', publicationData);
  },

  // Actualizar estado de publicación
  updateState(publicationId, newState) {
    return apiClient.put(`/publications/${publicationId}/state/${newState}`);
  },

  // Obtener publicación por ID
  getById(publicationId) {
    return apiClient.get(`/publications/${publicationId}`);
  },

  // Actualizar publicación
  update(publicationId, publicationData) {
    return apiClient.put(`/publications/${publicationId}`, publicationData);
  },

  // Eliminar publicación
  delete(publicationId) {
    return apiClient.delete(`/publications/${publicationId}`);
  },
};

export const categoriesAPI = {
  // Obtener categorías paginadas
  getAll(page = 0, size = 20) {
    return apiClient.get(`/categories/page/${page}/size/${size}`);
  },

  // Obtener categoría por ID
  getById(id) {
    return apiClient.get(`/categories/${id}`);
  },

  // Buscar por nombre
  searchByName(name, page = 0, size = 20) {
    return apiClient.get(`/categories/name/${name}/page/${page}/size/${size}`);
  },

  // Crear categoría
  create(categoryData) {
    return apiClient.post('/categories', categoryData);
  },

  // Actualizar categoría
  update(id, categoryData) {
    return apiClient.put(`/categories/${id}`, categoryData);
  },

  // Eliminar categoría
  delete(id) {
    return apiClient.delete(`/categories/${id}`);
  },
};

export const favoritesAPI = {
  // Obtener todos los favoritos (para contar)
  getAllFavorites(page = 0, size = 90) {
    return apiClient.get(`/favorites/page/${page}/size/${size}`);
  },

  // Obtener favoritos de un usuario
  getByUser(userId, page = 0, size = 20) {
    return apiClient.get(`/favorites/user/${userId}/page/${page}/size/${size}`);
  },

  // Obtener favoritos de una publicación
  getByPublication(publicationId, page = 0, size = 20) {
    return apiClient.get(`/favorites/publication/${publicationId}/page/${page}/size/${size}`);
  },

  // Contar favoritos de una publicación (solo obtiene totalItems)
  async countByPublication(publicationId) {
    try {
      const response = await apiClient.get(`/favorites/publication/${publicationId}/page/0/size/1`);
      return response.data.totalItems || 0;
    } catch (error) {
      console.error(`Error counting favorites for publication ${publicationId}:`, error);
      return 0;
    }
  },

  // Agregar a favoritos
  createFavorite(favoriteData) {
    return apiClient.post('/favorites', favoriteData);
  },

  // Eliminar de favoritos
  deleteFavorite(id) {
    return apiClient.delete(`/favorites/${id}`);
  },
};