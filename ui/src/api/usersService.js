import { usersApiClient } from './apiClient';

export const userProfilesAPI = {
  // Obtener todos los perfiles de usuario
  getAllProfiles() {
    return usersApiClient.get('/api/user-profiles');
  },

  // Obtener perfil por ID
  getProfileById(id) {
    return usersApiClient.get(`/api/user-profiles/${id}`);
  },

  // Crear perfil de usuario
  createProfile(profileData) {
    return usersApiClient.post('/api/user-profiles', profileData);
  },

  // Actualizar perfil de usuario
  updateProfile(id, profileData) {
    return usersApiClient.put(`/api/user-profiles/${id}`, profileData);
  },

  // Eliminar perfil de usuario
  deleteProfile(id) {
    return usersApiClient.delete(`/api/user-profiles/${id}`);
  },

  // Obtener perfil por userId
  async getProfileByUserId(userId) {
    try {
      const response = await this.getAllProfiles();
      const profiles = response.data || [];
      return profiles.find(profile => profile.userId === userId);
    } catch (error) {
      console.error(`Error getting profile for user ${userId}:`, error);
      return null;
    }
  }
};

export const usersAPI = {
  // Obtener todos los usuarios
  getAll() {
    return usersApiClient.get('/api/users');
  },

  // Obtener usuario por ID
  getById(id) {
    return usersApiClient.get(`/api/users/${id}`);
  },

  // Crear usuario
  create(userData) {
    return usersApiClient.post('/api/users', userData);
  },

  // Actualizar usuario
  update(id, userData) {
    return usersApiClient.put(`/api/users/${id}`, userData);
  },

  // Eliminar usuario
  delete(id) {
    return usersApiClient.delete(`/api/users/${id}`);
  }
};

export const announcementsAPI = {
  // Obtener todos los anuncios
  getAll() {
    return usersApiClient.get('/api/announcements');
  },

  // Obtener anuncio por ID
  getById(id) {
    return usersApiClient.get(`/api/announcements/${id}`);
  },

  // Crear anuncio
  create(announcementData) {
    return usersApiClient.post('/api/announcements', announcementData);
  },

  // Actualizar anuncio
  update(id, announcementData) {
    return usersApiClient.put(`/api/announcements/${id}`, announcementData);
  },

  // Eliminar anuncio
  delete(id) {
    return usersApiClient.delete(`/api/announcements/${id}`);
  }
};