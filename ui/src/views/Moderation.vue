<template>
  <div class="moderation-container">
    <div class="moderation-header">
      <h2 class="moderation-title">Panel de Moderación</h2>
      <div class="filters">
        <select v-model="filterStatus" class="filter-select">
          <option value="all">Todos los estados</option>
          <option value="pending">Pendientes</option>
          <option value="approved">Aprobados</option>
          <option value="rejected">Rechazados</option>
        </select>
      </div>
    </div>

    <div class="tabs">
      <button
        :class="['tab-button', { active: activeTab === 'publications' }]"
        @click="activeTab = 'publications'"
      >
        Publicaciones
      </button>
      <button
        :class="['tab-button', { active: activeTab === 'reports' }]"
        @click="activeTab = 'reports'"
      >
        Reportes
        <span v-if="pendingReportsCount > 0" class="badge">{{ pendingReportsCount }}</span>
      </button>
      <button
        :class="['tab-button', { active: activeTab === 'users' }]"
        @click="activeTab = 'users'"
      >
        Usuarios
      </button>
    </div>

    <!-- Publications Tab -->
    <div v-if="activeTab === 'publications'" class="tab-content">
      <div class="publications-list">
        <div
          v-for="publication in filteredPublications"
          :key="publication.id"
          class="moderation-card"
        >
          <div class="card-header">
            <div class="publication-info">
              <h3 class="publication-title">{{ publication.title }}</h3>
              <p class="publication-meta">
                Por: <strong>@{{ publication.userProfile?.user?.username || 'Desconocido' }}</strong>
                • {{ formatDate(publication.createdAt) }}
              </p>
            </div>
            <span :class="['status-badge', publication.state]">
              {{ getStatusLabel(publication.state) }}
            </span>
          </div>

          <p class="publication-description">{{ publication.description }}</p>

          <div class="publication-details">
            <div class="detail-item">
              <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <path d="M4 19.5v-15A2.5 2.5 0 0 1 6.5 2H20v20H6.5a2.5 2.5 0 0 1 0-5H20"/>
              </svg>
              <span>{{ publication.books?.length || 0 }} libro(s)</span>
            </div>
            <div class="detail-item">
              <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <path d="M16 21v-2a4 4 0 0 0-4-4H6a4 4 0 0 0-4 4v2"/>
                <circle cx="9" cy="7" r="4"/>
              </svg>
              <span>{{ publication.userProfile?.displayName || 'Desconocido' }}</span>
            </div>
          </div>

          <div class="categories">
            <span
              v-for="category in publication.categories"
              :key="category.id"
              class="category-tag"
            >
              {{ category.name }}
            </span>
          </div>

          <div class="action-buttons">
            <button
              v-if="publication.state === 'pending'"
              @click="handleApprove(publication.id)"
              class="btn btn-approve"
            >
              <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <polyline points="20 6 9 17 4 12"/>
              </svg>
              Aprobar
            </button>
            <button
              v-if="publication.state === 'pending'"
              @click="handleReject(publication.id)"
              class="btn btn-reject"
            >
              <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <line x1="18" x2="6" y1="6" y2="18"/>
                <line x1="6" x2="18" y1="6" y2="18"/>
              </svg>
              Rechazar
            </button>
            <button @click="handleViewDetails(publication)" class="btn btn-view">
              <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <path d="M2 12s3-7 10-7 10 7 10 7-3 7-10 7-10-7-10-7Z"/>
                <circle cx="12" cy="12" r="3"/>
              </svg>
              Ver detalles
            </button>
          </div>
        </div>
      </div>

      <div v-if="filteredPublications.length === 0" class="no-results">
        <p>No hay publicaciones para moderar</p>
      </div>
    </div>

    <!-- Reports Tab -->
    <div v-if="activeTab === 'reports'" class="tab-content">
      <div class="reports-info">
        <p>Sistema de reportes (próximamente)</p>
      </div>
    </div>

    <!-- Users Tab -->
    <div v-if="activeTab === 'users'" class="tab-content">
      <div class="users-info">
        <p>Gestión de usuarios (próximamente)</p>
      </div>
    </div>
  </div>
</template>

<script>
import { usePublicationsStore } from '../stores/publicationsStore';
import { computed, onMounted, ref } from 'vue';

export default {
  name: 'Moderation',
  setup() {
    const publicationsStore = usePublicationsStore();
    const activeTab = ref('publications');
    const filterStatus = ref('all');
    const pendingReportsCount = ref(0);

    const filteredPublications = computed(() => {
      let pubs = publicationsStore.publications;

      if (filterStatus.value !== 'all') {
        pubs = pubs.filter(pub => pub.state === filterStatus.value);
      }

      return pubs;
    });

    const formatDate = (dateString) => {
      if (!dateString) return '';
      const date = new Date(dateString);
      return date.toLocaleDateString('es-ES', {
        year: 'numeric',
        month: 'short',
        day: 'numeric',
        hour: '2-digit',
        minute: '2-digit'
      });
    };

    const getStatusLabel = (state) => {
      const labels = {
        'pending': 'Pendiente',
        'approved': 'Aprobado',
        'rejected': 'Rechazado'
      };
      return labels[state?.toLowerCase()] || state;
    };

    const handleApprove = async (publicationId) => {
      try {
        await publicationsStore.updatePublicationState(publicationId, 'approved');
        console.log('Publication approved:', publicationId);
      } catch (error) {
        console.error('Error approving publication:', error);
      }
    };

    const handleReject = async (publicationId) => {
      try {
        await publicationsStore.updatePublicationState(publicationId, 'rejected');
        console.log('Publication rejected:', publicationId);
      } catch (error) {
        console.error('Error rejecting publication:', error);
      }
    };

    const handleViewDetails = (publication) => {
      console.log('View details:', publication);
      // TODO: Show modal with publication details
    };

    onMounted(async () => {
      await publicationsStore.fetchPublications();
    });

    return {
      activeTab,
      filterStatus,
      pendingReportsCount,
      filteredPublications,
      formatDate,
      getStatusLabel,
      handleApprove,
      handleReject,
      handleViewDetails
    };
  }
}
</script>

<style scoped>
.moderation-container {
  padding: 24px;
  max-width: 1400px;
  margin: 0 auto;
}

.moderation-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.moderation-title {
  font-size: 28px;
  font-weight: bold;
  color: #1a202c;
}

.filters {
  display: flex;
  gap: 12px;
}

.filter-select {
  padding: 8px 16px;
  border: 1px solid #e2e8f0;
  border-radius: 6px;
  font-size: 14px;
  background: white;
  cursor: pointer;
}

.filter-select:focus {
  outline: none;
  border-color: #667eea;
}

.tabs {
  display: flex;
  gap: 8px;
  margin-bottom: 24px;
  border-bottom: 2px solid #e2e8f0;
}

.tab-button {
  padding: 12px 24px;
  background: none;
  border: none;
  border-bottom: 2px solid transparent;
  color: #718096;
  font-size: 15px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s;
  position: relative;
  margin-bottom: -2px;
}

.tab-button:hover {
  color: #667eea;
}

.tab-button.active {
  color: #667eea;
  border-bottom-color: #667eea;
}

.badge {
  display: inline-block;
  background: #ef4444;
  color: white;
  font-size: 11px;
  padding: 2px 6px;
  border-radius: 10px;
  margin-left: 6px;
}

.tab-content {
  animation: fadeIn 0.3s;
}

@keyframes fadeIn {
  from { opacity: 0; }
  to { opacity: 1; }
}

.publications-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.moderation-card {
  background: white;
  border: 1px solid #e2e8f0;
  border-radius: 12px;
  padding: 24px;
  transition: all 0.2s;
}

.moderation-card:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 12px;
}

.publication-info {
  flex: 1;
}

.publication-title {
  font-size: 18px;
  font-weight: 600;
  color: #1a202c;
  margin: 0 0 4px 0;
}

.publication-meta {
  font-size: 13px;
  color: #718096;
  margin: 0;
}

.status-badge {
  padding: 6px 14px;
  border-radius: 16px;
  font-size: 12px;
  font-weight: 600;
  text-transform: uppercase;
}

.status-badge.pending {
  background: #fef3c7;
  color: #92400e;
}

.status-badge.approved {
  background: #d1fae5;
  color: #065f46;
}

.status-badge.rejected {
  background: #fee2e2;
  color: #991b1b;
}

.publication-description {
  color: #4a5568;
  font-size: 14px;
  line-height: 1.6;
  margin-bottom: 16px;
}

.publication-details {
  display: flex;
  gap: 20px;
  margin-bottom: 12px;
}

.detail-item {
  display: flex;
  align-items: center;
  gap: 6px;
  color: #718096;
  font-size: 14px;
}

.detail-item svg {
  color: #a0aec0;
}

.categories {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-bottom: 20px;
}

.category-tag {
  padding: 4px 12px;
  background: #edf2f7;
  color: #4a5568;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 500;
}

.action-buttons {
  display: flex;
  gap: 12px;
  padding-top: 16px;
  border-top: 1px solid #e2e8f0;
}

.btn {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 10px 20px;
  border: none;
  border-radius: 6px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s;
}

.btn-approve {
  background: #10b981;
  color: white;
}

.btn-approve:hover {
  background: #059669;
}

.btn-reject {
  background: #ef4444;
  color: white;
}

.btn-reject:hover {
  background: #dc2626;
}

.btn-view {
  background: #f7fafc;
  color: #4a5568;
  border: 1px solid #e2e8f0;
}

.btn-view:hover {
  background: #edf2f7;
}

.no-results,
.reports-info,
.users-info {
  text-align: center;
  padding: 60px 20px;
  color: #718096;
  font-size: 16px;
}
</style>