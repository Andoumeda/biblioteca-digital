<template>
  <div class="announcement-detail-container">
    <button @click="goBack" class="back-btn">
      <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
        <polyline points="15 18 9 12 15 6"/>
      </svg>
      Todos los anuncios
    </button>

    <div v-if="loading" class="loading-state">
      <svg xmlns="http://www.w3.org/2000/svg" width="48" height="48" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2" class="spinner">
        <path d="M21 12a9 9 0 1 1-6.219-8.56"/>
      </svg>
      <p>Cargando anuncio...</p>
    </div>

    <div v-else-if="error" class="error-message">
      {{ error }}
    </div>

    <div v-else-if="announcement" class="announcement-detail">
      <div class="announcement-header">
        <div :class="['announcement-icon', `icon-${getTypeClass(announcement.type)}`]">
          <svg v-if="announcement.type === 'ALERT'" xmlns="http://www.w3.org/2000/svg" width="32" height="32" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M10.29 3.86L1.82 18a2 2 0 0 0 1.71 3h16.94a2 2 0 0 0 1.71-3L13.71 3.86a2 2 0 0 0-3.42 0z"/>
            <line x1="12" y1="9" x2="12" y2="13"/>
            <line x1="12" y1="17" x2="12.01" y2="17"/>
          </svg>
          <svg v-else-if="announcement.type === 'INFO'" xmlns="http://www.w3.org/2000/svg" width="32" height="32" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <circle cx="12" cy="12" r="10"/>
            <line x1="12" y1="16" x2="12" y2="12"/>
            <line x1="12" y1="8" x2="12.01" y2="8"/>
          </svg>
          <svg v-else-if="announcement.type === 'WARNING'" xmlns="http://www.w3.org/2000/svg" width="32" height="32" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M10.29 3.86L1.82 18a2 2 0 0 0 1.71 3h16.94a2 2 0 0 0 1.71-3L13.71 3.86a2 2 0 0 0-3.42 0z"/>
            <line x1="12" y1="9" x2="12" y2="13"/>
            <line x1="12" y1="17" x2="12.01" y2="17"/>
          </svg>
          <svg v-else-if="announcement.type === 'PROMO'" xmlns="http://www.w3.org/2000/svg" width="32" height="32" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M21 2l-2 2m-7.61 7.61a5.5 5.5 0 1 1-7.778 7.778 5.5 5.5 0 0 1 7.777-7.777zm0 0L15.5 7.5m0 0l3 3L22 7l-3-3m-3.5 3.5L19 4"/>
          </svg>
          <svg v-else xmlns="http://www.w3.org/2000/svg" width="32" height="32" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M10.5 5H6a2 2 0 0 0-2 2v12a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2v-4.5"/>
            <path d="M17 3l4 4L10 18l-5 1 1-5Z"/>
          </svg>
        </div>
        <div class="header-content">
          <h1 class="announcement-title">{{ announcement.title }}</h1>
          <div class="announcement-badges-detail">
            <span :class="['badge-detail', `badge-${getTypeClass(announcement.type)}`]">
              {{ getTypeLabel(announcement.type) }}
            </span>
            <span :class="['badge-detail', `badge-audience-${getAudienceClass(announcement.targetAudience)}`]">
              {{ getAudienceLabel(announcement.targetAudience) }}
            </span>
          </div>
          <div class="announcement-meta">
            <span class="announcement-date">
              <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
                <path d="M8 2v4"/>
                <path d="M16 2v4"/>
                <rect width="18" height="18" x="3" y="4" rx="2"/>
                <path d="M3 10h18"/>
              </svg>
              {{ formatDate(announcement.createdAt) }}
            </span>
            <span class="announcement-author">
              <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
                <path d="M19 21v-2a4 4 0 0 0-4-4H9a4 4 0 0 0-4 4v2"/>
                <circle cx="12" cy="7" r="4"/>
              </svg>
              {{ announcement.userProfile?.displayName || 'Administrador' }}
            </span>
          </div>
        </div>
        <div class="header-actions">
          <button @click="showEditModal = true" class="btn-action btn-edit" title="Editar anuncio">
            <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M17 3a2.85 2.83 0 1 1 4 4L7.5 20.5 2 22l1.5-5.5Z"/>
            </svg>
            Editar
          </button>
          <button @click="handleDeleteAnnouncement" class="btn-action btn-delete" title="Eliminar anuncio">
            <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M3 6h18"/>
              <path d="M19 6v14c0 1-1 2-2 2H7c-1 0-2-1-2-2V6"/>
              <path d="M8 6V4c0-1 1-2 2-2h4c1 0 2 1 2 2v2"/>
            </svg>
            Eliminar
          </button>
        </div>
      </div>

      <div class="announcement-body">
        <div class="announcement-message">
          {{ announcement.message }}
        </div>
      </div>
    </div>

    <!-- Modal Editar Anuncio -->
    <div v-if="showEditModal" class="modal-overlay" @click="showEditModal = false">
      <div class="modal-content" @click.stop>
        <h3>Editar Anuncio</h3>
        <form @submit.prevent="handleUpdateAnnouncement" class="announcement-form">
          <div class="form-group">
            <label for="editTitle">Título *</label>
            <input
              id="editTitle"
              v-model="editAnnouncement.title"
              type="text"
              placeholder="Título del anuncio"
              required
              class="form-input"
            />
          </div>
          <div class="form-group">
            <label for="editMessage">Mensaje *</label>
            <textarea
              id="editMessage"
              v-model="editAnnouncement.message"
              placeholder="Escribe el mensaje del anuncio..."
              rows="6"
              required
              class="form-input form-textarea"
            />
          </div>
          <div class="form-row">
            <div class="form-group">
              <label for="editType">Tipo *</label>
              <select
                id="editType"
                v-model="editAnnouncement.type"
                required
                class="form-input form-select"
              >
                <option value="">Selecciona un tipo</option>
                <option value="ALERT">Alerta</option>
                <option value="INFO">Información</option>
                <option value="WARNING">Advertencia</option>
                <option value="PROMO">Promoción</option>
              </select>
            </div>
            <div class="form-group">
              <label for="editTargetAudience">Audiencia *</label>
              <select
                id="editTargetAudience"
                v-model="editAnnouncement.targetAudience"
                required
                class="form-input form-select"
              >
                <option value="">Selecciona una audiencia</option>
                <option value="ALL">Todos</option>
                <option value="NEW_USERS">Nuevos usuarios</option>
                <option value="ADMINS">Administradores</option>
              </select>
            </div>
          </div>
          <div class="form-actions">
            <button type="button" @click="showEditModal = false" class="btn btn-secondary">
              Cancelar
            </button>
            <button type="submit" class="btn btn-primary" :disabled="isSubmitting">
              {{ isSubmitting ? 'Actualizando...' : 'Actualizar Anuncio' }}
            </button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { useAnnouncementsStore } from '../stores/announcements';

// Hardcoded UserProfile ID (por ahora, hasta implementar login)
const HARDCODED_USER_PROFILE_ID = 1;

export default {
  name: 'AnnouncementDetail',
  setup() {
    const route = useRoute();
    const router = useRouter();
    const store = useAnnouncementsStore();

    const announcement = ref(null);
    const loading = ref(false);
    const error = ref(null);
    const showEditModal = ref(false);
    const isSubmitting = ref(false);
    const editAnnouncement = ref({
      title: '',
      message: '',
      type: '',
      targetAudience: ''
    });

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
      router.push('/announcements');
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

    const loadAnnouncement = async () => {
      loading.value = true;
      error.value = null;

      try {
        const id = route.params.id;
        announcement.value = await store.fetchAnnouncementById(id);

        // Cargar datos en el formulario de edición
        editAnnouncement.value = {
          title: announcement.value.title,
          message: announcement.value.message,
          type: announcement.value.type || '',
          targetAudience: announcement.value.targetAudience || ''
        };
      } catch (err) {
        error.value = 'Error al cargar el anuncio';
        console.error('Error loading announcement:', err);
      } finally {
        loading.value = false;
      }
    };

    const handleUpdateAnnouncement = async () => {
      if (!editAnnouncement.value.title || !editAnnouncement.value.message || !editAnnouncement.value.type || !editAnnouncement.value.targetAudience) {
        alert('Por favor completa todos los campos');
        return;
      }

      isSubmitting.value = true;

      try {
        const announcementData = {
          title: editAnnouncement.value.title,
          message: editAnnouncement.value.message,
          type: editAnnouncement.value.type,
          targetAudience: editAnnouncement.value.targetAudience,
          userProfileId: HARDCODED_USER_PROFILE_ID
        };

        await store.updateAnnouncement(route.params.id, announcementData);
        alert('¡Anuncio actualizado exitosamente!');

        showEditModal.value = false;

        // Recargar el anuncio
        await loadAnnouncement();
      } catch (error) {
        console.error('Error updating announcement:', error);
        alert('Error al actualizar el anuncio. Por favor intenta de nuevo.');
      } finally {
        isSubmitting.value = false;
      }
    };

    const handleDeleteAnnouncement = async () => {
      if (!confirm('¿Estás seguro de que deseas eliminar este anuncio? Esta acción no se puede deshacer.')) {
        return;
      }

      try {
        await store.deleteAnnouncement(route.params.id);
        alert('Anuncio eliminado exitosamente');
        router.push('/announcements');
      } catch (error) {
        console.error('Error deleting announcement:', error);
        alert('Error al eliminar el anuncio. Por favor intenta de nuevo.');
      }
    };

    onMounted(() => {
      loadAnnouncement();
    });

    return {
      announcement,
      loading,
      error,
      showEditModal,
      isSubmitting,
      editAnnouncement,
      formatDate,
      goBack,
      getTypeClass,
      getTypeLabel,
      getAudienceClass,
      getAudienceLabel,
      handleUpdateAnnouncement,
      handleDeleteAnnouncement
    };
  }
};
</script>

<style scoped>
.announcement-detail-container {
  padding: 24px;
  width: 100%;
  max-width: 1000px;
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
  border-radius: 16px;
  backdrop-filter: blur(10px);
}

.icon-alert {
  background: rgba(239, 68, 68, 0.2);
}

.icon-info {
  background: rgba(59, 130, 246, 0.2);
}

.icon-warning {
  background: rgba(245, 158, 11, 0.2);
}

.icon-promo {
  background: rgba(16, 185, 129, 0.2);
}

.header-content {
  flex: 1;
}

.announcement-badges-detail {
  display: flex;
  gap: 10px;
  margin-bottom: 12px;
  flex-wrap: wrap;
}

.badge-detail {
  padding: 6px 16px;
  border-radius: 20px;
  font-size: 13px;
  font-weight: 600;
  white-space: nowrap;
}

.badge-alert {
  background: rgba(255, 255, 255, 0.3);
  color: white;
  border: 1px solid rgba(255, 255, 255, 0.5);
}

.badge-info {
  background: rgba(255, 255, 255, 0.3);
  color: white;
  border: 1px solid rgba(255, 255, 255, 0.5);
}

.badge-warning {
  background: rgba(255, 255, 255, 0.3);
  color: white;
  border: 1px solid rgba(255, 255, 255, 0.5);
}

.badge-promo {
  background: rgba(255, 255, 255, 0.3);
  color: white;
  border: 1px solid rgba(255, 255, 255, 0.5);
}

.badge-audience-all {
  background: rgba(255, 255, 255, 0.2);
  color: white;
  border: 1px solid rgba(255, 255, 255, 0.4);
}

.badge-audience-new {
  background: rgba(255, 255, 255, 0.2);
  color: white;
  border: 1px solid rgba(255, 255, 255, 0.4);
}

.badge-audience-admin {
  background: rgba(255, 255, 255, 0.2);
  color: white;
  border: 1px solid rgba(255, 255, 255, 0.4);
}

.header-actions {
  display: flex;
  gap: 12px;
  align-items: flex-start;
}

.btn-action {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px 16px;
  border: none;
  border-radius: 8px;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s;
  color: white;
}

.btn-edit {
  background: rgba(255, 255, 255, 0.2);
  backdrop-filter: blur(10px);
}

.btn-edit:hover {
  background: rgba(255, 255, 255, 0.3);
  transform: translateY(-2px);
}

.btn-delete {
  background: rgba(239, 68, 68, 0.9);
}

.btn-delete:hover {
  background: rgba(220, 38, 38, 1);
  transform: translateY(-2px);
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
  width: 100%;
  font-size: 18px;
  line-height: 1.7;
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
