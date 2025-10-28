import { usersApiClient } from './apiClient';

export const userProfilesAPI = {
  // Obtener todos los perfiles de usuario
  getAllProfiles() {
    return usersApiClient.get('/user-profiles');
  },

  // Obtener perfil por ID
  getProfileById(id) {
    return usersApiClient.get(`/user-profiles/${id}`);
  },

  // Crear perfil de usuario
  createProfile(profileData) {
    return usersApiClient.post('/user-profiles', profileData);
  },

  // Actualizar perfil de usuario
  updateProfile(id, profileData) {
    return usersApiClient.put(`/user-profiles/${id}`, profileData);
  },

  // Eliminar perfil de usuario
  deleteProfile(id) {
    return usersApiClient.delete(`/user-profiles/${id}`);
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
    return usersApiClient.get('/user-profiles');
  },

  // Obtener usuario por ID
  getById(id) {
    return usersApiClient.get(`/user-profiles/${id}`);
  },

  // Crear usuario
  create(userData) {
    return usersApiClient.post('/user-profiles', userData);
  },

  // Actualizar usuario
  update(id, userData) {
    return usersApiClient.put(`/user-profiles/${id}`, userData);
  },

  // Eliminar usuario
  delete(id) {
    return usersApiClient.delete(`/user-profiles/${id}`);
  }
};

export const announcementsAPI = {
  // Obtener todos los anuncios
  getAll(page = 0, size = 20) {
    return usersApiClient.get(`/announcements/page/${page}/size/${size}`);
  },

  // Obtener anuncio por ID
  getById(id) {
    return usersApiClient.get(`/announcements/${id}`);
  },

  // Crear anuncio
  create(announcementData) {
    return usersApiClient.post('/announcements', announcementData);
  },

  // Actualizar anuncio
  update(id, announcementData) {
    return usersApiClient.put(`/announcements/${id}`, announcementData);
  },

  // Eliminar anuncio
  delete(id) {
    return usersApiClient.delete(`/announcements/${id}`);
  }
};