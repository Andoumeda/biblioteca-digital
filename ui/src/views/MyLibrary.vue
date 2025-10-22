<template>
  <div class="my-library-container">
    <div class="library-header">
      <h2 class="library-title">Mi Biblioteca</h2>
      <button @click="showUploadModal = true" class="upload-btn">
        <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
          <path d="M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4"/>
          <polyline points="17 8 12 3 7 8"/>
          <line x1="12" x2="12" y1="3" y2="15"/>
        </svg>
        Subir Publicación
      </button>
    </div>

    <div class="tabs-container">
      <div class="tabs-list">
        <button @click="activeTab = 'publications'" :class="['tab', { active: activeTab === 'publications' }]">
          <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M4 19.5v-15A2.5 2.5 0 0 1 6.5 2H20v20H6.5a2.5 2.5 0 0 1 0-5H20"/>
          </svg>
          Mis Publicaciones ({{ myPublications.length }})
        </button>
        <button @click="activeTab = 'bookmarks'" :class="['tab', { active: activeTab === 'bookmarks' }]">
          <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M19 21l-7-5-7 5V5a2 2 0 0 1 2-2h10a2 2 0 0 1 2 2z"/>
          </svg>
          Guardados ({{ myBookmarks.length }})
        </button>
        <button @click="activeTab = 'comments'" :class="['tab', { active: activeTab === 'comments' }]">
          <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z"/>
          </svg>
          Mis Comentarios ({{ myComments.length }})
        </button>
      </div>

      <!-- Pestaña de Publicaciones -->
      <div v-show="activeTab === 'publications'" class="tab-content">
        <div v-if="myPublications.length > 0" class="publications-list">
          <div v-for="publication in myPublications" :key="publication.id" class="publication-card">
            <div class="publication-content">
              <img
                :src="publication.cover || '/programming-book-cover.jpg'"
                :alt="publication.title"
                class="publication-cover"
                @error="handleImageError"
              />
              <div class="publication-info">
                <div class="publication-header">
                  <div>
                    <h3 class="publication-title">{{ publication.title }}</h3>
                    <p class="publication-description">{{ publication.description }}</p>
                  </div>
                  <span :class="['status-badge', publication.status === 'approved' ? 'approved' : 'pending']">
                    {{ publication.status === 'approved' ? 'Aprobada' : 'Pendiente' }}
                  </span>
                </div>

                <div class="publication-stats">
                  <div class="stat">
                    <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                      <path d="M4 19.5v-15A2.5 2.5 0 0 1 6.5 2H20v20H6.5a2.5 2.5 0 0 1 0-5H20"/>
                    </svg>
                    {{ publication.bookCount }} libros
                  </div>
                  <div class="stat">
                    <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                      <path d="M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4"/>
                      <polyline points="7 10 12 15 17 10"/>
                      <line x1="12" x2="12" y1="15" y2="3"/>
                    </svg>
                    {{ publication.totalDownloads }} descargas
                  </div>
                  <div class="stat">
                    <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                      <polygon points="12 2 15.09 8.26 22 9.27 17 14.14 18.18 21.02 12 17.77 5.82 21.02 7 14.14 2 9.27 8.91 8.26 12 2"/>
                    </svg>
                    {{ publication.averageRating }} ({{ publication.totalRatings }})
                  </div>
                  <div class="stat">
                    <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                      <path d="M8 2v4"/>
                      <path d="M16 2v4"/>
                      <rect width="18" height="18" x="3" y="4" rx="2"/>
                      <path d="M3 10h18"/>
                    </svg>
                    {{ formatDate(publication.uploadDate) }}
                  </div>
                </div>

                <div class="publication-actions">
                  <button @click="handleViewDetails(publication)" class="action-btn outline">
                    <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                      <path d="M2 12s3-7 10-7 10 7 10 7-3 7-10 7-10-7-10-7Z"/>
                      <circle cx="12" cy="12" r="3"/>
                    </svg>
                    Ver Detalles
                  </button>
                  <button @click="handleEdit(publication)" class="action-btn outline">
                    <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                      <path d="M17 3a2.85 2.83 0 1 1 4 4L7.5 20.5 2 22l1.5-5.5Z"/>
                    </svg>
                    Editar
                  </button>
                  <button @click="handleDelete(publication)" class="action-btn outline danger">
                    <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                      <path d="M3 6h18"/>
                      <path d="M19 6v14c0 1-1 2-2 2H7c-1 0-2-1-2-2V6"/>
                      <path d="M8 6V4c0-1 1-2 2-2h4c1 0 2 1 2 2v2"/>
                    </svg>
                    Eliminar
                  </button>
                </div>
              </div>
            </div>
          </div>
        </div>

        <div v-else class="empty-state">
          <svg xmlns="http://www.w3.org/2000/svg" width="64" height="64" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M4 19.5v-15A2.5 2.5 0 0 1 6.5 2H20v20H6.5a2.5 2.5 0 0 1 0-5H20"/>
          </svg>
          <h3>No tienes publicaciones aún</h3>
          <p>Comienza subiendo tu primera publicación de libros</p>
          <button @click="showUploadModal = true" class="upload-btn">
            <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4"/>
              <polyline points="17 8 12 3 7 8"/>
              <line x1="12" x2="12" y1="3" y2="15"/>
            </svg>
            Subir Publicación
          </button>
        </div>
      </div>

      <!-- Pestaña de Guardados -->
      <div v-show="activeTab === 'bookmarks'" class="tab-content">
        <div v-if="myBookmarks.length > 0" class="bookmarks-grid">
          <div v-for="bookmark in myBookmarks" :key="bookmark.id" class="bookmark-card" @click="handleBookmarkClick(bookmark)">
            <div class="bookmark-cover-container">
              <img
                :src="bookmark.cover || '/programming-book-cover.jpg'"
                :alt="bookmark.title"
                class="bookmark-cover"
                @error="handleImageError"
              />
              <span class="book-count-badge">{{ bookmark.bookCount }} libros</span>
            </div>
            <div class="bookmark-info">
              <h3 class="bookmark-title">{{ bookmark.title }}</h3>
              <p class="bookmark-author">por {{ bookmark.author }}</p>
              <div class="bookmark-footer">
                <div class="rating">
                  <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="currentColor" stroke="currentColor" stroke-width="2">
                    <polygon points="12 2 15.09 8.26 22 9.27 17 14.14 18.18 21.02 12 17.77 5.82 21.02 7 14.14 2 9.27 8.91 8.26 12 2"/>
                  </svg>
                  <span>{{ bookmark.rating }}</span>
                </div>
                <span class="saved-date">Guardado {{ formatDate(bookmark.savedDate) }}</span>
              </div>
            </div>
          </div>
        </div>

        <div v-else class="empty-state">
          <svg xmlns="http://www.w3.org/2000/svg" width="64" height="64" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M19 21l-7-5-7 5V5a2 2 0 0 1 2-2h10a2 2 0 0 1 2 2z"/>
          </svg>
          <h3>No tienes publicaciones guardadas</h3>
          <p>Explora y guarda tus publicaciones favoritas</p>
        </div>
      </div>

      <!-- Pestaña de Comentarios -->
      <div v-show="activeTab === 'comments'" class="tab-content">
        <div v-if="myComments.length > 0" class="comments-list">
          <div v-for="comment in myComments" :key="comment.id" class="comment-card">
            <div class="comment-header">
              <div>
                <h4 class="comment-book-title">{{ comment.bookTitle }}</h4>
                <div class="comment-meta">
                  <div class="stars">
                    <svg v-for="star in 5" :key="star" xmlns="http://www.w3.org/2000/svg" width="12" height="12" viewBox="0 0 24 24"
                      :fill="star <= comment.rating ? 'currentColor' : 'none'"
                      stroke="currentColor"
                      stroke-width="2"
                      :class="star <= comment.rating ? 'star-filled' : 'star-empty'">
                      <polygon points="12 2 15.09 8.26 22 9.27 17 14.14 18.18 21.02 12 17.77 5.82 21.02 7 14.14 2 9.27 8.91 8.26 12 2"/>
                    </svg>
                  </div>
                  <span class="comment-date">{{ formatDate(comment.date) }}</span>
                </div>
              </div>
              <div class="comment-actions">
                <button @click="handleEditComment(comment)" class="icon-btn">
                  <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                    <path d="M17 3a2.85 2.83 0 1 1 4 4L7.5 20.5 2 22l1.5-5.5Z"/>
                  </svg>
                </button>
                <button @click="handleDeleteComment(comment)" class="icon-btn danger">
                  <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                    <path d="M3 6h18"/>
                    <path d="M19 6v14c0 1-1 2-2 2H7c-1 0-2-1-2-2V6"/>
                  </svg>
                </button>
              </div>
            </div>
            <p class="comment-text">{{ comment.comment }}</p>
            <div class="comment-likes">
              <svg xmlns="http://www.w3.org/2000/svg" width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <path d="M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z"/>
              </svg>
              {{ comment.likes }} personas encontraron esto útil
            </div>
          </div>
        </div>

        <div v-else class="empty-state">
          <svg xmlns="http://www.w3.org/2000/svg" width="64" height="64" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z"/>
          </svg>
          <h3>No has dejado comentarios aún</h3>
          <p>Comparte tu opinión sobre los libros que has leído</p>
        </div>
      </div>
    </div>

    <!-- Modal de Subir Publicación -->
    <PublicationUploadModal
      v-if="showUploadModal"
      @close="showUploadModal = false"
      @success="handleUploadSuccess"
    />
  </div>
</template>

<script>
import { ref } from 'vue';
import PublicationUploadModal from '../components/PublicationUploadModal.vue';

export default {
  name: 'MyLibrary',
  components: {
    PublicationUploadModal
  },
  setup() {
    const activeTab = ref('publications');
    const showUploadModal = ref(false);

    const myPublications = ref([
      {
        id: 1,
        title: "Guías de Programación Completas",
        description: "Una publicación completa de guías para aprender programación",
        bookCount: 3,
        totalDownloads: 1247,
        totalRatings: 124,
        averageRating: 4.8,
        status: "approved",
        uploadDate: "2025-10-05",
        cover: "/programming-book-cover.jpg",
      },
    ]);

    const myBookmarks = ref([
      {
        id: 1,
        title: "Cuentos de Misterio Clásicos",
        author: "Carlos Escritor",
        cover: "/mystery-book-cover.png",
        rating: 4.6,
        bookCount: 2,
        savedDate: "2025-10-08",
      },
      {
        id: 2,
        title: "Recetarios Veganos",
        author: "María Chef",
        cover: "/vegan-cooking-book-cover.png",
        rating: 4.9,
        bookCount: 3,
        savedDate: "2025-10-02",
      },
    ]);

    const myComments = ref([
      {
        id: 1,
        bookTitle: "Fundamentos de Programación",
        comment: "Excelente libro, muy bien explicado y con ejemplos prácticos.",
        rating: 5,
        date: "2025-10-12",
        likes: 12,
      },
      {
        id: 2,
        bookTitle: "El Misterio de la Casa Antigua",
        comment: "Muy entretenido, me mantuvo enganchado hasta el final.",
        rating: 4,
        date: "2025-10-10",
        likes: 8,
      },
    ]);

    const formatDate = (dateString) => {
      if (!dateString) return '';
      const date = new Date(dateString);
      return date.toLocaleDateString('es-ES', {
        year: 'numeric',
        month: 'short',
        day: 'numeric'
      });
    };

    const handleImageError = (event) => {
      event.target.src = '/programming-book-cover.jpg';
    };

    const handleViewDetails = (publication) => {
      console.log('Ver detalles:', publication);
      // TODO: Implementar navegación a detalles
    };

    const handleEdit = (publication) => {
      console.log('Editar:', publication);
      // TODO: Implementar edición
    };

    const handleDelete = (publication) => {
      if (confirm(`¿Estás seguro de que deseas eliminar "${publication.title}"?`)) {
        console.log('Eliminar:', publication);
        // TODO: Implementar eliminación
      }
    };

    const handleBookmarkClick = (bookmark) => {
      console.log('Bookmark clicked:', bookmark);
      // TODO: Navegar a detalles del bookmark
    };

    const handleEditComment = (comment) => {
      console.log('Editar comentario:', comment);
      // TODO: Implementar edición de comentario
    };

    const handleDeleteComment = (comment) => {
      if (confirm('¿Estás seguro de que deseas eliminar este comentario?')) {
        console.log('Eliminar comentario:', comment);
        // TODO: Implementar eliminación de comentario
      }
    };

    const handleUploadSuccess = () => {
      console.log('Publicación subida exitosamente');
      // TODO: Recargar las publicaciones del usuario
      showUploadModal.value = true;
    };

    return {
      activeTab,
      myPublications,
      myBookmarks,
      myComments,
      showUploadModal,
      formatDate,
      handleImageError,
      handleViewDetails,
      handleEdit,
      handleDelete,
      handleBookmarkClick,
      handleEditComment,
      handleDeleteComment,
      handleUploadSuccess
    };
  }
};
</script>

<style scoped>
.my-library-container {
  padding: 24px;
  max-width: 1400px;
  margin: 0 auto;
}

.library-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 32px;
}

.library-title {
  font-size: 32px;
  font-weight: bold;
  color: #1a202c;
}

.upload-btn {
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

.upload-btn:hover {
  background: #5568d3;
  transform: translateY(-2px);
}

.tabs-container {
  background: white;
  border-radius: 12px;
  overflow: hidden;
  min-width: 800px;
  max-width: 100%;
}

.tabs-list {
  display: flex;
  border-bottom: 1px solid #e2e8f0;
  background: #f7fafc;
}

.tab {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  padding: 16px 24px;
  background: transparent;
  border: none;
  color: #718096;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s;
  border-bottom: 2px solid transparent;
}

.tab:hover {
  background: white;
  color: #4a5568;
}

.tab.active {
  background: white;
  color: #667eea;
  border-bottom-color: #667eea;
}

.tab-content {
  padding: 24px;
  min-height: 400px;
  width: 100%;
}

.publications-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
  width: 94%;
}

.publication-card {
  background: white;
  border: 1px solid #e2e8f0;
  border-radius: 12px;
  padding: 20px;
  transition: all 0.3s;
}

.publication-card:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  border-color: #cbd5e0;
}

.publication-content {
  display: flex;
  gap: 20px;
}

.publication-cover {
  width: 96px;
  height: 128px;
  object-fit: cover;
  border-radius: 8px;
  flex-shrink: 0;
}

.publication-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.publication-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
}

.publication-title {
  font-size: 18px;
  font-weight: 600;
  color: #1a202c;
  margin-bottom: 4px;
}

.publication-description {
  font-size: 14px;
  color: #718096;
  margin: 0;
}

.status-badge {
  padding: 4px 12px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 500;
}

.status-badge.approved {
  background: #c6f6d5;
  color: #22543d;
}

.status-badge.pending {
  background: #fed7d7;
  color: #742a2a;
}

.publication-stats {
  display: flex;
  gap: 20px;
  flex-wrap: wrap;
}

.stat {
  display: flex;
  align-items: center;
  gap: 6px;
  color: #718096;
  font-size: 13px;
}

.stat svg {
  color: #a0aec0;
}

.publication-actions {
  display: flex;
  gap: 8px;
  padding-top: 8px;
}

.action-btn {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 8px 16px;
  border: 1px solid #e2e8f0;
  border-radius: 6px;
  background: white;
  color: #4a5568;
  font-size: 13px;
  cursor: pointer;
  transition: all 0.2s;
}

.action-btn:hover {
  background: #f7fafc;
  border-color: #cbd5e0;
}

.action-btn.danger {
  color: #e53e3e;
}

.action-btn.danger:hover {
  background: #fff5f5;
  border-color: #fc8181;
}

.bookmarks-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 20px;
  width: 94%;
}

.bookmark-card {
  background: white;
  border: 1px solid #e2e8f0;
  border-radius: 12px;
  overflow: hidden;
  cursor: pointer;
  transition: all 0.3s;
}

.bookmark-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 16px rgba(0, 0, 0, 0.1);
  border-color: #667eea;
}

.bookmark-cover-container {
  position: relative;
  width: 100%;
  aspect-ratio: 16/9;
}

.bookmark-cover {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.book-count-badge {
  position: absolute;
  top: 12px;
  right: 12px;
  padding: 6px 12px;
  background: rgba(102, 126, 234, 0.9);
  color: white;
  border-radius: 16px;
  font-size: 12px;
  font-weight: 500;
}

.bookmark-info {
  padding: 16px;
}

.bookmark-title {
  font-size: 16px;
  font-weight: 600;
  color: #1a202c;
  margin-bottom: 4px;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.bookmark-author {
  font-size: 13px;
  color: #718096;
  margin: 0 0 12px 0;
}

.bookmark-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.rating {
  display: flex;
  align-items: center;
  gap: 4px;
  color: #f6ad55;
  font-size: 14px;
  font-weight: 500;
}

.saved-date {
  font-size: 11px;
  color: #a0aec0;
}

.comments-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
  width: 94%;
}

.comment-card {
  background: white;
  border: 1px solid #e2e8f0;
  border-radius: 8px;
  padding: 16px;
}

.comment-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 12px;
}

.comment-book-title {
  font-size: 16px;
  font-weight: 600;
  color: #1a202c;
  margin: 0 0 8px 0;
}

.comment-meta {
  display: flex;
  align-items: center;
  gap: 12px;
}

.stars {
  display: flex;
  gap: 2px;
}

.star-filled {
  color: #f6ad55;
}

.star-empty {
  color: #e2e8f0;
}

.comment-date {
  font-size: 11px;
  color: #a0aec0;
}

.comment-actions {
  display: flex;
  gap: 8px;
}

.icon-btn {
  padding: 6px;
  background: transparent;
  border: none;
  color: #718096;
  cursor: pointer;
  border-radius: 4px;
  transition: all 0.2s;
}

.icon-btn:hover {
  background: #f7fafc;
  color: #4a5568;
}

.icon-btn.danger {
  color: #e53e3e;
}

.icon-btn.danger:hover {
  background: #fff5f5;
}

.comment-text {
  color: #4a5568;
  font-size: 14px;
  line-height: 1.6;
  margin: 0 0 12px 0;
}

.comment-likes {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 11px;
  color: #a0aec0;
}

.empty-state {
  text-align: center;
  padding: 80px 20px;
  color: #718096;
}

.empty-state svg {
  margin: 0 auto 20px;
  color: #cbd5e0;
}

.empty-state h3 {
  font-size: 20px;
  font-weight: 600;
  color: #2d3748;
  margin: 0 0 8px 0;
}

.empty-state p {
  font-size: 14px;
  margin: 0 0 20px 0;
}

/* Responsive design */
@media (max-width: 1024px) {
  .tabs-container {
    min-width: 600px;
  }

  .bookmarks-grid {
    grid-template-columns: repeat(auto-fill, minmax(240px, 1fr));
  }
}

@media (max-width: 768px) {
  .tabs-container {
    min-width: 100%;
  }

  .tab-content {
    padding: 16px;
    min-height: 300px;
  }

  .tabs-list {
    flex-direction: column;
  }

  .tab {
    border-bottom: 1px solid #e2e8f0;
    justify-content: flex-start;
  }

  .tab.active {
    border-bottom-color: #667eea;
    border-left: 3px solid #667eea;
  }

  .publication-content {
    flex-direction: column;
  }

  .publication-cover {
    width: 100%;
    height: auto;
    aspect-ratio: 3/4;
  }

  .bookmarks-grid {
    grid-template-columns: 1fr;
  }
}
</style>