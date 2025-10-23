<template>
  <div class="topbar">
    <div class="topbar-content">
      <div class="topbar-left">
        <div class="logo-section">
          <svg
            xmlns="http://www.w3.org/2000/svg"
            width="28"
            height="28"
            viewBox="0 0 24 24"
            fill="none"
            stroke="currentColor"
            stroke-width="2"
            stroke-linecap="round"
            stroke-linejoin="round"
            class="logo-icon"
          >
            <path d="M4 19.5v-15A2.5 2.5 0 0 1 6.5 2H20v20H6.5a2.5 2.5 0 0 1 0-5H20" />
          </svg>
          <h1 class="app-name">Biblioteca Digital</h1>
        </div>
      </div>

      <div class="topbar-right">
        <div class="notifications-menu">
          <button @click="toggleNotifications" class="icon-button" title="Notificaciones">
            <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M6 8a6 6 0 0 1 12 0c0 7 3 9 3 9H3s3-2 3-9"/>
              <path d="M10.3 21a1.94 1.94 0 0 0 3.4 0"/>
            </svg>
            <span v-if="announcements.length > 0" class="notification-badge">{{ announcements.length }}</span>
          </button>

          <!-- Dropdown de notificaciones -->
          <div v-if="showNotifications" class="notifications-dropdown" @click.stop>
            <div class="notifications-header">
              <h3>Anuncios</h3>
              <button @click="showNotifications = false" class="close-notifications-btn">×</button>
            </div>
            <div class="notifications-list">
              <div v-if="announcements.length === 0" class="no-notifications">
                <svg xmlns="http://www.w3.org/2000/svg" width="48" height="48" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <path d="M6 8a6 6 0 0 1 12 0c0 7 3 9 3 9H3s3-2 3-9"/>
                  <path d="M10.3 21a1.94 1.94 0 0 0 3.4 0"/>
                </svg>
                <p>No hay anuncios nuevos</p>
              </div>
              <div
                v-for="announcement in announcements"
                :key="announcement.id"
                class="notification-item"
              >
                <div class="notification-icon">
                  <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                    <circle cx="12" cy="12" r="10"/>
                    <line x1="12" y1="16" x2="12" y2="12"/>
                    <line x1="12" y1="8" x2="12.01" y2="8"/>
                  </svg>
                </div>
                <div class="notification-content">
                  <h4>{{ announcement.title }}</h4>
                  <p>{{ announcement.message }}</p>
                  <span class="notification-time">{{ formatDate(announcement.createdAt) }}</span>
                </div>
              </div>
            </div>
            <div class="notifications-footer">
              <button @click="handleViewAllNotifications" class="view-all-notifications-btn">
                Ver todas las notificaciones
              </button>
            </div>
          </div>
        </div>

        <div class="user-menu">
          <button @click="toggleUserMenu" class="user-button">
            <div class="user-avatar">
              {{ userInitial }}
            </div>
            <span class="user-name">{{ currentUserProfile?.displayName || 'Usuario' }}</span>
            <svg class="chevron-icon" xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <polyline points="6 9 12 15 18 9"/>
            </svg>
          </button>

          <div v-if="showUserMenu" class="user-dropdown" @click.stop>
            <div class="dropdown-header">
              <div class="user-info">
                <p class="user-display-name">{{ currentUserProfile?.displayName || 'Usuario' }}</p>
                <p class="user-email">{{ currentUserProfile?.user?.username || 'usuario' }}@biblioteca.com</p>
                <p class="user-id">ID: {{ CURRENT_USER_PROFILE_ID }}</p>
              </div>
            </div>
            <div class="dropdown-divider"></div>
            <button @click="handleSettings" class="dropdown-item">
              <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <path d="M12.22 2h-.44a2 2 0 0 0-2 2v.18a2 2 0 0 1-1 1.73l-.43.25a2 2 0 0 1-2 0l-.15-.08a2 2 0 0 0-2.73.73l-.22.38a2 2 0 0 0 .73 2.73l.15.1a2 2 0 0 1 1 1.72v.51a2 2 0 0 1-1 1.74l-.15.09a2 2 0 0 0-.73 2.73l.22.38a2 2 0 0 0 2.73.73l.15-.08a2 2 0 0 1 2 0l.43.25a2 2 0 0 1 1 1.73V20a2 2 0 0 0 2 2h.44a2 2 0 0 0 2-2v-.18a2 2 0 0 1 1-1.73l.43-.25a2 2 0 0 1 2 0l.15.08a2 2 0 0 0 2.73-.73l.22-.39a2 2 0 0 0-.73-2.73l-.15-.08a2 2 0 0 1-1-1.74v-.5a2 2 0 0 1 1-1.74l.15-.09a2 2 0 0 0 .73-2.73l-.22-.38a2 2 0 0 0-2.73-.73l-.15.08a2 2 0 0 1-2 0l-.43-.25a2 2 0 0 1-1-1.73V4a2 2 0 0 0-2-2z"/>
                <circle cx="12" cy="12" r="3"/>
              </svg>
              Configuración
            </button>
            <button @click="handleLogout" class="dropdown-item logout">
              <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <path d="M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4"/>
                <polyline points="16 17 21 12 16 7"/>
                <line x1="21" x2="9" y1="12" y2="12"/>
              </svg>
              Cerrar Sesión
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, onMounted, onUnmounted, computed } from 'vue';
import { useRouter } from 'vue-router';
import { announcementsAPI, userProfilesAPI } from '../api/usersService';
import { CURRENT_USER_PROFILE_ID } from '../utils/constants';

export default {
  name: "Topbar",
  setup() {
    const router = useRouter();
    const showUserMenu = ref(false);
    const showNotifications = ref(false);
    const announcements = ref([]);
    const currentUserProfile = ref(null);

    const userInitial = computed(() => {
      return currentUserProfile.value?.displayName?.[0]?.toUpperCase() || 'U';
    });

    const toggleUserMenu = () => {
      showUserMenu.value = !showUserMenu.value;
      showNotifications.value = false;
    };

    const toggleNotifications = () => {
      showNotifications.value = !showNotifications.value;
      showUserMenu.value = false;
    };

    const loadCurrentUser = async () => {
      try {
        const response = await userProfilesAPI.getProfileById(CURRENT_USER_PROFILE_ID);
        currentUserProfile.value = response.data;
      } catch (error) {
        console.error('Error loading current user:', error);
        currentUserProfile.value = null;
      }
    };

    const loadAnnouncements = async () => {
      try {
        const response = await announcementsAPI.getAll();
        announcements.value = response.data?.content || [];
      } catch (error) {
        console.error('Error loading announcements:', error);
        announcements.value = [];
      }
    };

    const handleViewAllNotifications = () => {
      showNotifications.value = false;
      // Navigate to notifications page or show all
      console.log('Ver todas las notificaciones');
    };

    const handleSettings = () => {
      showUserMenu.value = false;
      router.push('/settings');
    };

    const handleLogout = () => {
      showUserMenu.value = false;
      router.push('/login');
    };

    const formatDate = (dateString) => {
      if (!dateString) return '';
      const date = new Date(dateString);
      return date.toLocaleDateString('es-ES', {
        year: 'numeric',
        month: 'long',
        day: 'numeric'
      });
    };

    const closeMenuOnClickOutside = (event) => {
      if (showUserMenu.value && !event.target.closest('.user-menu')) {
        showUserMenu.value = false;
      }
      if (showNotifications.value && !event.target.closest('.notifications-menu')) {
        showNotifications.value = false;
      }
    };

    onMounted(() => {
      document.addEventListener('click', closeMenuOnClickOutside);
      loadAnnouncements();
      loadCurrentUser();
    });

    onUnmounted(() => {
      document.removeEventListener('click', closeMenuOnClickOutside);
    });

    return {
      showUserMenu,
      showNotifications,
      userInitial,
      announcements,
      currentUserProfile,
      CURRENT_USER_PROFILE_ID,
      toggleUserMenu,
      toggleNotifications,
      handleViewAllNotifications,
      handleSettings,
      handleLogout,
      formatDate
    };
  }
};
</script>

<style scoped>
.topbar {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border-bottom: 2px solid rgba(255, 255, 255, 0.1);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.topbar-content {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 12px 24px;
  max-width: 1600px;
  margin: 0 auto;
}

.topbar-left {
  flex: 0 0 auto;
}

.logo-section {
  display: flex;
  align-items: center;
  gap: 12px;
}

.logo-icon {
  color: white;
}

.app-name {
  font-size: 20px;
  font-weight: bold;
  margin: 0;
}

.topbar-right {
  display: flex;
  align-items: center;
  gap: 16px;
}

.icon-button {
  position: relative;
  background: rgba(255, 255, 255, 0.1);
  border: none;
  border-radius: 8px;
  padding: 8px;
  cursor: pointer;
  color: white;
  transition: all 0.2s;
  display: flex;
  align-items: center;
  justify-content: center;
}

.icon-button:hover {
  background: rgba(255, 255, 255, 0.2);
}

.notification-badge {
  position: absolute;
  top: -4px;
  right: -4px;
  background: #ef4444;
  color: white;
  border-radius: 10px;
  padding: 2px 6px;
  font-size: 11px;
  font-weight: bold;
}

.notifications-menu {
  position: relative;
}

.notifications-dropdown {
  position: absolute;
  top: calc(100% + 8px);
  right: 0;
  background: white;
  border-radius: 12px;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.15);
  width: 380px;
  max-height: 500px;
  overflow: hidden;
  z-index: 1000;
  color: #333;
}

.notifications-header {
  padding: 16px;
  border-bottom: 1px solid #e5e7eb;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.notifications-header h3 {
  margin: 0;
  font-size: 18px;
  font-weight: 600;
}

.close-notifications-btn {
  background: none;
  border: none;
  font-size: 24px;
  cursor: pointer;
  color: #6b7280;
  padding: 0;
  width: 28px;
  height: 28px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 4px;
  transition: all 0.2s;
}

.close-notifications-btn:hover {
  background: #f3f4f6;
  color: #111827;
}

.notifications-list {
  max-height: 360px;
  overflow-y: auto;
}

.notification-item {
  display: flex;
  gap: 12px;
  padding: 16px;
  border-bottom: 1px solid #f3f4f6;
  cursor: pointer;
  transition: background 0.2s;
}

.notification-item:hover {
  background: #f9fafb;
}

.notification-item:last-child {
  border-bottom: none;
}

.notification-icon {
  flex-shrink: 0;
  width: 40px;
  height: 40px;
  background: #eff6ff;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #3b82f6;
}

.notification-content h4 {
  margin: 0 0 4px 0;
  font-size: 14px;
  font-weight: 600;
  color: #111827;
}

.notification-content p {
  margin: 0 0 8px 0;
  font-size: 13px;
  color: #6b7280;
}

.notification-time {
  font-size: 12px;
  color: #9ca3af;
}

.no-notifications {
  padding: 48px 24px;
  text-align: center;
  color: #9ca3af;
}

.no-notifications svg {
  color: #d1d5db;
  margin-bottom: 16px;
}

.no-notifications p {
  margin: 0;
  font-size: 14px;
}

.notifications-footer {
  padding: 12px 16px;
  border-top: 1px solid #e5e7eb;
  text-align: center;
}

.view-all-notifications-btn {
  background: none;
  border: none;
  color: #667eea;
  font-weight: 600;
  cursor: pointer;
  padding: 8px 16px;
  border-radius: 6px;
  transition: all 0.2s;
  width: 100%;
}

.view-all-notifications-btn:hover {
  background: #eff6ff;
}

.user-menu {
  position: relative;
}

.user-button {
  display: flex;
  align-items: center;
  gap: 8px;
  background: rgba(255, 255, 255, 0.1);
  border: none;
  border-radius: 24px;
  padding: 6px 12px 6px 6px;
  cursor: pointer;
  color: white;
  transition: all 0.2s;
}

.user-button:hover {
  background: rgba(255, 255, 255, 0.2);
}

.user-avatar {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  background: white;
  color: #667eea;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 600;
  font-size: 14px;
}

.user-name {
  font-size: 14px;
  font-weight: 500;
}

.user-dropdown {
  position: absolute;
  top: calc(100% + 8px);
  right: 0;
  background: white;
  border-radius: 12px;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.15);
  width: 280px;
  overflow: hidden;
  z-index: 1000;
  color: #333;
}

.dropdown-header {
  padding: 16px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
}

.user-display-name {
  margin: 0 0 4px 0;
  font-size: 16px;
  font-weight: 600;
}

.user-email {
  margin: 0 0 4px 0;
  font-size: 13px;
  opacity: 0.9;
}

.user-id {
  margin: 0;
  font-size: 11px;
  opacity: 0.7;
}

.dropdown-divider {
  height: 1px;
  background: #e5e7eb;
}

.dropdown-item {
  width: 100%;
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 16px;
  background: none;
  border: none;
  text-align: left;
  cursor: pointer;
  font-size: 14px;
  color: #374151;
  transition: background 0.2s;
}

.dropdown-item:hover {
  background: #f9fafb;
}

.dropdown-item.logout {
  color: #ef4444;
}

.dropdown-item.logout:hover {
  background: #fef2f2;
}

.chevron-icon {
  color: white;
}
</style>