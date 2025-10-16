<template>
  <teleport to="body">
    <transition name="modal">
      <div v-if="isOpen" class="modal-overlay" @click="handleClose">
        <div class="modal-container" @click.stop>
          <div class="modal-header">
            <div>
              <h2 class="modal-title">{{ book.title }}</h2>
              <div v-if="book.collectionTitle" class="collection-info">
                <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <path d="M4 19.5v-15A2.5 2.5 0 0 1 6.5 2H20v20H6.5a2.5 2.5 0 0 1 0-5H20"/>
                </svg>
                <span>Parte de la colección: <strong>{{ book.collectionTitle }}</strong></span>
              </div>
            </div>
            <button @click="handleClose" class="close-btn">
              <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <line x1="18" y1="6" x2="6" y2="18"/>
                <line x1="6" y1="6" x2="18" y2="18"/>
              </svg>
            </button>
          </div>

          <div class="modal-body">
            <div class="book-sidebar">
              <img
                :src="book.cover || '/programming-book-cover.png'"
                :alt="book.title"
                class="book-cover"
                @error="handleImageError"
              />

              <div class="rating-container">
                <div class="rating-display">
                  <RatingSystem :current-rating="Math.round(book.rating || 0)" :readonly="true" size="lg" />
                  <span class="rating-number">{{ (book.rating || 0).toFixed(1) }}</span>
                  <span class="reviews-count">({{ book.reviews || 0 }} reseñas)</span>
                </div>
              </div>

              <div class="action-buttons">
                <button @click="handleDownload" class="btn btn-primary" :disabled="isDownloading">
                  <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                    <path d="M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4"/>
                    <polyline points="7 10 12 15 17 10"/>
                    <line x1="12" x2="12" y1="15" y2="3"/>
                  </svg>
                  {{ isDownloading ? 'Descargando...' : 'Descargar' }}
                </button>
                <div class="secondary-actions">
                  <button @click="handleFavorite" :class="['btn', 'btn-icon', { 'active': isFavorite }]">
                    <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                      <path d="M19 21l-7-5-7 5V5a2 2 0 0 1 2-2h10a2 2 0 0 1 2 2z"/>
                    </svg>
                    {{ favoriteCount }}
                  </button>
                  <button @click="handleShare" class="btn btn-icon">
                    <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                      <circle cx="18" cy="5" r="3"/>
                      <circle cx="6" cy="12" r="3"/>
                      <circle cx="18" cy="19" r="3"/>
                      <line x1="8.59" x2="15.42" y1="13.51" y2="17.49"/>
                      <line x1="15.41" x2="8.59" y1="6.51" y2="10.49"/>
                    </svg>
                  </button>
                </div>
              </div>
            </div>

            <div class="book-content">
              <div class="book-metadata">
                <div class="metadata-item">
                  <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                    <path d="M19 21v-2a4 4 0 0 0-4-4H9a4 4 0 0 0-4 4v2"/>
                    <circle cx="12" cy="7" r="4"/>
                  </svg>
                  <span class="label">Autor:</span>
                  <span class="value">{{ book.author }}</span>
                </div>

                <div v-if="book.uploadDate" class="metadata-item">
                  <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                    <rect width="18" height="18" x="3" y="4" rx="2" ry="2"/>
                    <line x1="16" x2="16" y1="2" y2="6"/>
                    <line x1="8" x2="8" y1="2" y2="6"/>
                    <line x1="3" x2="21" y1="10" y2="10"/>
                  </svg>
                  <span class="label">Subido:</span>
                  <span class="value">{{ formatDate(book.uploadDate) }}</span>
                </div>

                <div class="metadata-item">
                  <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                    <path d="M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4"/>
                    <polyline points="7 10 12 15 17 10"/>
                    <line x1="12" x2="12" y1="15" y2="3"/>
                  </svg>
                  <span class="label">Descargas:</span>
                  <span class="value">{{ book.downloadCount || 0 }}</span>
                </div>
              </div>

              <div v-if="book.uploader || book.uploaderName" class="uploader-info">
                <span class="label">Subido por:</span>
                <div class="uploader">
                  <div class="avatar">{{ (book.uploaderName || book.uploader || 'U')[0].toUpperCase() }}</div>
                  <span class="name">{{ book.uploaderName || `@${book.uploader}` }}</span>
                </div>
              </div>

              <div class="description-section">
                <h3>Descripción</h3>
                <p>{{ book.description || 'Sin descripción disponible.' }}</p>
              </div>

              <div class="tags-section">
                <h3>Etiquetas</h3>
                <div class="tags">
                  <span v-for="tag in (book.tags || ['libro', 'lectura'])" :key="tag" class="tag">
                    {{ tag }}
                  </span>
                </div>
              </div>

              <div class="reviews-section">
                <h3>Calificaciones y Reseñas</h3>
                <div class="add-rating">
                  <p>Califica este libro:</p>
                  <RatingSystem
                    :current-rating="userRating"
                    :readonly="false"
                    size="lg"
                    @rating-change="handleAddRating"
                  />
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </transition>
  </teleport>
</template>

<script>
import { ref } from 'vue';
import RatingSystem from './RatingSystem.vue';

export default {
  name: 'BookDetailModal',
  components: {
    RatingSystem
  },
  props: {
    book: {
      type: Object,
      required: true
    },
    isOpen: {
      type: Boolean,
      default: false
    }
  },
  emits: ['close'],
  setup(props, { emit }) {
    const isFavorite = ref(props.book.isFavorite || false);
    const favoriteCount = ref(props.book.favorites || props.book.likes || 0);
    const userRating = ref(props.book.userRating || 0);
    const isDownloading = ref(false);

    const handleClose = () => {
      emit('close');
    };

    const handleDownload = async () => {
      isDownloading.value = true;
      console.log('Descargando libro:', props.book.id, props.book.title);
      setTimeout(() => {
        isDownloading.value = false;
      }, 2000);
    };

    const handleFavorite = () => {
      isFavorite.value = !isFavorite.value;
      favoriteCount.value += isFavorite.value ? 1 : -1;
      console.log('Favorito:', props.book.id, isFavorite.value);
    };

    const handleShare = () => {
      console.log('Compartir libro:', props.book.id, props.book.title);
    };

    const handleAddRating = (rating) => {
      userRating.value = rating;
      console.log('Nueva calificación:', { rating, bookId: props.book.id });
    };

    const handleImageError = (event) => {
      event.target.src = '/programming-book-cover.png';
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

    return {
      isFavorite,
      favoriteCount,
      userRating,
      isDownloading,
      handleClose,
      handleDownload,
      handleFavorite,
      handleShare,
      handleAddRating,
      handleImageError,
      formatDate
    };
  }
};
</script>

<style scoped>
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
  padding: 20px;
}

.modal-container {
  background: white;
  border-radius: 16px;
  width: 100%;
  max-width: 1200px;
  max-height: 90vh;
  overflow-y: auto;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  padding: 32px 32px 24px;
  border-bottom: 1px solid #e2e8f0;
}

.modal-title {
  font-size: 28px;
  font-weight: bold;
  color: #1a202c;
  margin: 0 0 8px 0;
}

.collection-info {
  display: flex;
  align-items: center;
  gap: 8px;
  color: #718096;
  font-size: 14px;
}

.collection-info strong {
  color: #667eea;
}

.close-btn {
  padding: 8px;
  background: transparent;
  border: none;
  color: #718096;
  cursor: pointer;
  border-radius: 8px;
  transition: all 0.2s;
}

.close-btn:hover {
  background: #f7fafc;
  color: #1a202c;
}

.modal-body {
  display: grid;
  grid-template-columns: 320px 1fr;
  gap: 32px;
  padding: 32px;
}

.book-sidebar {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.book-cover {
  width: 100%;
  aspect-ratio: 2/3;
  object-fit: cover;
  border-radius: 12px;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.15);
}

.rating-container {
  text-align: center;
}

.rating-display {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
}

.rating-number {
  font-size: 24px;
  font-weight: bold;
  color: #1a202c;
}

.reviews-count {
  font-size: 13px;
  color: #718096;
}

.action-buttons {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.btn {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  padding: 12px 24px;
  border: none;
  border-radius: 8px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s;
}

.btn-primary {
  background: #667eea;
  color: white;
}

.btn-primary:hover:not(:disabled) {
  background: #5568d3;
  transform: translateY(-2px);
}

.btn-primary:disabled {
  background: #cbd5e0;
  cursor: not-allowed;
}

.secondary-actions {
  display: flex;
  gap: 8px;
}

.btn-icon {
  flex: 1;
  background: white;
  border: 1px solid #e2e8f0;
  color: #4a5568;
}

.btn-icon:hover {
  background: #f7fafc;
  border-color: #cbd5e0;
}

.btn-icon.active {
  background: #667eea;
  color: white;
  border-color: #667eea;
}

.book-content {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.book-metadata {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 16px;
}

.metadata-item {
  display: flex;
  align-items: center;
  gap: 10px;
  color: #4a5568;
  font-size: 14px;
}

.metadata-item svg {
  color: #a0aec0;
  flex-shrink: 0;
}

.metadata-item .label {
  color: #718096;
}

.metadata-item .value {
  font-weight: 500;
  color: #1a202c;
}

.uploader-info {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 16px;
  background: #f7fafc;
  border-radius: 8px;
}

.uploader-info .label {
  color: #718096;
  font-size: 14px;
}

.uploader {
  display: flex;
  align-items: center;
  gap: 10px;
}

.avatar {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 600;
  font-size: 14px;
}

.uploader .name {
  font-weight: 500;
  color: #667eea;
}

.description-section h3,
.tags-section h3,
.reviews-section h3 {
  font-size: 20px;
  font-weight: 600;
  color: #1a202c;
  margin: 0 0 16px 0;
}

.description-section p {
  color: #4a5568;
  line-height: 1.7;
  font-size: 15px;
  margin: 0;
}

.tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.tag {
  padding: 6px 14px;
  background: #edf2f7;
  color: #4a5568;
  border-radius: 16px;
  font-size: 13px;
  font-weight: 500;
}

.add-rating {
  padding: 20px;
  background: #f7fafc;
  border-radius: 8px;
  display: flex;
  align-items: center;
  gap: 16px;
}

.add-rating p {
  margin: 0;
  font-size: 15px;
  color: #4a5568;
}

@media (max-width: 768px) {
  .modal-body {
    grid-template-columns: 1fr;
  }

  .book-metadata {
    grid-template-columns: 1fr;
  }
}
</style>