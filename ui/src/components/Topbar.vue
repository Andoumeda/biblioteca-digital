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
            stroke="currentColor"
            stroke-width="2"
            stroke-linecap="round"
            stroke-linejoin="round"
            class="logo-icon"
          >
            <path d="M4 19.5v-15A2.5 2.5 0 0 1 6.5 2H20v20H6.5a2.5 2.5 0 0 1 0-5H20" />
          </svg>
          <h1 class="app-name">DigiBooks Source</h1>
        </div>
      </div>

      <div class="topbar-right">
        <div class="notifications-menu">
          <button @click="toggleNotifications" class="icon-button" title="Notificaciones">
            <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
              <path d="M6 8a6 6 0 0 1 12 0c0 7 3 9 3 9H3s3-2 3-9"/>
              <path d="M10.3 21a1.94 1.94 0 0 0 3.4 0"/>
            </svg>
            <span v-if="announcements.length > 0" class="notification-badge"></span>
          </button>

          <!-- Dropdown de notificaciones -->
          <div v-if="showNotifications" class="notifications-dropdown" @click.stop>
            <div class="notifications-header">
              <h3>Anuncios</h3>
              <button @click="showNotifications = false" class="close-notifications-btn">×</button>
            </div>
            <div class="notifications-list">
              <div v-if="announcements.length === 0" class="no-notifications">
                <svg xmlns="http://www.w3.org/2000/svg" width="48" height="48" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
                  <path d="M6 8a6 6 0 0 1 12 0c0 7 3 9 3 9H3s3-2 3-9"/>
                  <path d="M10.3 21a1.94 1.94 0 0 0 3.4 0"/>
                </svg>
                <p>No hay anuncios nuevos</p>
              </div>
              <div
                v-for="announcement in announcements"
                :key="announcement.id"
                class="notification-item"
                @click="goToAnnouncement(announcement.id)"
              >
                <div :class="['notification-icon', `icon-${getTypeClass(announcement.type)}`]">
                  <svg v-if="announcement.type === 'ALERT'" xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                    <path d="M10.29 3.86L1.82 18a2 2 0 0 0 1.71 3h16.94a2 2 0 0 0 1.71-3L13.71 3.86a2 2 0 0 0-3.42 0z"/>
                    <line x1="12" y1="9" x2="12" y2="13"/>
                    <line x1="12" y1="17" x2="12.01" y2="17"/>
                  </svg>
                  <svg v-else-if="announcement.type === 'INFO'" xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                    <circle cx="12" cy="12" r="10"/>
                    <line x1="12" y1="16" x2="12" y2="12"/>
                    <line x1="12" y1="8" x2="12.01" y2="8"/>
                  </svg>
                  <svg v-else-if="announcement.type === 'WARNING'" xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                    <path d="M10.29 3.86L1.82 18a2 2 0 0 0 1.71 3h16.94a2 2 0 0 0 1.71-3L13.71 3.86a2 2 0 0 0-3.42 0z"/>
                    <line x1="12" y1="9" x2="12" y2="13"/>
                    <line x1="12" y1="17" x2="12.01" y2="17"/>
                  </svg>
                  <svg v-else-if="announcement.type === 'PROMO'" xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                    <path d="M21 2l-2 2m-7.61 7.61a5.5 5.5 0 1 1-7.778 7.778 5.5 5.5 0 0 1 7.777-7.777zm0 0L15.5 7.5m0 0l3 3L22 7l-3-3m-3.5 3.5L19 4"/>
                  </svg>
                  <svg v-else xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                    <circle cx="12" cy="12" r="10"/>
                    <line x1="12" y1="16" x2="12" y2="12"/>
                    <line x1="12" y1="8" x2="12.01" y2="8"/>
                  </svg>
                </div>
                <div class="notification-content">
                  <div class="notification-header-row">
                    <h4>{{ announcement.title }}</h4>
                  </div>
                  <p>{{ announcement.message }}</p>
                  <div class="notification-footer-row">
                    <span class="notification-time">{{ formatDate(announcement.createdAt) }}</span>
                    <span :class="['badge-small', `badge-audience-${getAudienceClass(announcement.targetAudience)}`]">
                      {{ getAudienceLabel(announcement.targetAudience) }}
                    </span>
                  </div>
                </div>
              </div>
            </div>
            <div class="notifications-footer">
              <router-link to="/announcements" class="nav-link" @click="showNotifications = false">
                <svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
                  <path d="M10.5 5H6a2 2 0 0 0-2 2v12a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2v-4.5"/>
                  <path d="M17 3l4 4L10 18l-5 1 1-5Z"/>
                </svg>
                Ver todos los anuncios
              </router-link>
            </div>
          </div>
        </div>

        <div class="user-menu">
          <button @click="toggleUserMenu" class="user-button">
            <div class="user-avatar">
              {{ userInitial }}
            </div>
            <span class="user-name">{{ displayName }}</span>
            <svg class="chevron-icon" xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
              <polyline points="6 9 12 15 18 9"/>
            </svg>
          </button>

          <div v-if="showUserMenu" class="user-dropdown" @click.stop>
            <div class="dropdown-header">
              <div class="user-info">
                <div class="user-avatar">
                  {{ userInitial }}
                </div>
                <p class="user-display-name">{{ displayName }}</p>
                <p class="user-email">{{ userEmail }}</p>
              </div>
            </div>
            <div class="dropdown-divider"></div>
            <button @click="handleSettings" class="dropdown-item">
              <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
                <path d="M12.22 2h-.44a2 2 0 0 0-2 2v.18a2 2 0 0 1-1 1.73l-.43.25a2 2 0 0 1-2 0l-.15-.08a2 2 0 0 0-2.73.73l-.22.38a2 2 0 0 0 .73 2.73l.15.1a2 2 0 0 1 1 1.72v.51a2 2 0 0 1-1 1.74l-.15.09a2 2 0 0 0-.73 2.73l.22.38a2 2 0 0 0 2.73.73l.15-.08a2 2 0 0 1 2 0l.43.25a2 2 0 0 1 1 1.73V20a2 2 0 0 0 2 2h.44a2 2 0 0 0 2-2v-.18a2 2 0 0 1 1-1.73l.43-.25a2 2 0 0 1 2 0l.15.08a2 2 0 0 0 2.73-.73l.22-.39a2 2 0 0 0-.73-2.73l-.15-.08a2 2 0 0 1-1-1.74v-.5a2 2 0 0 1 1-1.74l.15-.09a2 2 0 0 0 .73-2.73l-.22-.38a2 2 0 0 0-2.73-.73l-.15.08a2 2 0 0 1-2 0l-.43-.25a2 2 0 0 1-1-1.73V4a2 2 0 0 0-2-2z"/>
                <circle cx="12" cy="12" r="3"/>
              </svg>
              Configuración
            </button>
            <button @click="handleLogout" class="dropdown-item logout">
              <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
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
import { useAuthStore } from '../stores/authStore';

export default {
  name: "Topbar",
  setup() {
    const router = useRouter();
    const authStore = useAuthStore();
    const showUserMenu = ref(false);
    const showNotifications = ref(false);
    const announcements = ref([]);
    const currentUserProfile = ref(null);

    const userInitial = computed(() => {
      if (currentUserProfile.value?.displayName) {
        return currentUserProfile.value.displayName[0].toUpperCase();
      }
      if (authStore.displayName) {
        return authStore.displayName[0].toUpperCase();
      }
      if (authStore.currentUsername) {
        return authStore.currentUsername[0].toUpperCase();
      }
      return 'U';
    });

    const displayName = computed(() => {
      return currentUserProfile.value?.displayName || authStore.displayName || authStore.currentUsername || 'Usuario';
    });

    const userEmail = computed(() => {
      return currentUserProfile.value?.user?.username || authStore.userEmail || 'usuario@biblioteca.com';
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
        // Usar el userProfileId del token JWT
        const userProfileId = authStore.currentUserProfileId;
        if (userProfileId) {
          const response = await userProfilesAPI.getProfileById(userProfileId);
          currentUserProfile.value = response.data;
        }
      } catch (error) {
        console.error('Error loading current user:', error);
        currentUserProfile.value = null;
      }
    };

    const loadAnnouncements = async () => {
      try {
        const response = await announcementsAPI.getAll({
          targetAudience: '-',
          type: '-',
          page: 0
        });
        // El backend devuelve en 'data', no en 'content'
        announcements.value = response.data?.data || [];
        announcements.value = announcements.value.sort((a, b) => new Date(b.createdAt) - new Date(a.createdAt));
      } catch (error) {
        console.error('Error loading announcements:', error);
        announcements.value = [];
      }
    };

    const handleViewAllNotifications = () => {
      showNotifications.value = false;
      router.push('/announcements');
    };

    const handleSettings = () => {
      showUserMenu.value = false;
      router.push('/settings');
    };

    const handleLogout = async () => {
      showUserMenu.value = false;
      await authStore.logout();
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

    const goToAnnouncement = (id) => {
      showNotifications.value = false;
      router.push(`/announcements/${id}`);
    };

    const getTypeClass = (type) => {
      const typeMap = {
        'ALERT': 'alert',
        'INFO': 'info',
        'WARNING': 'warning',
        'PROMO': 'promo'
      };
      return typeMap[type] || 'info';
    };

    const getTypeLabel = (type) => {
      const labelMap = {
        'ALERT': 'Alerta',
        'INFO': 'Información',
        'WARNING': 'Advertencia',
        'PROMO': 'Promoción'
      };
      return labelMap[type] || type;
    };

    const getAudienceClass = (audience) => {
      const audienceMap = {
        'ALL': 'all',
        'NEW_USERS': 'new',
        'ADMINS': 'admin'
      };
      return audienceMap[audience] || 'all';
    };

    const getAudienceLabel = (audience) => {
      const labelMap = {
        'ALL': 'Todos',
        'NEW_USERS': 'Nuevos usuarios',
        'ADMINS': 'Administradores'
      };
      return labelMap[audience] || audience;
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
      displayName,
      userEmail,
      announcements,
      currentUserProfile,
      toggleUserMenu,
      toggleNotifications,
      handleViewAllNotifications,
      handleSettings,
      handleLogout,
      formatDate,
      goToAnnouncement,
      getTypeClass,
      getTypeLabel,
      getAudienceClass,
      getAudienceLabel
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
  width: 12px;
  height: 12px;
  border-radius: 50%;
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
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
}

.icon-alert {
  background: linear-gradient(135deg, #ef4444 0%, #dc2626 100%);
}

.icon-info {
  background: linear-gradient(135deg, #3b82f6 0%, #2563eb 100%);
}

.icon-warning {
  background: linear-gradient(135deg, #f59e0b 0%, #d97706 100%);
}

.icon-promo {
  background: linear-gradient(135deg, #10b981 0%, #059669 100%);
}

.notification-content {
  flex: 1;
  min-width: 0;
}

.notification-header-row {
  display: flex;
  margin-bottom: 4px;
}

.notification-badges {
  display: flex;
  gap: 4px;
  flex-shrink: 0;
}

.notification-footer-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 8px;
  margin-top: 8px;
}

.badge-small {
  padding: 2px 8px;
  border-radius: 12px;
  font-size: 10px;
  font-weight: 600;
  white-space: nowrap;
}

.badge-alert {
  background: #fee2e2;
  color: #991b1b;
}

.badge-info {
  background: #dbeafe;
  color: #1e40af;
}

.badge-warning {
  background: #fef3c7;
  color: #92400e;
}

.badge-promo {
  background: #d1fae5;
  color: #065f46;
}

.badge-audience-all {
  background: #e0e7ff;
  color: #3730a3;
}

.badge-audience-new {
  background: #fce7f3;
  color: #9f1239;
}

.badge-audience-admin {
  background: #f3e8ff;
  color: #6b21a8;
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