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

      <div class="topbar-center">
        <div class="search-box">
          <svg class="search-icon" xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <circle cx="11" cy="11" r="8"/>
            <path d="m21 21-4.35-4.35"/>
          </svg>
          <input
            v-model="searchQuery"
            type="text"
            placeholder="Buscar libros, publicaciones..."
            class="search-input"
            @keyup.enter="handleSearch"
          />
        </div>
      </div>

      <div class="topbar-right">
        <button class="icon-button" title="Notificaciones">
          <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M6 8a6 6 0 0 1 12 0c0 7 3 9 3 9H3s3-2 3-9"/>
            <path d="M10.3 21a1.94 1.94 0 0 0 3.4 0"/>
          </svg>
          <span v-if="notificationCount > 0" class="notification-badge">{{ notificationCount }}</span>
        </button>

        <div class="user-menu">
          <button @click="toggleUserMenu" class="user-button">
            <div class="user-avatar">
              {{ userInitial }}
            </div>
            <span class="user-name">{{ userName }}</span>
            <svg class="chevron-icon" xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <polyline points="6 9 12 15 18 9"/>
            </svg>
          </button>

          <div v-if="showUserMenu" class="user-dropdown" @click.stop>
            <div class="dropdown-header">
              <div class="user-info">
                <p class="user-display-name">{{ userName }}</p>
                <p class="user-email">usuario@ejemplo.com</p>
              </div>
            </div>
            <div class="dropdown-divider"></div>
            <button @click="handleProfile" class="dropdown-item">
              <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <path d="M19 21v-2a4 4 0 0 0-4-4H9a4 4 0 0 0-4 4v2"/>
                <circle cx="12" cy="7" r="4"/>
              </svg>
              Mi Perfil
            </button>
            <button @click="handleSettings" class="dropdown-item">
              <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <path d="M12.22 2h-.44a2 2 0 0 0-2 2v.18a2 2 0 0 1-1 1.73l-.43.25a2 2 0 0 1-2 0l-.15-.08a2 2 0 0 0-2.73.73l-.22.38a2 2 0 0 0 .73 2.73l.15.1a2 2 0 0 1 1 1.72v.51a2 2 0 0 1-1 1.74l-.15.09a2 2 0 0 0-.73 2.73l.22.38a2 2 0 0 0 2.73.73l.15-.08a2 2 0 0 1 2 0l.43.25a2 2 0 0 1 1 1.73V20a2 2 0 0 0 2 2h.44a2 2 0 0 0 2-2v-.18a2 2 0 0 1 1-1.73l.43-.25a2 2 0 0 1 2 0l.15.08a2 2 0 0 0 2.73-.73l.22-.39a2 2 0 0 0-.73-2.73l-.15-.08a2 2 0 0 1-1-1.74v-.5a2 2 0 0 1 1-1.74l.15-.09a2 2 0 0 0 .73-2.73l-.22-.38a2 2 0 0 0-2.73-.73l-.15.08a2 2 0 0 1-2 0l-.43-.25a2 2 0 0 1-1-1.73V4a2 2 0 0 0-2-2z"/>
                <circle cx="12" cy="12" r="3"/>
              </svg>
              Configuración
            </button>
            <div class="dropdown-divider"></div>
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
import { ref, onMounted, onUnmounted } from 'vue';
import { useRouter } from 'vue-router';

export default {
  name: "Topbar",
  setup() {
    const router = useRouter();
    const searchQuery = ref('');
    const showUserMenu = ref(false);
    const notificationCount = ref(3);
    const userName = ref('Usuario');
    const userInitial = ref('U');

    const toggleUserMenu = () => {
      showUserMenu.value = !showUserMenu.value;
    };

    const handleSearch = () => {
      console.log('Search:', searchQuery.value);
      // TODO: Implement search functionality
    };

    const handleProfile = () => {
      showUserMenu.value = false;
      console.log('Navigate to profile');
      // TODO: Navigate to profile page
    };

    const handleSettings = () => {
      showUserMenu.value = false;
      console.log('Navigate to settings');
      // TODO: Navigate to settings page
    };

    const handleLogout = () => {
      showUserMenu.value = false;
      router.push('/login');
    };

    const closeMenuOnClickOutside = (event) => {
      if (showUserMenu.value && !event.target.closest('.user-menu')) {
        showUserMenu.value = false;
      }
    };

    onMounted(() => {
      document.addEventListener('click', closeMenuOnClickOutside);
    });

    onUnmounted(() => {
      document.removeEventListener('click', closeMenuOnClickOutside);
    });

    return {
      searchQuery,
      showUserMenu,
      notificationCount,
      userName,
      userInitial,
      toggleUserMenu,
      handleSearch,
      handleProfile,
      handleSettings,
      handleLogout
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

.topbar-center {
  flex: 1;
  max-width: 600px;
  margin: 0 24px;
}

.search-box {
  position: relative;
  width: 100%;
}

.search-icon {
  position: absolute;
  left: 14px;
  top: 50%;
  transform: translateY(-50%);
  color: #a0aec0;
  pointer-events: none;
}

.search-input {
  width: 100%;
  padding: 10px 14px 10px 42px;
  border: none;
  border-radius: 8px;
  font-size: 14px;
  background: rgba(255, 255, 255, 0.95);
  transition: all 0.2s;
}

.search-input:focus {
  outline: none;
  background: white;
  box-shadow: 0 0 0 3px rgba(255, 255, 255, 0.2);
}

.topbar-right {
  flex: 0 0 auto;
  display: flex;
  align-items: center;
  gap: 16px;
}

.icon-button {
  position: relative;
  background: rgba(255, 255, 255, 0.15);
  border: none;
  border-radius: 8px;
  padding: 10px;
  color: white;
  cursor: pointer;
  transition: all 0.2s;
  display: flex;
  align-items: center;
  justify-content: center;
}

.icon-button:hover {
  background: rgba(255, 255, 255, 0.25);
}

.notification-badge {
  position: absolute;
  top: 4px;
  right: 4px;
  background: #ef4444;
  color: white;
  font-size: 10px;
  font-weight: 600;
  padding: 2px 5px;
  border-radius: 10px;
  min-width: 16px;
  text-align: center;
}

.user-menu {
  position: relative;
}

.user-button {
  display: flex;
  align-items: center;
  gap: 10px;
  background: rgba(255, 255, 255, 0.15);
  border: none;
  border-radius: 8px;
  padding: 6px 12px 6px 6px;
  color: white;
  cursor: pointer;
  transition: all 0.2s;
}

.user-button:hover {
  background: rgba(255, 255, 255, 0.25);
}

.user-avatar {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  background: white;
  color: #667eea;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 600;
  font-size: 16px;
}

.user-name {
  font-size: 14px;
  font-weight: 500;
}

.chevron-icon {
  transition: transform 0.2s;
}

.user-button:hover .chevron-icon {
  transform: translateY(2px);
}

.user-dropdown {
  position: absolute;
  top: calc(100% + 8px);
  right: 0;
  background: white;
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
  min-width: 220px;
  z-index: 1000;
  animation: dropdownFade 0.2s;
}

@keyframes dropdownFade {
  from {
    opacity: 0;
    transform: translateY(-10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.dropdown-header {
  padding: 16px;
}

.user-info {
  margin: 0;
}

.user-display-name {
  font-weight: 600;
  color: #1a202c;
  margin: 0 0 4px 0;
  font-size: 15px;
}

.user-email {
  font-size: 13px;
  color: #718096;
  margin: 0;
}

.dropdown-divider {
  height: 1px;
  background: #e2e8f0;
  margin: 0;
}

.dropdown-item {
  display: flex;
  align-items: center;
  gap: 12px;
  width: 100%;
  padding: 12px 16px;
  border: none;
  background: none;
  color: #4a5568;
  font-size: 14px;
  text-align: left;
  cursor: pointer;
  transition: all 0.2s;
}

.dropdown-item:hover {
  background: #f7fafc;
  color: #1a202c;
}

.dropdown-item.logout {
  color: #ef4444;
}

.dropdown-item.logout:hover {
  background: #fef2f2;
}

@media (max-width: 768px) {
  .topbar-center {
    display: none;
  }

  .user-name {
    display: none;
  }
}
</style>