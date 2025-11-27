import { usersApiClient } from './apiClient';

export const userProfilesAPI = {
  getAllProfiles(page = 0, displayName = '-') {
    const dn = encodeURIComponent(displayName || '-');
    return usersApiClient.get(`/user-profiles/display-name/${dn}/page/${page}`);
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

  // Obtener perfil por userId (si el backend no provee un endpoint directo, buscar localmente)
  async getProfileByUserId(userId) {
    try {
      const response = await this.getAllProfiles(0, '-'); // obtener todos sin filtro
      const profiles = response.data?.content || response.data || [];
      return (profiles || []).find(profile => profile.userId === userId) || null;
    } catch (error) {
      console.error(`Error getting profile for user ${userId}:`, error);
      return null;
    }
  }
};

export const usersAPI = {
  // Obtener todos los usuarios (reutiliza user-profiles endpoint sin filtro)
  getAll() {
    return userProfilesAPI.getAllProfiles(0, '-');
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

  // Actualizar datos de usuario en /auth/users/{id}
  updateAuthUser(id, userData) {
    return usersApiClient.put(`/auth/users/${id}`, userData);
  },

  // Eliminar usuario
  delete(id) {
    return usersApiClient.delete(`/user-profiles/${id}`);
  }
};

// API para anuncios
export const announcementsAPI = {
  getAll({ targetAudience = '-', type = '-', page = 0 } = {}) {
    const ta = encodeURIComponent(targetAudience || '-');
    const t = encodeURIComponent(type || '-');
    return usersApiClient.get(`/announcements/audience/${ta}/type/${t}/page/${page}`);
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

// API para la relación many-to-many entre UserProfile y Announcement
export const profileAnnouncementsAPI = {
  getAllByFilters({ profileId = 0, announcementId = 0, min = '0000-01-01T00:00:00Z', max = '0000-01-01T00:00:00Z', isRead = '-', page = 0 } = {}) {
    const p = Number.isInteger(profileId) ? profileId : encodeURIComponent(profileId);
    const a = Number.isInteger(announcementId) ? announcementId : encodeURIComponent(announcementId);
    const minEnc = encodeURIComponent(min);
    const maxEnc = encodeURIComponent(max);
    const isReadEnc = encodeURIComponent(isRead);

    return usersApiClient.get(`/profile-announcements/profile/${p}/announcement/${a}/date/${minEnc}/${maxEnc}/isRead/${isReadEnc}/page/${page}`);
  },

  // Obtener relación por ID
  getById(id) {
    return usersApiClient.get(`/profile-announcements/${id}`);
  },

  // Crear relación profile-announcement
  create(profileAnnouncementData) {
    return usersApiClient.post('/profile-announcements', profileAnnouncementData);
  },

  // Actualizar relación (marcar leido, programar fecha, etc.)
  update(id, updateData) {
    return usersApiClient.put(`/profile-announcements/${id}`, updateData);
  },

  // Eliminar relación
  delete(id) {
    return usersApiClient.delete(`/profile-announcements/${id}`);
  }
};

// Helper para mapear el DTO de profile-announcement a la forma que pide la UI
export function mapProfileAnnouncementDTO(dto) {
  if (!dto) return null;
  return {
    id: dto.id,
    createdAt: dto.createdAt || dto.created_at || dto.created, // tolerancia a distintos nombres
    isDeleted: dto.isDeleted ?? dto.deleted ?? false,
    updatedAt: dto.updatedAt || dto.updated_at || dto.updated,
    isRead: dto.isRead ?? dto.read ?? false,
    programmedDate: dto.programmedDate || dto.programmed_date || dto.programmedAt || null,
    announcementId: dto.announcementId || dto.announcement_id || (dto.announcement && dto.announcement.id) || null,
    userProfileId: dto.userProfileId || dto.user_profile_id || (dto.userProfile && dto.userProfile.id) || null,
    // mantener resto del DTO por si la UI necesita más campos
    raw: dto
  };
}

export default {
  userProfilesAPI,
  usersAPI,
  announcementsAPI,
  profileAnnouncementsAPI,

};
