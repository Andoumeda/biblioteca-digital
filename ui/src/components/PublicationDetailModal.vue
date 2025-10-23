<template>
  <teleport to="body">
    <transition name="modal">
      <div v-if="isOpen" class="modal-overlay" @click="handleClose">
        <div class="modal-container" @click.stop>
          <div class="modal-header">
            <div>
              <h2 class="modal-title">{{ publication.title }}</h2>
              <div class="publication-meta">
                <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <path d="M4 19.5v-15A2.5 2.5 0 0 1 6.5 2H20v20H6.5a2.5 2.5 0 0 1 0-5H20"/>
                </svg>
                <span>Publicación con {{ publication.books?.length || 0 }} libro{{ publication.books?.length !== 1 ? 's' : '' }}</span>
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
            <!-- Sidebar -->
            <div class="publication-sidebar">
              <img
                :src="publication.coverImage || '/programming-book-cover.png'"
                :alt="publication.title"
                class="publication-cover"
                @error="handleImageError"
              />

              <!-- Rating & Stats -->
              <div class="stats-container">
                <div class="rating-display">
                  <div class="star-rating">
                    <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="currentColor" stroke="currentColor" stroke-width="2" class="star-icon">
                      <polygon points="12 2 15.09 8.26 22 9.27 17 14.14 18.18 21.02 12 17.77 5.82 21.02 7 14.14 2 9.27 8.91 8.26 12 2"/>
                    </svg>
                    <span class="rating-number">{{ averageRating.toFixed(1) }}</span>
                  </div>
                  <span class="reviews-count">({{ totalRatings }} calificaciones)</span>
                </div>

                <div class="publication-stats">
                  <div class="stat-item">
                    <svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                      <path d="M19 21l-7-5-7 5V5a2 2 0 0 1 2-2h10a2 2 0 0 1 2 2z"/>
                    </svg>
                    <span>{{ favoriteCount }} favoritos</span>
                  </div>
                  <div class="stat-item">
                    <svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                      <path d="M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4"/>
                      <polyline points="7 10 12 15 17 10"/>
                      <line x1="12" x2="12" y1="15" y2="3"/>
                    </svg>
                    <span>{{ totalDownloads }} descargas</span>
                  </div>
                </div>
              </div>

              <!-- Action Buttons -->
              <div class="action-buttons">
                <button @click="handleDownloadAll" class="btn btn-primary" :disabled="isDownloading">
                  <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                    <path d="M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4"/>
                    <polyline points="7 10 12 15 17 10"/>
                    <line x1="12" x2="12" y1="15" y2="3"/>
                  </svg>
                  {{ isDownloading ? 'Descargando...' : 'Descargar Todo' }}
                </button>
                <div class="secondary-actions">
                  <button @click="handleFavorite" :class="['btn', 'btn-icon', { 'active': isFavorite }]">
                    <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" :fill="isFavorite ? 'currentColor' : 'none'" stroke="currentColor" stroke-width="2">
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

            <!-- Content -->
            <div class="publication-content">
              <!-- Metadata Grid -->
              <div class="metadata-grid">
                <div class="metadata-item">
                  <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                    <path d="M19 21v-2a4 4 0 0 0-4-4H9a4 4 0 0 0-4 4v2"/>
                    <circle cx="12" cy="7" r="4"/>
                  </svg>
                  <span class="label">Creador:</span>
                  <span class="value">{{ publication.userProfile?.displayName || 'Desconocido' }}</span>
                </div>

                <div class="metadata-item">
                  <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                    <path d="M20.59 13.41l-7.17 7.17a2 2 0 0 1-2.83 0L2 12V2h10l8.59 8.59a2 2 0 0 1 0 2.82z"/>
                    <line x1="7" x2="7.01" y1="7" y2="7"/>
                  </svg>
                  <span class="label">Categoría:</span>
                  <span class="category-badge">{{ publication.categories?.[0]?.name || 'General' }}</span>
                </div>

                <div v-if="publication.createdAt" class="metadata-item">
                  <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                    <rect width="18" height="18" x="3" y="4" rx="2" ry="2"/>
                    <line x1="16" x2="16" y1="2" y2="6"/>
                    <line x1="8" x2="8" y1="2" y2="6"/>
                    <line x1="3" x2="21" y1="10" y2="10"/>
                  </svg>
                  <span class="label">Subida:</span>
                  <span class="value">{{ formatDate(publication.createdAt) }}</span>
                </div>

                <div class="metadata-item">
                  <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                    <path d="M4 19.5v-15A2.5 2.5 0 0 1 6.5 2H20v20H6.5a2.5 2.5 0 0 1 0-5H20"/>
                  </svg>
                  <span class="label">Libros:</span>
                  <span class="value">{{ publication.books?.length || 0 }}</span>
                </div>
              </div>

              <!-- Uploader Info -->
              <div v-if="publication.userProfile" class="uploader-info">
                <span class="label">Subido por:</span>
                <div class="uploader">
                  <div class="avatar">{{ (publication.userProfile.displayName || publication.userProfile.user?.username || 'U')[0].toUpperCase() }}</div>
                  <span class="name">@{{ publication.userProfile.user?.username || 'usuario' }}</span>
                </div>
              </div>

              <!-- Description -->
              <div class="description-section">
                <h3>Descripción</h3>
                <p>{{ publication.description || 'Sin descripción disponible.' }}</p>
              </div>

              <!-- Categories -->
              <div v-if="publication.categories && publication.categories.length > 0" class="tags-section">
                <h3>Categorías</h3>
                <div class="tags">
                  <span v-for="category in publication.categories" :key="category.id" class="tag">
                    {{ category.name }}
                  </span>
                </div>
              </div>

              <!-- Books List -->
              <div class="books-section">
                <h3>Libros en esta Publicación</h3>
                <div class="books-grid">
                  <div
                    v-for="book in publication.books"
                    :key="book.id"
                    class="book-card"
                    @click="handleBookClick(book)"
                  >
                    <div class="book-card-content">
                      <img
                        :src="book.coverImg || '/programming-book-cover.png'"
                        :alt="book.title"
                        class="book-thumbnail"
                        @error="handleImageError"
                      />
                      <div class="book-info">
                        <h5 class="book-title">{{ book.title }}</h5>
                        <p class="book-authors">{{ formatAuthors(book.authors) }}</p>

                        <!-- Book Rating -->
                        <div v-if="bookRatings[book.id]" class="book-rating">
                          <RatingSystem
                            :current-rating="Math.round(bookRatings[book.id].average || 0)"
                            :readonly="true"
                            size="sm"
                          />
                          <span class="rating-text">{{ (bookRatings[book.id].average || 0).toFixed(1) }} ({{ bookRatings[book.id].count || 0 }})</span>
                        </div>

                        <!-- Book Actions -->
                        <div class="book-actions">
                          <button @click.stop="handleDownloadBook(book)" class="book-btn" :disabled="downloadingBooks[book.id]">
                            <svg xmlns="http://www.w3.org/2000/svg" width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                              <path d="M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4"/>
                              <polyline points="7 10 12 15 17 10"/>
                              <line x1="12" x2="12" y1="15" y2="3"/>
                            </svg>
                            {{ downloadingBooks[book.id] ? 'Descargando...' : 'Descargar' }}
                          </button>
                          <button @click.stop="handleRateBook(book)" class="book-btn book-btn-secondary">
                            <svg xmlns="http://www.w3.org/2000/svg" width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                              <polygon points="12 2 15.09 8.26 22 9.27 17 14.14 18.18 21.02 12 17.77 5.82 21.02 7 14.14 2 9.27 8.91 8.26 12 2"/>
                            </svg>
                            Calificar
                          </button>
                          <button @click.stop="handleShareBook(book)" class="book-btn-icon">
                            <svg xmlns="http://www.w3.org/2000/svg" width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
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
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </transition>

    <!-- Book Detail Modal -->
    <BookDetailModal
      v-if="selectedBook"
      :book="selectedBook"
      :is-open="showBookModal"
      @close="closeBookModal"
    />
  </teleport>
</template>

<script>
import { ref, computed, watch } from 'vue';
import { useBooksStore } from '../stores/books';
import { useFavoritesStore } from '../stores/favorites';
import RatingSystem from './RatingSystem.vue';
import BookDetailModal from './BookDetailModal.vue';

export default {
  name: 'PublicationDetailModal',
  components: {
    RatingSystem,
    BookDetailModal
  },
  props: {
    publication: {
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
    const booksStore = useBooksStore();
    const favoritesStore = useFavoritesStore();

    const isFavorite = ref(false);
    const favoriteCount = ref(props.publication.favoritesCount || 0);
    const isDownloading = ref(false);
    const downloadingBooks = ref({});
    const bookRatings = ref({});
    const showBookModal = ref(false);
    const selectedBook = ref(null);

    // Computed properties
    const averageRating = computed(() => {
      const books = props.publication.books || [];
      if (books.length === 0) return 0;

      const total = books.reduce((sum, book) => {
        const rating = bookRatings.value[book.id];
        return sum + (rating?.average || 0);
      }, 0);

      return total / books.length;
    });

    const totalRatings = computed(() => {
      const books = props.publication.books || [];
      return books.reduce((sum, book) => {
        const rating = bookRatings.value[book.id];
        return sum + (rating?.count || 0);
      }, 0);
    });

    const totalDownloads = computed(() => {
      const books = props.publication.books || [];
      return books.reduce((sum, book) => sum + (book.downloadCount || 0), 0);
    });

    // Methods
    const handleClose = () => {
      emit('close');
    };

    const handleDownloadAll = async () => {
      isDownloading.value = true;
      console.log('Descargando todos los libros de la publicación:', props.publication.id);
      setTimeout(() => {
        isDownloading.value = false;
      }, 2000);
    };

    const handleFavorite = async () => {
      try {
        if (isFavorite.value) {
          await favoritesStore.removeFavorite(props.publication.id, 'publication');
          favoriteCount.value--;
        } else {
          await favoritesStore.addFavorite(props.publication.id, 'publication');
          favoriteCount.value++;
        }
        isFavorite.value = !isFavorite.value;
      } catch (error) {
        console.error('Error al marcar como favorito:', error);
      }
    };

    const handleShare = () => {
      console.log('Compartir publicación:', props.publication.id);
      // TODO: Implementar funcionalidad de compartir
    };

    const handleDownloadBook = async (book) => {
      downloadingBooks.value[book.id] = true;
      console.log('Descargando libro:', book.id, book.title);
      setTimeout(() => {
        downloadingBooks.value[book.id] = false;
      }, 2000);
    };

    const handleRateBook = (book) => {
      selectedBook.value = {
        ...book,
        publicationTitle: props.publication.title
      };
      showBookModal.value = true;
    };

    const handleShareBook = (book) => {
      console.log('Compartir libro:', book.id, book.title);
      // TODO: Implementar funcionalidad de compartir libro
    };

    const handleBookClick = (book) => {
      selectedBook.value = {
        ...book,
        publicationTitle: props.publication.title
      };
      showBookModal.value = true;
    };

    const closeBookModal = () => {
      showBookModal.value = false;
      selectedBook.value = null;
    };

    const formatAuthors = (authors) => {
      if (!authors || authors.length === 0) return 'Autor desconocido';
      return authors.map(a => a.fullName).join(', ');
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

    const handleImageError = (event) => {
      event.target.src = '/programming-book-cover.png';
    };

    const loadBookRatings = async () => {
      const books = props.publication.books || [];
      const bookIds = books.map(book => book.id);

      if (bookIds.length > 0) {
        await booksStore.fetchMultipleBookRatings(bookIds);

        // Update local bookRatings ref
        bookIds.forEach(bookId => {
          const rating = booksStore.getBookRating(bookId);
          bookRatings.value[bookId] = rating;
        });
      }
    };

    const checkIfFavorite = async () => {
      isFavorite.value = await favoritesStore.isFavorite(props.publication.id, 'publication');
    };

    // Watch for publication changes
    watch(() => props.publication, async (newVal) => {
      if (newVal && newVal.id) {
        await loadBookRatings();
        await checkIfFavorite();
        favoriteCount.value = newVal.favoritesCount || 0;
      }
    }, { immediate: true });

    return {
      isFavorite,
      favoriteCount,
      isDownloading,
      downloadingBooks,
      bookRatings,
      averageRating,
      totalRatings,
      totalDownloads,
      showBookModal,
      selectedBook,
      handleClose,
      handleDownloadAll,
      handleFavorite,
      handleShare,
      handleDownloadBook,
      handleRateBook,
      handleShareBook,
      handleBookClick,
      closeBookModal,
      formatAuthors,
      formatDate,
      handleImageError
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
  max-width: 1400px;
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
  position: sticky;
  top: 0;
  background: white;
  z-index: 10;
}

.modal-title {
  font-size: 28px;
  font-weight: bold;
  color: #1a202c;
  margin: 0 0 8px 0;
}

.publication-meta {
  display: flex;
  align-items: center;
  gap: 8px;
  color: #718096;
  font-size: 14px;
}

.close-btn {
  padding: 8px;
  background: transparent;
  border: none;
  color: #718096;
  cursor: pointer;
  border-radius: 8px;
  transition: all 0.2s;
  flex-shrink: 0;
}

.close-btn:hover {
  background: #f7fafc;
  color: #1a202c;
}

.modal-body {
  display: grid;
  grid-template-columns: 340px 1fr;
  gap: 32px;
  padding: 32px;
}

.publication-sidebar {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.publication-cover {
  width: 100%;
  aspect-ratio: 2/3;
  object-fit: cover;
  border-radius: 12px;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.15);
}

.stats-container {
  display: flex;
  flex-direction: column;
  gap: 16px;
  text-align: center;
}

.rating-display {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
}

.star-rating {
  display: flex;
  align-items: center;
  gap: 8px;
}

.star-icon {
  color: #f59e0b;
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

.publication-stats {
  display: flex;
  flex-direction: column;
  gap: 8px;
  padding: 16px;
  background: #f7fafc;
  border-radius: 8px;
}

.stat-item {
  display: flex;
  align-items: center;
  gap: 8px;
  color: #4a5568;
  font-size: 14px;
}

.stat-item svg {
  color: #a0aec0;
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

.publication-content {
  display: flex;
  flex-direction: column;
  gap: 28px;
}

.metadata-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(220px, 1fr));
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

.category-badge {
  padding: 4px 12px;
  background: #edf2f7;
  color: #4a5568;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 500;
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
.books-section h3 {
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

.books-section {
  margin-top: 8px;
}

.books-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 16px;
}

.book-card {
  background: white;
  border: 1px solid #e2e8f0;
  border-radius: 12px;
  padding: 16px;
  cursor: pointer;
  transition: all 0.2s;
}

.book-card:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  transform: translateY(-2px);
}

.book-card-content {
  display: flex;
  gap: 12px;
}

.book-thumbnail {
  width: 60px;
  height: 80px;
  object-fit: cover;
  border-radius: 6px;
  flex-shrink: 0;
}

.book-info {
  flex: 1;
  min-width: 0;
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.book-title {
  font-size: 14px;
  font-weight: 600;
  color: #1a202c;
  margin: 0;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.book-authors {
  font-size: 12px;
  color: #718096;
  margin: 0;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.book-rating {
  display: flex;
  align-items: center;
  gap: 8px;
}

.rating-text {
  font-size: 12px;
  color: #4a5568;
}

.book-actions {
  display: flex;
  align-items: center;
  gap: 6px;
  margin-top: 4px;
}

.book-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 4px;
  padding: 6px 10px;
  border: 1px solid #e2e8f0;
  background: white;
  color: #4a5568;
  border-radius: 6px;
  font-size: 11px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s;
}

.book-btn:hover:not(:disabled) {
  background: #f7fafc;
  border-color: #cbd5e0;
}

.book-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.book-btn-secondary {
  border-color: #667eea;
  color: #667eea;
}

.book-btn-secondary:hover:not(:disabled) {
  background: #667eea;
  color: white;
}

.book-btn-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 6px;
  border: 1px solid #e2e8f0;
  background: white;
  color: #4a5568;
  border-radius: 6px;
  cursor: pointer;
  transition: all 0.2s;
}

.book-btn-icon:hover {
  background: #f7fafc;
  border-color: #cbd5e0;
}

@media (max-width: 1024px) {
  .modal-body {
    grid-template-columns: 1fr;
  }

  .publication-sidebar {
    max-width: 400px;
    margin: 0 auto;
  }
}

@media (max-width: 768px) {
  .metadata-grid {
    grid-template-columns: 1fr;
  }

  .books-grid {
    grid-template-columns: 1fr;
  }
}

/* Modal transitions */
.modal-enter-active,
.modal-leave-active {
  transition: opacity 0.3s ease;
}

.modal-enter-from,
.modal-leave-to {
  opacity: 0;
}
</style>