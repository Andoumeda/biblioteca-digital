<template>
  <div class="announcement-detail-container">
    <button @click="goBack" class="back-btn">
      <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
        <polyline points="15 18 9 12 15 6"/>
      </svg>
      Volver
    </button>

    <div v-if="loading" class="loading-state">
      <svg xmlns="http://www.w3.org/2000/svg" width="48" height="48" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" class="spinner">
        <path d="M21 12a9 9 0 1 1-6.219-8.56"/>
      </svg>
      <p>Cargando anuncio...</p>
    </div>

    <div v-else-if="error" class="error-message">
      {{ error }}
    </div>

    <div v-else-if="announcement" class="announcement-detail">
      <div class="announcement-header">
        <div class="announcement-icon">
          <svg xmlns="http://www.w3.org/2000/svg" width="32" height="32" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M10.5 5H6a2 2 0 0 0-2 2v12a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2v-4.5"/>
            <path d="M17 3l4 4L10 18l-5 1 1-5Z"/>
          </svg>
        </div>
        <div class="header-content">
          <h1 class="announcement-title">{{ announcement.title }}</h1>
          <div class="announcement-meta">
            <span class="announcement-date">
              <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <path d="M8 2v4"/>
                <path d="M16 2v4"/>
                <rect width="18" height="18" x="3" y="4" rx="2"/>
                <path d="M3 10h18"/>
              </svg>
              {{ formatDate(announcement.createdAt) }}
            </span>
            <span class="announcement-author">
              <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <path d="M19 21v-2a4 4 0 0 0-4-4H9a4 4 0 0 0-4 4v2"/>
                <circle cx="12" cy="7" r="4"/>
              </svg>
              {{ announcement.userProfile?.displayName || 'Administrador' }}
            </span>
          </div>
        </div>
      </div>

      <div class="announcement-body">
        <div class="announcement-message">
          {{ announcement.message }}
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { useAnnouncementsStore } from '../stores/announcements';

export default {
  name: 'AnnouncementDetail',
  setup() {
    const route = useRoute();
    const router = useRouter();
    const store = useAnnouncementsStore();

    const announcement = ref(null);
    const loading = ref(false);
    const error = ref(null);

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

    const goBack = () => {
      router.back();
    };

    const loadAnnouncement = async () => {
      loading.value = true;
      error.value = null;

      try {
        const id = route.params.id;
        announcement.value = await store.fetchAnnouncementById(id);
      } catch (err) {
        error.value = 'Error al cargar el anuncio';
        console.error('Error loading announcement:', err);
      } finally {
        loading.value = false;
      }
    };

    onMounted(() => {
      loadAnnouncement();
    });

    return {
      announcement,
      loading,
      error,
      formatDate,
      goBack
    };
  }
};
</script>

<style scoped>
.announcement-detail-container {
  padding: 24px;
  max-width: 900px;
  margin: 0 auto;
}

.back-btn {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 16px;
  background: white;
  border: 1px solid #e2e8f0;
  border-radius: 8px;
  color: #4a5568;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s;
  margin-bottom: 24px;
}

.back-btn:hover {
  background: #f7fafc;
  border-color: #cbd5e0;
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

.announcement-detail {
  background: white;
  border: 1px solid #e2e8f0;
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
}

.announcement-header {
  display: flex;
  gap: 20px;
  padding: 32px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
}

.announcement-icon {
  flex-shrink: 0;
  width: 64px;
  height: 64px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(255, 255, 255, 0.2);
  border-radius: 16px;
  backdrop-filter: blur(10px);
}

.header-content {
  flex: 1;
}

.announcement-title {
  font-size: 32px;
  font-weight: bold;
  color: white;
  margin-bottom: 12px;
  line-height: 1.3;
}

.announcement-meta {
  display: flex;
  flex-wrap: wrap;
  gap: 20px;
  font-size: 14px;
  color: rgba(255, 255, 255, 0.9);
}

.announcement-date,
.announcement-author {
  display: flex;
  align-items: center;
  gap: 6px;
}

.announcement-body {
  padding: 32px;
}

.announcement-message {
  font-size: 16px;
  line-height: 1.8;
  color: #2d3748;
  white-space: pre-wrap;
}

@media (max-width: 640px) {
  .announcement-header {
    flex-direction: column;
    text-align: center;
    align-items: center;
  }

  .announcement-title {
    font-size: 24px;
  }

  .announcement-meta {
    justify-content: center;
  }
}
</style>

