<template>
  <div class="announcements-container">
    <div class="announcements-header">
      <div>
        <h2 class="announcements-title">Anuncios</h2>
        <p class="announcements-subtitle">Mantente al día con las últimas novedades</p>
      </div>
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
        <div :class="['announcement-icon', `icon-${getTypeClass(announcement.type)}`]">
          <svg v-if="announcement.type === 'ALERT'" xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M10.29 3.86L1.82 18a2 2 0 0 0 1.71 3h16.94a2 2 0 0 0 1.71-3L13.71 3.86a2 2 0 0 0-3.42 0z"/>
            <line x1="12" y1="9" x2="12" y2="13"/>
            <line x1="12" y1="17" x2="12.01" y2="17"/>
          </svg>
          <svg v-else-if="announcement.type === 'INFO'" xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <circle cx="12" cy="12" r="10"/>
            <line x1="12" y1="16" x2="12" y2="12"/>
            <line x1="12" y1="8" x2="12.01" y2="8"/>
          </svg>
          <svg v-else-if="announcement.type === 'WARNING'" xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M10.29 3.86L1.82 18a2 2 0 0 0 1.71 3h16.94a2 2 0 0 0 1.71-3L13.71 3.86a2 2 0 0 0-3.42 0z"/>
            <line x1="12" y1="9" x2="12" y2="13"/>
            <line x1="12" y1="17" x2="12.01" y2="17"/>
          </svg>
          <svg v-else-if="announcement.type === 'PROMO'" xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M21 2l-2 2m-7.61 7.61a5.5 5.5 0 1 1-7.778 7.778 5.5 5.5 0 0 1 7.777-7.777zm0 0L15.5 7.5m0 0l3 3L22 7l-3-3m-3.5 3.5L19 4"/>
          </svg>
          <svg v-else xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M10.5 5H6a2 2 0 0 0-2 2v12a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2v-4.5"/>
            <path d="M17 3l4 4L10 18l-5 1 1-5Z"/>
          </svg>
        </div>
        <div class="announcement-content">
          <div class="announcement-header-row">
            <h3 class="announcement-title">{{ announcement.title }}</h3>
            <div class="announcement-badges">
              <span :class="['badge', `badge-${getTypeClass(announcement.type)}`]">
                {{ getTypeLabel(announcement.type) }}
              </span>
              <span :class="['badge', `badge-audience-${getAudienceClass(announcement.targetAudience)}`]">
                {{ getAudienceLabel(announcement.targetAudience) }}
              </span>
            </div>
          </div>
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
import { ref, onMounted } from 'vue';
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

    onMounted(async () => {
      await store.fetchAnnouncements();
      store.announcements = store.announcements.sort((a, b) => new Date(b.createdAt) - new Date(a.createdAt));
    });

    return {
      store,
      formatDate,
      goToDetail,
      getTypeClass,
      getTypeLabel,
      getAudienceClass,
      getAudienceLabel
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
  display: flex;
  justify-content: space-between;
  align-items: center;
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

.btn-create {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px 24px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border: none;
  border-radius: 8px;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s;
}

.btn-create:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.3);
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
  width: 56px;
  height: 56px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 12px;
  transition: all 0.3s;
}

.icon-alert {
  background: linear-gradient(135deg, #ef4444 0%, #dc2626 100%);
  color: white;
}

.icon-info {
  background: linear-gradient(135deg, #3b82f6 0%, #2563eb 100%);
  color: white;
}

.icon-warning {
  background: linear-gradient(135deg, #f59e0b 0%, #d97706 100%);
  color: white;
}

.icon-promo {
  background: linear-gradient(135deg, #10b981 0%, #059669 100%);
  color: white;
}

.announcement-content {
  flex: 1;
  min-width: 0;
}

.announcement-header-row {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 16px;
  margin-bottom: 8px;
}

.announcement-badges {
  display: flex;
  gap: 8px;
  flex-shrink: 0;
  flex-wrap: wrap;
}

.badge {
  padding: 4px 12px;
  border-radius: 16px;
  font-size: 12px;
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

/* Modal Styles */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.7);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.modal-content {
  background: white;
  padding: 32px;
  border-radius: 16px;
  max-width: 600px;
  width: 90%;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
}

.modal-content h3 {
  font-size: 24px;
  font-weight: 700;
  color: #1a202c;
  margin: 0 0 24px 0;
}

.announcement-form {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.form-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 16px;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.form-group label {
  font-size: 14px;
  font-weight: 600;
  color: #2d3748;
}

.form-input {
  width: 100%;
  padding: 12px 16px;
  border: 2px solid #e2e8f0;
  border-radius: 8px;
  font-size: 14px;
  font-family: inherit;
  transition: all 0.2s;
}

.form-input:focus {
  outline: none;
  border-color: #667eea;
  box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
}

.form-textarea {
  resize: vertical;
  min-height: 120px;
  line-height: 1.6;
}

.form-select {
  cursor: pointer;
  background-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' width='12' height='12' viewBox='0 0 12 12'%3E%3Cpath fill='%234a5568' d='M6 9L1 4h10z'/%3E%3C/svg%3E");
  background-repeat: no-repeat;
  background-position: right 12px center;
  padding-right: 40px;
  appearance: none;
}

.form-select option {
  padding: 8px;
}

.form-actions {
  display: flex;
  gap: 12px;
  justify-content: flex-end;
  margin-top: 8px;
}

.btn {
  padding: 12px 24px;
  border-radius: 8px;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s;
  border: none;
}

.btn-secondary {
  background: #e2e8f0;
  color: #4a5568;
}

.btn-secondary:hover {
  background: #cbd5e0;
}

.btn-primary {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
}

.btn-primary:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.3);
}

.btn-primary:disabled {
  opacity: 0.6;
  cursor: not-allowed;
  transform: none;
}
</style>