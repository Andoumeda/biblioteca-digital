import { defineStore } from 'pinia';
import { userProfilesAPI, usersAPI } from '../api/usersService';

export const useUsersStore = defineStore('users', {
  state: () => ({
    users: [],
    userProfiles: [],
    loading: false,
    error: null,
  }),

  getters: {
    getUserById: (state) => (id) => {
      return state.users.find(user => user.id === id);
    },

    getProfileById: (state) => (id) => {
      return state.userProfiles.find(profile => profile.id === id);
    },

    getProfileByUserId: (state) => (userId) => {
      return state.userProfiles.find(profile => profile.userId === userId);
    },

    totalUsers: (state) => state.users.length,

    totalProfiles: (state) => state.userProfiles.length
  },

  actions: {
    async fetchUsers() {
      this.loading = true;
      this.error = null;

      try {
        const response = await usersAPI.getAll();
        this.users = response.data || [];
      } catch (error) {
        this.error = error.message || 'Error al cargar usuarios';
        console.error('Error fetching users:', error);
      } finally {
        this.loading = false;
      }
    },

    async fetchUserProfiles() {
      this.loading = true;
      this.error = null;

      try {
        const response = await userProfilesAPI.getAllProfiles();
        this.userProfiles = response.data || [];
      } catch (error) {
        this.error = error.message || 'Error al cargar perfiles de usuario';
        console.error('Error fetching user profiles:', error);
      } finally {
        this.loading = false;
      }
    },

    async fetchUserById(id) {
      try {
        const response = await usersAPI.getById(id);
        const user = response.data;

        // Actualizar en el array si existe, sino agregarlo
        const index = this.users.findIndex(u => u.id === id);
        if (index !== -1) {
          this.users[index] = user;
        } else {
          this.users.push(user);
        }

        return user;
      } catch (error) {
        console.error(`Error fetching user ${id}:`, error);
        return null;
      }
    },

    async fetchProfileById(id) {
      try {
        const response = await userProfilesAPI.getProfileById(id);
        const profile = response.data;

        // Actualizar en el array si existe, sino agregarlo
        const index = this.userProfiles.findIndex(p => p.id === id);
        if (index !== -1) {
          this.userProfiles[index] = profile;
        } else {
          this.userProfiles.push(profile);
        }

        return profile;
      } catch (error) {
        console.error(`Error fetching profile ${id}:`, error);
        return null;
      }
    },

    async createUser(userData) {
      this.loading = true;
      this.error = null;

      try {
        const response = await usersAPI.create(userData);
        await this.fetchUsers();
        return response.data;
      } catch (error) {
        this.error = error.message || 'Error al crear usuario';
        console.error('Error creating user:', error);
        throw error;
      } finally {
        this.loading = false;
      }
    },

    async createProfile(profileData) {
      this.loading = true;
      this.error = null;

      try {
        const response = await userProfilesAPI.createProfile(profileData);
        await this.fetchUserProfiles();
        return response.data;
      } catch (error) {
        this.error = error.message || 'Error al crear perfil';
        console.error('Error creating profile:', error);
        throw error;
      } finally {
        this.loading = false;
      }
    },

    async updateUser(id, userData) {
      this.loading = true;
      this.error = null;

      try {
        const response = await usersAPI.update(id, userData);
        await this.fetchUsers();
        return response.data;
      } catch (error) {
        this.error = error.message || 'Error al actualizar usuario';
        console.error('Error updating user:', error);
        throw error;
      } finally {
        this.loading = false;
      }
    },

    async updateProfile(id, profileData) {
      this.loading = true;
      this.error = null;

      try {
        const response = await userProfilesAPI.updateProfile(id, profileData);
        await this.fetchUserProfiles();
        return response.data;
      } catch (error) {
        this.error = error.message || 'Error al actualizar perfil';
        console.error('Error updating profile:', error);
        throw error;
      } finally {
        this.loading = false;
      }
    }
  }
});