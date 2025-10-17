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
            @input="handleSearchInput"
          />

          <!-- Resultados de búsqueda -->
          <div v-if="showSearchResults && searchResults.length > 0" class="search-results-dropdown">
            <div class="search-results-header">
              <span>Resultados ({{ searchResults.length }})</span>
              <button @click="closeSearchResults" class="close-search-btn">×</button>
            </div>
            <div class="search-results-list">
              <div
                v-for="result in searchResults"
                :key="result.id"
                @click="handleResultClick(result)"
                class="search-result-item"
              >
                <div class="result-icon">
                  <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                    <path d="M4 19.5v-15A2.5 2.5 0 0 1 6.5 2H20v20H6.5a2.5 2.5 0 0 1 0-5H20"/>
                  </svg>
                </div>
                <div class="result-content">
                  <div class="result-title">{{ result.title }}</div>
                  <div class="result-meta">
                    por {{ result.userProfile?.displayName || 'Desconocido' }} •
                    {{ result.books?.length || 0 }} libros
                  </div>
                </div>
              </div>
            </div>
            <div class="search-results-footer">
              <button @click="viewAllResults" class="view-all-search-btn">
                Ver todos los resultados
              </button>
            </div>
          </div>

          <div v-else-if="showSearchResults && searchQuery.length > 0 && searchResults.length === 0" class="search-results-dropdown">
            <div class="no-results">
              <svg xmlns="http://www.w3.org/2000/svg" width="32" height="32" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <circle cx="11" cy="11" r="8"/>
                <path d="m21 21-4.35-4.35"/>
              </svg>
              <p>No se encontraron resultados</p>
            </div>
          </div>
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
          </div>
        </div>

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
import { ref, onMounted, onUnmounted } from 'vue';
import { useRouter } from 'vue-router';
import { announcementsAPI } from '../api/usersService';
import { publicationsAPI } from '../api/publicationsService';

export default {
  name: "Topbar",
  setup() {
    const router = useRouter();
    const searchQuery = ref('');
    const showUserMenu = ref(false);
    const showNotifications = ref(false);
    const showSearchResults = ref(false);
    const userName = ref('Usuario');
    const userInitial = ref('U');
    const announcements = ref([]);
    const searchResults = ref([]);
    const searchTimeout = ref(null);

    const toggleUserMenu = () => {
      showUserMenu.value = !showUserMenu.value;
      showNotifications.value = false;
    };

    const toggleNotifications = () => {
      showNotifications.value = !showNotifications.value;
      showUserMenu.value = false;
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

    const handleSearchInput = () => {
      clearTimeout(searchTimeout.value);

      if (searchQuery.value.length < 2) {
        showSearchResults.value = false;
        searchResults.value = [];
        return;
      }

      searchTimeout.value = setTimeout(async () => {
        await performSearch();
      }, 500);
    };

    const performSearch = async () => {
      try {
        const query = searchQuery.value.trim();
        if (!query) {
          showSearchResults.value = false;
          return;
        }

        const results = [];

        // Buscar por título
        try {
          const titleResponse = await publicationsAPI.searchByTitle(query, 0, 5);
          if (titleResponse.data?.data) {
            results.push(...titleResponse.data.data);
          }
        } catch (error) {
          console.log('No results by title');
        }

        // Buscar por descripción
        try {
          const descResponse = await publicationsAPI.searchByDescription(query, 0, 5);
          if (descResponse.data?.data) {
            results.push(...descResponse.data.data);
          }
        } catch (error) {
          console.log('No results by description');
        }

        // Eliminar duplicados
        const uniqueResults = Array.from(
          new Map(results.map(item => [item.id, item])).values()
        );

        searchResults.value = uniqueResults.slice(0, 5);
        showSearchResults.value = true;
      } catch (error) {
        console.error('Error performing search:', error);
        searchResults.value = [];
        showSearchResults.value = true;
      }
    };

    const handleSearch = async () => {
      if (searchQuery.value.trim()) {
        await performSearch();
        // Navegar a explorar con el término de búsqueda
        router.push({
          path: '/explore'
        });
        closeSearchResults();
      }
    };

    const handleResultClick = (result) => {
      console.log('Result clicked:', result);
      closeSearchResults();
      // TODO: Navigate to publication detail
    };

    const viewAllResults = () => {
      router.push({
        path: '/explore'
      });
      closeSearchResults();
    };

    const closeSearchResults = () => {
      showSearchResults.value = false;
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
      if (showSearchResults.value && !event.target.closest('.search-box')) {
        showSearchResults.value = false;
      }
    };

    onMounted(() => {
      document.addEventListener('click', closeMenuOnClickOutside);
      loadAnnouncements();
    });

    onUnmounted(() => {
      document.removeEventListener('click', closeMenuOnClickOutside);
    });

    return {
      searchQuery,
      showUserMenu,
      showNotifications,
      showSearchResults,
      userName,
      userInitial,
      announcements,
      searchResults,
      toggleUserMenu,
      toggleNotifications,
      handleSearchInput,
      handleSearch,
      handleResultClick,
      viewAllResults,
      closeSearchResults,
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

/* Search Results Dropdown */
.search-results-dropdown {
  position: absolute;
  top: calc(100% + 8px);
  left: 0;
  right: 0;
  background: white;
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
  z-index: 1001;
  max-height: 400px;
  overflow: hidden;
  animation: dropdownFade 0.2s;
}

.search-results-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 16px;
  border-bottom: 1px solid #e2e8f0;
  font-size: 13px;
  font-weight: 600;
  color: #4a5568;
}

.close-search-btn {
  background: none;
  border: none;
  font-size: 24px;
  color: #718096;
  cursor: pointer;
  padding: 0;
  width: 24px;
  height: 24px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 4px;
  transition: all 0.2s;
}

.close-search-btn:hover {
  background: #f7fafc;
  color: #1a202c;
}

.search-results-list {
  max-height: 300px;
  overflow-y: auto;
}

.search-result-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 16px;
  cursor: pointer;
  transition: background 0.2s;
}

.search-result-item:hover {
  background: #f7fafc;
}

.result-icon {
  width: 36px;
  height: 36px;
  border-radius: 8px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  flex-shrink: 0;
}

.result-content {
  flex: 1;
  min-width: 0;
}

.result-title {
  font-size: 14px;
  font-weight: 500;
  color: #1a202c;
  margin-bottom: 2px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.result-meta {
  font-size: 12px;
  color: #718096;
}

.search-results-footer {
  border-top: 1px solid #e2e8f0;
  padding: 12px 16px;
}

.view-all-search-btn {
  width: 100%;
  padding: 8px;
  background: #f7fafc;
  border: none;
  border-radius: 6px;
  color: #667eea;
  font-size: 13px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s;
}

.view-all-search-btn:hover {
  background: #edf2f7;
}

.no-results {
  padding: 40px 20px;
  text-align: center;
  color: #718096;
}

.no-results svg {
  margin-bottom: 12px;
  opacity: 0.5;
}

.no-results p {
  margin: 0;
  font-size: 14px;
}

/* Notifications */
.notifications-menu {
  position: relative;
}

.notifications-dropdown {
  position: absolute;
  top: calc(100% + 8px);
  right: 0;
  background: white;
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
  min-width: 360px;
  max-width: 420px;
  max-height: 500px;
  z-index: 1000;
  animation: dropdownFade 0.2s;
}

.notifications-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px;
  border-bottom: 1px solid #e2e8f0;
}

.notifications-header h3 {
  margin: 0;
  font-size: 16px;
  font-weight: 600;
  color: #1a202c;
}

.close-notifications-btn {
  background: none;
  border: none;
  font-size: 28px;
  color: #718096;
  cursor: pointer;
  padding: 0;
  width: 28px;
  height: 28px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 4px;
  transition: all 0.2s;
  line-height: 1;
}

.close-notifications-btn:hover {
  background: #f7fafc;
  color: #1a202c;
}

.notifications-list {
  max-height: 400px;
  overflow-y: auto;
}

.no-notifications {
  padding: 60px 20px;
  text-align: center;
  color: #718096;
}

.no-notifications svg {
  margin-bottom: 16px;
  opacity: 0.5;
}

.no-notifications p {
  margin: 0;
  font-size: 14px;
}

.notification-item {
  display: flex;
  gap: 12px;
  padding: 16px;
  border-bottom: 1px solid #e2e8f0;
  transition: background 0.2s;
}

.notification-item:last-child {
  border-bottom: none;
}

.notification-item:hover {
  background: #f7fafc;
}

.notification-icon {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  background: #edf2f7;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #667eea;
  flex-shrink: 0;
}

.notification-content {
  flex: 1;
  min-width: 0;
}

.notification-content h4 {
  font-size: 14px;
  font-weight: 600;
  color: #1a202c;
  margin: 0 0 4px 0;
}

.notification-content p {
  font-size: 13px;
  color: #4a5568;
  margin: 0 0 6px 0;
  line-height: 1.5;
}

.notification-time {
  font-size: 11px;
  color: #a0aec0;
}
</style>