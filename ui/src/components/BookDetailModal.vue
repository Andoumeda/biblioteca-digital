<template>
  <teleport to="body">
    <transition name="modal">
      <div v-if="isOpen" class="modal-overlay" @click="handleClose">
        <div class="modal-container" @click.stop>
          <div class="modal-header">
            <div>
              <h2 class="modal-title">{{ book.title }}</h2>
              <div v-if="book.publicationTitle" class="publication-info">
                <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <path d="M4 19.5v-15A2.5 2.5 0 0 1 6.5 2H20v20H6.5a2.5 2.5 0 0 1 0-5H20"/>
                </svg>
                <span>Parte de la publicación: <strong>{{ book.publicationTitle }}</strong></span>
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
                <button @click="handleShare" class="btn btn-secondary">
                  <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                    <circle cx="18" cy="5" r="3"/>
                    <circle cx="6" cy="12" r="3"/>
                    <circle cx="18" cy="19" r="3"/>
                    <line x1="8.59" x2="15.42" y1="13.51" y2="17.49"/>
                    <line x1="15.41" x2="8.59" y1="6.51" y2="10.49"/>
                  </svg>
                  Compartir
                </button>
              </div>
            </div>

            <div class="book-content">
              <div class="book-metadata">
                <div class="metadata-item">
                  <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                    <path d="M19 21v-2a4 4 0 0-4-4H9a4 4 0 0 0-4 4v2"/>
                    <circle cx="12" cy="7" r="4"/>
                  </svg>
                  <span class="label">{{ authorsLabel }}:</span>
                  <span class="value">{{ formattedAuthors }}</span>
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

                <!-- Mostrar rating existente del usuario -->
                <div v-if="existingRating && !isEditingRating" class="existing-rating-display">
                  <div class="existing-rating-header">
                    <div class="rating-info">
                      <p class="rating-label">Tu calificación:</p>
                      <RatingSystem
                        :current-rating="existingRating.valoration"
                        :readonly="true"
                        size="lg"
                      />
                    </div>
                    <button @click="startEditingRating" class="btn-edit-rating">
                      <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                        <path d="M17 3a2.85 2.83 0 1 1 4 4L7.5 20.5 2 22l1.5-5.5Z"/>
                      </svg>
                      Editar calificación
                    </button>
                  </div>
                  <p v-if="existingRating.comment" class="existing-comment">
                    {{ existingRating.comment }}
                  </p>
                  <p v-else class="no-comment">Sin comentario</p>
                </div>

                <!-- Formulario para crear o editar rating -->
                <div v-if="!existingRating || isEditingRating" class="add-rating">
                  <div class="rating-form-container">
                    <div class="rating-stars-row">
                      <p>{{ isEditingRating ? 'Editar tu calificación:' : 'Califica este libro:' }}</p>
                      <RatingSystem
                        :current-rating="userRating"
                        :readonly="false"
                        size="lg"
                        @rating-change="handleRatingChange"
                      />
                    </div>
                    <div class="rating-comment-row">
                      <label for="ratingComment">Comentario (opcional):</label>
                      <textarea
                        id="ratingComment"
                        v-model="userComment"
                        placeholder="Comparte tu opinión sobre este libro..."
                        rows="3"
                        class="comment-textarea"
                      />
                    </div>
                    <div class="rating-form-actions">
                      <button
                        @click="handleSubmitRating"
                        class="btn btn-submit-rating"
                        :disabled="userRating === 0 || isSubmittingRating"
                      >
                        <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                          <path d="M19 21l-7-5-7 5V5a2 2 0 0 1 2-2h10a2 2 0 0 1 2 2z"/>
                        </svg>
                        {{ isSubmittingRating ? 'Guardando...' : (isEditingRating ? 'Actualizar calificación' : 'Enviar calificación') }}
                      </button>
                      <button
                        v-if="isEditingRating"
                        @click="cancelEditingRating"
                        class="btn btn-cancel-rating"
                        :disabled="isSubmittingRating"
                      >
                        Cancelar
                      </button>
                    </div>
                  </div>
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
import { ref, computed, watch, onMounted } from 'vue';
import RatingSystem from './RatingSystem.vue';
import { ratingsAPI } from '../api/booksService';

// Hardcoded UserProfile ID (por ahora, hasta implementar login)
const HARDCODED_USER_PROFILE_ID = 1;

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
  emits: ['close', 'rating-added'],
  setup(props, { emit }) {
    const userRating = ref(0);
    const userComment = ref('');
    const isDownloading = ref(false);
    const isSubmittingRating = ref(false);
    const existingRating = ref(null);
    const isEditingRating = ref(false);
    const isLoadingRating = ref(false);

    // Computed properties for authors
    const formattedAuthors = computed(() => {
      if (props.book.authors && Array.isArray(props.book.authors) && props.book.authors.length > 0) {
        return props.book.authors.map(a => a.fullName).join(', ');
      }
      return props.book.author || 'Autor desconocido';
    });

    const authorsLabel = computed(() => {
      if (props.book.authors && Array.isArray(props.book.authors) && props.book.authors.length > 1) {
        return 'Autores';
      }
      return 'Autor';
    });

    // Buscar si ya existe un rating del usuario para este libro
    const loadExistingRating = async () => {
      if (!props.book.id) return;

      isLoadingRating.value = true;
      try {
        // Obtener todos los ratings del usuario
        const response = await ratingsAPI.getRatingsByFilters({
          userProfileId: HARDCODED_USER_PROFILE_ID,
          bookId: props.book.id
        }, 0);

        const ratings = response.data?.data || [];

        // Buscar el rating específico para este libro
        const userBookRating = ratings.find(r =>
          r.bookId === props.book.id && r.userProfileId === HARDCODED_USER_PROFILE_ID
        );

        if (userBookRating) {
          existingRating.value = userBookRating;
          console.log('Rating existente encontrado:', userBookRating);
        } else {
          existingRating.value = null;
        }
      } catch (error) {
        console.error('Error loading existing rating:', error);
        existingRating.value = null;
      } finally {
        isLoadingRating.value = false;
      }
    };

    // Cargar rating cuando el modal se abre o el libro cambia
    watch(() => props.isOpen, (newVal) => {
      if (newVal) {
        loadExistingRating();
      }
    });

    watch(() => props.book.id, () => {
      if (props.isOpen) {
        loadExistingRating();
      }
    });

    onMounted(() => {
      if (props.isOpen) {
        loadExistingRating();
      }
    });

    const handleClose = () => {
      // Reset form when closing
      userRating.value = 0;
      userComment.value = '';
      isEditingRating.value = false;
      emit('close');
    };

    const handleDownload = async () => {
      isDownloading.value = true;
      console.log('Descargando libro:', props.book.id, props.book.title);
      setTimeout(() => {
        isDownloading.value = false;
      }, 2000);
    };

    const handleShare = () => {
      console.log('Compartir libro:', props.book.id, props.book.title);
    };

    const handleRatingChange = (rating) => {
      userRating.value = rating;
    };

    const startEditingRating = () => {
      if (existingRating.value) {
        userRating.value = existingRating.value.valoration;
        userComment.value = existingRating.value.comment || '';
        isEditingRating.value = true;
      }
    };

    const cancelEditingRating = () => {
      userRating.value = 0;
      userComment.value = '';
      isEditingRating.value = false;
    };

    const handleSubmitRating = async () => {
      if (userRating.value === 0) {
        alert('Por favor selecciona una calificación antes de enviar');
        return;
      }

      isSubmittingRating.value = true;

      try {
        const ratingData = {
          valoration: userRating.value,
          comment: userComment.value || '',
          bookId: props.book.id,
          userProfileId: HARDCODED_USER_PROFILE_ID
        };

        if (isEditingRating.value && existingRating.value) {
          // Actualizar rating existente
          await ratingsAPI.updateRating(existingRating.value.id, ratingData);
          alert('¡Calificación actualizada exitosamente!');
        } else {
          // Crear nuevo rating
          await ratingsAPI.createRating(ratingData);
          alert('¡Calificación enviada exitosamente!');
        }

        // Recargar el rating existente
        await loadExistingRating();

        // Reset form
        userRating.value = 0;
        userComment.value = '';
        isEditingRating.value = false;

        // Emit event to parent
        emit('rating-added');
      } catch (error) {
        console.error('Error submitting rating:', error);
        alert('Error al enviar la calificación. Por favor intenta de nuevo.');
      } finally {
        isSubmittingRating.value = false;
      }
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
      userRating,
      userComment,
      isDownloading,
      isSubmittingRating,
      existingRating,
      isEditingRating,
      isLoadingRating,
      formattedAuthors,
      authorsLabel,
      handleClose,
      handleDownload,
      handleShare,
      handleRatingChange,
      handleSubmitRating,
      startEditingRating,
      cancelEditingRating,
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

.publication-info {
  display: flex;
  align-items: center;
  gap: 8px;
  color: #718096;
  font-size: 14px;
}

.publication-info strong {
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

.btn-secondary {
  background: white;
  border: 1px solid #e2e8f0;
  color: #4a5568;
}

.btn-secondary:hover {
  background: #f7fafc;
  border-color: #cbd5e0;
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
}

.existing-rating-display {
  padding: 20px;
  background: #edf2f7;
  border-radius: 8px;
  margin-bottom: 16px;
  border-left: 4px solid #667eea;
}

.existing-rating-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.rating-info {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.rating-label {
  margin: 0;
  font-size: 14px;
  color: #4a5568;
  font-weight: 600;
}

.btn-edit-rating {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 16px;
  background: #667eea;
  color: white;
  border: none;
  border-radius: 6px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s;
}

.btn-edit-rating:hover {
  background: #5568d3;
  transform: translateY(-1px);
  box-shadow: 0 2px 8px rgba(102, 126, 234, 0.3);
}

.existing-comment {
  margin: 8px 0 0 0;
  padding: 12px;
  background: white;
  border-radius: 6px;
  color: #2d3748;
  line-height: 1.6;
  font-size: 14px;
}

.no-comment {
  margin: 8px 0 0 0;
  color: #a0aec0;
  font-style: italic;
  font-size: 14px;
}

.rating-form-container {
  display: flex;
  flex-direction: column;
  gap: 16px;
  width: 100%;
}

.rating-stars-row {
  display: flex;
  align-items: center;
  gap: 16px;
}

.rating-stars-row p {
  margin: 0;
  font-size: 15px;
  color: #4a5568;
  font-weight: 500;
}

.rating-comment-row {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.rating-comment-row label {
  font-size: 14px;
  color: #4a5568;
  font-weight: 500;
}

.comment-textarea {
  width: 100%;
  padding: 10px 14px;
  border: 1px solid #e2e8f0;
  border-radius: 6px;
  font-size: 14px;
  font-family: inherit;
  resize: vertical;
  transition: all 0.2s;
}

.comment-textarea:focus {
  outline: none;
  border-color: #667eea;
  box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
}

.rating-form-actions {
  display: flex;
  gap: 12px;
  align-items: center;
}

.btn-submit-rating {
  display: flex;
  align-items: center;
  justify-content: center;
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

.btn-submit-rating:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.3);
}

.btn-submit-rating:disabled {
  background: #cbd5e0;
  cursor: not-allowed;
  transform: none;
}

.btn-cancel-rating {
  padding: 12px 24px;
  background: white;
  color: #4a5568;
  border: 2px solid #e2e8f0;
  border-radius: 8px;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s;
}

.btn-cancel-rating:hover:not(:disabled) {
  border-color: #cbd5e0;
  background: #f7fafc;
}

.btn-cancel-rating:disabled {
  opacity: 0.5;
  cursor: not-allowed;
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