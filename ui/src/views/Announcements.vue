<template>
  <div class="announcements-container">
    <div class="announcements-header">
      <h2 class="announcements-title">Anuncios</h2>
      <p class="announcements-subtitle">Mantente al día con las últimas novedades</p>
    </div>

    <div v-if="store.loading" class="loading-state">
      <svg xmlns="http://www.w3.org/2000/svg" width="48" height="48" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2" class="spinner">
        <path d="M21 12a9 9 0 1 1-6.219-8.56"/>
      </svg>
      <p>Cargando anuncios...</p>
    </div>

    <div v-else-if="store.error" class="error-message">
      {{ store.error }}
    </div>

    <div v-else-if="store.announcements.length > 0" class="announcements-list">
      <div
        v-for="announcement in store.announcements"
        :key="announcement.id"
        class="announcement-card"
        @click="goToDetail(announcement.id)"
      >
        <div class="announcement-icon">
          <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
            <path d="M10.5 5H6a2 2 0 0 0-2 2v12a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2v-4.5"/>
            <path d="M17 3l4 4L10 18l-5 1 1-5Z"/>
          </svg>
        </div>
        <div class="announcement-content">
          <h3 class="announcement-title">{{ announcement.title }}</h3>
          <p class="announcement-message">{{ announcement.message }}</p>
          <div class="announcement-meta">
            <span class="announcement-date">{{ formatDate(announcement.createdAt) }}</span>
            <span class="announcement-author">Por: {{ announcement.userProfile?.displayName || 'Administrador' }}</span>
          </div>
        </div>
      </div>
    </div>

    <div v-else class="no-results">
      <svg xmlns="http://www.w3.org/2000/svg" width="64" height="64" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
        <path d="M10.5 5H6a2 2 0 0 0-2 2v12a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2v-4.5"/>
        <path d="M17 3l4 4L10 18l-5 1 1-5Z"/>
      </svg>
      <p>No hay anuncios disponibles</p>
    </div>

    <!-- Paginación -->
    <div class="pagination" v-if="!store.loading && store.totalPages > 1">
      <button @click="store.previousPage()" :disabled="store.currentPage === 0" class="pagination-btn">
        <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
          <polyline points="15 18 9 12 15 6"/>
        </svg>
        Anterior
      </button>
      <span class="pagination-info">Página {{ store.currentPage + 1 }} de {{ store.totalPages }}</span>
      <button @click="store.nextPage()" :disabled="!store.hasMore" class="pagination-btn">
        Siguiente
        <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
          <polyline points="9 18 15 12 9 6"/>
        </svg>
      </button>
    </div>
  </div>
</template>

<script>
import { useAnnouncementsStore } from '../stores/announcements';
import { onMounted } from 'vue';
import { useRouter } from 'vue-router';

export default {
  name: 'Announcements',
  setup() {
    const store = useAnnouncementsStore();
    const router = useRouter();

    const formatDate = (dateString) => {
      if (!dateString) return '';
      const date = new Date(dateString);
      return date.toLocaleDateString('es-ES', {
        year: 'numeric',
        month: 'long',
        day: 'numeric',
        hour: '2-digit',
        minute: '2-digit'
      });
    };

    const goToDetail = (id) => {
      router.push({ name: 'AnnouncementDetail', params: { id } });
    };

    onMounted(async () => {
      await store.fetchAnnouncements();
      store.announcements = store.announcements.sort((a, b) => new Date(b.createdAt) - new Date(a.createdAt));
    });

    return {
      store,
      formatDate,
      goToDetail
    };
  }
};
</script>

<style scoped>
.announcements-container {
  padding: 24px;
  width: 100%;
  max-width: 1000px;
  margin: 5% auto;
}

.announcements-header {
  margin-bottom: 32px;
  text-align: center;
}

.announcements-title {
  font-size: 36px;
  font-weight: bold;
  color: #1a202c;
  margin-bottom: 12px;
}

.announcements-subtitle {
  color: #718096;
  font-size: 18px;
}

.loading-state {
  text-align: center;
  padding: 80px 20px;
  color: #718096;
}

.spinner {
  margin: 0 auto 16px;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}

.error-message {
  background-color: #fee;
  color: #c33;
  padding: 1rem;
  border-radius: 8px;
  margin-bottom: 1rem;
}

.announcements-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
  margin-bottom: 32px;
}

.announcement-card {
  display: flex;
  gap: 16px;
  padding: 20px;
  background: white;
  border: 1px solid #e2e8f0;
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.3s;
}

.announcement-card:hover {
  transform: translateX(4px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  border-color: #667eea;
}

.announcement-icon {
  flex-shrink: 0;
  width: 48px;
  height: 48px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border-radius: 12px;
}

.announcement-content {
  flex: 1;
}

.announcement-title {
  font-size: 20px;
  font-weight: 600;
  color: #1a202c;
  margin-bottom: 8px;
}

.announcement-message {
  color: #4a5568;
  font-size: 15px;
  line-height: 1.6;
  margin-bottom: 12px;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.announcement-meta {
  display: flex;
  gap: 16px;
  font-size: 13px;
  color: #718096;
}

.announcement-date,
.announcement-author {
  display: flex;
  align-items: center;
}

.no-results {
  text-align: center;
  padding: 80px 20px;
  color: #718096;
}

.no-results svg {
  margin: 0 auto 16px;
  opacity: 0.5;
}

.no-results p {
  font-size: 18px;
  margin: 0;
}

.pagination {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 20px;
  padding: 24px;
}

.pagination-btn {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px 20px;
  background: #667eea;
  color: white;
  border: none;
  border-radius: 8px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s;
}

.pagination-btn:hover:not(:disabled) {
  background: #5568d3;
  transform: translateY(-2px);
}

.pagination-btn:disabled {
  background: #cbd5e0;
  cursor: not-allowed;
  transform: none;
}

.pagination-info {
  font-weight: 500;
  color: #4a5568;
}
</style>
