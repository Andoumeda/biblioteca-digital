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
        <button @click="activeTab = 'favorites'" :class="['tab', { active: activeTab === 'favorites' }]">
          <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M19 21l-7-5-7 5V5a2 2 0 0 1 2-2h10a2 2 0 0 1 2 2z"/>
          </svg>
          Favoritos ({{ myFavorites.length }})
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
        <div v-if="isLoading" class="loading-state">
          <svg xmlns="http://www.w3.org/2000/svg" width="48" height="48" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" class="spinner">
            <path d="M21 12a9 9 0 1 1-6.219-8.56"/>
          </svg>
          <p>Cargando publicaciones...</p>
        </div>
        <div v-else-if="myPublications.length > 0" class="publications-list">
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

      <!-- Pestaña de Favoritos -->
      <div v-show="activeTab === 'favorites'" class="tab-content">
        <div v-if="isLoadingFavorites" class="loading-state">
          <svg xmlns="http://www.w3.org/2000/svg" width="48" height="48" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" class="spinner">
            <path d="M21 12a9 9 0 1 1-6.219-8.56"/>
          </svg>
          <p>Cargando favoritos...</p>
        </div>
        <div v-else-if="myFavorites.length > 0" class="bookmarks-grid">
          <div v-for="favorite in myFavorites" :key="favorite.id" class="bookmark-card">
            <div class="bookmark-cover-container" @click="handleFavoriteClick(favorite)">
              <img
                :src="favorite.publication?.coverImage || (favorite.publication?.books?.[0]?.coverImg) || '/programming-book-cover.jpg'"
                :alt="favorite.publication?.title || 'Publicación'"
                class="bookmark-cover"
                @error="handleImageError"
              />
              <span class="book-count-badge">{{ favorite.publication?.books?.length || 0 }} libros</span>
            </div>
            <div class="bookmark-info" @click="handleFavoriteClick(favorite)">
              <div>
                <h3 class="bookmark-title">{{ favorite.publication?.title || 'Sin título' }}</h3>
                <p class="bookmark-author">por {{ favorite.publication?.userProfile?.displayName || 'Desconocido' }}</p>
              </div>
              <div class="bookmark-footer">
                <div class="rating">
                  <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="currentColor" stroke="currentColor" stroke-width="2">
                    <polygon points="12 2 15.09 8.26 22 9.27 17 14.14 18.18 21.02 12 17.77 5.82 21.02 7 14.14 2 9.27 8.91 8.26 12 2"/>
                  </svg>
                  <span>{{ favorite.averageRating || 'N/A' }}</span>
                </div>
                <span class="saved-date">Agregado {{ formatDate(favorite.createdAt) }}</span>
              </div>
            </div>
            <button @click.stop="handleDeleteFavorite(favorite)" class="delete-favorite-btn" title="Eliminar de favoritos">
              <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <path d="M3 6h18"/>
                <path d="M19 6v14c0 1-1 2-2 2H7c-1 0-2-1-2-2V6"/>
                <path d="M8 6V4c0-1 1-2 2-2h4c1 0 2 1 2 2v2"/>
                <line x1="10" x2="10" y1="11" y2="17"/>
                <line x1="14" x2="14" y1="11" y2="17"/>
              </svg>
            </button>
          </div>
        </div>

        <div v-else class="empty-state">
          <svg xmlns="http://www.w3.org/2000/svg" width="64" height="64" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M19 21l-7-5-7 5V5a2 2 0 0 1 2-2h10a2 2 0 0 1 2 2z"/>
          </svg>
          <h3>No tienes publicaciones favoritas</h3>
          <p>Explora y marca tus publicaciones favoritas</p>
        </div>
      </div>

      <!-- Pestaña de Comentarios -->
      <div v-show="activeTab === 'comments'" class="tab-content">
        <div v-if="isLoadingComments" class="loading-state">
          <svg xmlns="http://www.w3.org/2000/svg" width="48" height="48" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" class="spinner">
            <path d="M21 12a9 9 0 1 1-6.219-8.56"/>
          </svg>
          <p>Cargando comentarios...</p>
        </div>
        <div v-else-if="myComments.length > 0" class="comments-list">
          <div v-for="comment in myComments" :key="comment.id" class="comment-card">
            <div class="comment-header">
              <div>
                <h4 class="comment-book-title">{{ comment.book?.title || 'Libro desconocido' }}</h4>
                <div class="comment-meta">
                  <div class="stars">
                    <svg v-for="star in 5" :key="star" xmlns="http://www.w3.org/2000/svg" width="12" height="12" viewBox="0 0 24 24"
                      :fill="star <= comment.valoration ? 'currentColor' : 'none'"
                      stroke="currentColor"
                      stroke-width="2"
                      :class="star <= comment.valoration ? 'star-filled' : 'star-empty'">
                      <polygon points="12 2 15.09 8.26 22 9.27 17 14.14 18.18 21.02 12 17.77 5.82 21.02 7 14.14 2 9.27 8.91 8.26 12 2"/>
                    </svg>
                  </div>
                  <span class="comment-date">{{ formatDate(comment.createdAt) }}</span>
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
                    <path d="M8 6V4c0-1 1-2 2-2h4c1 0 2 1 2 2v2"/>
                  </svg>
                </button>
              </div>
            </div>
            <p class="comment-text">{{ comment.comment || 'Sin comentario' }}</p>
            <div class="comment-stats">
              <span class="comment-rating-label">Calificación: {{ comment.valoration }}/5</span>
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

    <!-- Modal de Detalles de Publicación -->
    <PublicationDetailModal
      v-if="showDetailModal && selectedPublication"
      :publication="selectedPublication"
      :is-open="showDetailModal"
      @close="closeDetailModal"
    />

    <!-- Modal de Editar Publicación -->
    <PublicationUploadModal
      v-if="showEditModal && publicationToEdit"
      :edit-mode="true"
      :publication-data="publicationToEdit"
      @close="closeEditModal"
      @success="handleEditSuccess"
    />
  </div>
</template>

<script>
import { ref, onMounted } from 'vue';
import PublicationUploadModal from '../components/PublicationUploadModal.vue';
import PublicationDetailModal from '../components/PublicationDetailModal.vue';
import { publicationsAPI, favoritesAPI } from '../api/publicationsService';
import { booksAPI, ratingsAPI } from '../api/booksService';

export default {
  name: 'MyLibrary',
  components: {
    PublicationUploadModal,
    PublicationDetailModal
  },
  setup() {
    const activeTab = ref('publications');
    const showUploadModal = ref(false);
    const showDetailModal = ref(false);
    const showEditModal = ref(false);
    const selectedPublication = ref(null);
    const publicationToEdit = ref(null);
    const isLoading = ref(false);
    const isLoadingFavorites = ref(false);
    const isLoadingComments = ref(false);

    // ID del usuario hardcoded
    const USER_PROFILE_ID = 1;

    const myPublications = ref([]);
    const myFavorites = ref([]);
    const myComments = ref([]);

    // Cargar favoritos del usuario desde la API
    const loadUserFavorites = async () => {
      try {
        isLoadingFavorites.value = true;
        const response = await favoritesAPI.getByUser(USER_PROFILE_ID, 0, 100);

        if (response.data && response.data.data) {
          const favorites = response.data.data;

          // Enriquecer cada favorito con los datos de la publicación
          for (const favorite of favorites) {
            try {
              const pubResponse = await publicationsAPI.getById(favorite.publicationId);
              if (pubResponse.data) {
                favorite.publication = pubResponse.data;

                // Cargar libros de la publicación
                const booksResponse = await booksAPI.getBooksByPublication(favorite.publicationId, 0);
                favorite.publication.books = booksResponse.data?.data || [];

                // Calcular rating promedio
                let totalRatings = 0;
                let sumRatings = 0;

                for (const book of favorite.publication.books) {
                  const ratingData = await ratingsAPI.getBookAverageRating(book.id);
                  totalRatings += ratingData.count;
                  sumRatings += ratingData.average * ratingData.count;
                }

                favorite.averageRating = totalRatings > 0 ? (sumRatings / totalRatings).toFixed(1) : 0;
              }
            } catch (error) {
              console.error(`Error al cargar publicación ${favorite.publicationId}:`, error);
            }
          }

          myFavorites.value = favorites;
        }
      } catch (error) {
        console.error('Error al cargar favoritos del usuario:', error);
      } finally {
        isLoadingFavorites.value = false;
      }
    };

    // Cargar comentarios/ratings del usuario desde la API
    const loadUserComments = async () => {
      try {
        isLoadingComments.value = true;
        const response = await ratingsAPI.getRatingsByUserProfile(USER_PROFILE_ID, 0);

        if (response.data && response.data.data) {
          const ratings = response.data.data;

          // Enriquecer cada rating con información del libro
          for (const rating of ratings) {
            try {
              const bookResponse = await booksAPI.getBookById(rating.bookId);
              if (bookResponse.data) {
                rating.book = bookResponse.data;
              }
            } catch (error) {
              console.error(`Error al cargar libro ${rating.bookId}:`, error);
            }
          }

          myComments.value = ratings;
        }
      } catch (error) {
        console.error('Error al cargar comentarios del usuario:', error);
      } finally {
        isLoadingComments.value = false;
      }
    };

    // Cargar publicaciones del usuario desde la API
    const loadUserPublications = async () => {
      try {
        isLoading.value = true;
        const response = await publicationsAPI.getByUser(USER_PROFILE_ID, 0, 100);

        if (response.data && response.data.data) {
          const publications = response.data.data;

          // Enriquecer cada publicación con sus libros y estadísticas
          for (const publication of publications) {
            // Cargar libros de la publicación
            const booksResponse = await booksAPI.getBooksByPublication(publication.id, 0);
            publication.books = booksResponse.data?.data || [];
            publication.bookCount = publication.books.length;

            // Calcular estadísticas de ratings
            let totalRatings = 0;
            let sumRatings = 0;

            for (const book of publication.books) {
              const ratingData = await ratingsAPI.getBookAverageRating(book.id);
              totalRatings += ratingData.count;
              sumRatings += ratingData.average * ratingData.count;
            }

            publication.totalRatings = totalRatings;
            publication.averageRating = totalRatings > 0 ? (sumRatings / totalRatings).toFixed(1) : 0;

            // Contar favoritos
            publication.favoriteCount = await favoritesAPI.countByPublication(publication.id);

            // Calcular descargas totales (placeholder)
            publication.totalDownloads = publication.books.reduce((sum, book) => sum + (book.downloadCount || 0), 0);

            // Determinar estado
            publication.status = publication.state || 'pending';
            publication.uploadDate = publication.createdAt;

            // Obtener portada del primer libro
            if (publication.books.length > 0 && publication.books[0].coverImg) {
              publication.cover = publication.books[0].coverImg;
            }
          }

          myPublications.value = publications;
        }
      } catch (error) {
        console.error('Error al cargar publicaciones del usuario:', error);
      } finally {
        isLoading.value = false;
      }
    };

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

    const handleViewDetails = async (publication) => {
      try {
        // Cargar detalles completos de la publicación
        const response = await publicationsAPI.getById(publication.id);

        if (response.data) {
          selectedPublication.value = {
            ...response.data,
            books: publication.books || [],
            favoriteCount: publication.favoriteCount || 0
          };
          showDetailModal.value = true;
        }
      } catch (error) {
        console.error('Error al cargar detalles de la publicación:', error);
        alert('Error al cargar los detalles de la publicación');
      }
    };

    const handleEdit = async (publication) => {
      try {
        // Cargar detalles completos para editar
        const response = await publicationsAPI.getById(publication.id);

        if (response.data) {
          publicationToEdit.value = {
            ...response.data,
            books: publication.books || []
          };
          showEditModal.value = true;
        }
      } catch (error) {
        console.error('Error al cargar publicación para editar:', error);
        alert('Error al cargar la publicación para editar');
      }
    };

    const handleDelete = async (publication) => {
      if (!confirm('¿Estás seguro de que deseas eliminar esta publicación? Esta acción no se puede deshacer.')) {
        return;
      }

      try {
        // Primero eliminar todos los libros de la publicación
        if (publication.books && publication.books.length > 0) {
          for (const book of publication.books) {
            await booksAPI.deleteBook(book.id);
            console.log(`Libro eliminado: ${book.id}`);
          }
        }

        // Luego eliminar la publicación
        await publicationsAPI.delete(publication.id);
        console.log(`Publicación eliminada: ${publication.id}`);

        // Recargar las publicaciones
        await loadUserPublications();

        alert('Publicación eliminada exitosamente');
      } catch (error) {
        console.error('Error al eliminar la publicación:', error);
        alert('Error al eliminar la publicación. Por favor, intenta de nuevo.');
      }
    };

    const handleFavoriteClick = async (favorite) => {
      if (favorite.publication) {
        selectedPublication.value = favorite.publication;
        showDetailModal.value = true;
      }
    };

    const handleDeleteFavorite = async (favorite) => {
      if (!confirm('¿Estás seguro de que deseas eliminar esta publicación de favoritos?')) {
        return;
      }

      try {
        await favoritesAPI.deleteFavorite(favorite.id);
        alert('Publicación eliminada de favoritos exitosamente');
        // Recargar la lista de favoritos
        await loadUserFavorites();
      } catch (error) {
        console.error('Error al eliminar favorito:', error);
        alert('Error al eliminar la publicación de favoritos. Por favor, intenta de nuevo.');
      }
    };

    const handleEditComment = async (comment) => {
      // TODO: Implementar modal de edición de comentario
      const newComment = prompt('Edita tu comentario:', comment.comment);
      const newRating = prompt('Edita tu calificación (1-5):', comment.valoration);

      if (newComment !== null && newRating !== null) {
        try {
          const payload = {
            bookId: comment.bookId,
            userProfileId: comment.userProfileId,
            valoration: parseInt(newRating),
            comment: newComment
          };

          await ratingsAPI.updateRating(comment.id, payload);
          alert('Comentario actualizado exitosamente');
          await loadUserComments();
        } catch (error) {
          console.error('Error al editar comentario:', error);
          alert('Error al editar el comentario');
        }
      }
    };

    const handleDeleteComment = async (comment) => {
      if (!confirm('¿Estás seguro de que deseas eliminar este comentario?')) {
        return;
      }

      try {
        await ratingsAPI.deleteRating(comment.id);
        alert('Comentario eliminado exitosamente');
        await loadUserComments();
      } catch (error) {
        console.error('Error al eliminar comentario:', error);
        alert('Error al eliminar el comentario');
      }
    };

    const handleUploadSuccess = async () => {
      console.log('Publicación subida exitosamente');
      showUploadModal.value = false;
      // Recargar las publicaciones del usuario
      await loadUserPublications();
    };

    const handleEditSuccess = async () => {
      console.log('Publicación editada exitosamente');
      showEditModal.value = false;
      publicationToEdit.value = null;
      // Recargar las publicaciones del usuario
      await loadUserPublications();
    };

    const closeDetailModal = () => {
      showDetailModal.value = false;
      selectedPublication.value = null;
    };

    const closeEditModal = () => {
      showEditModal.value = false;
      publicationToEdit.value = null;
    };

    // Cargar datos al montar el componente
    onMounted(() => {
      loadUserPublications();
      loadUserFavorites();
      loadUserComments();
    });

    return {
      activeTab,
      showUploadModal,
      showDetailModal,
      showEditModal,
      selectedPublication,
      publicationToEdit,
      isLoading,
      isLoadingFavorites,
      isLoadingComments,
      myPublications,
      myFavorites,
      myComments,
      formatDate,
      handleImageError,
      handleViewDetails,
      handleEdit,
      handleDelete,
      handleFavoriteClick,
      handleDeleteFavorite,
      handleEditComment,
      handleDeleteComment,
      handleUploadSuccess,
      handleEditSuccess,
      closeDetailModal,
      closeEditModal
    };
  }
};
</script>

<style scoped>
.my-library-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 2rem;
}

.library-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 2rem;
}

.library-title {
  font-size: 2rem;
  font-weight: 700;
  color: #1a1a1a;
}

.upload-btn {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  padding: 0.75rem 1.5rem;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border: none;
  border-radius: 8px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
}

.upload-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.4);
}

.tabs-container {
  background: white;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  overflow: hidden;
}

.tabs-list {
  display: flex;
  border-bottom: 2px solid #f0f0f0;
  background: #fafafa;
}

.tab {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 0.5rem;
  padding: 1rem;
  background: none;
  border: none;
  color: #666;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s ease;
  position: relative;
}

.tab:hover {
  background: #f0f0f0;
  color: #333;
}

.tab.active {
  color: #667eea;
  background: white;
}

.tab.active::after {
  content: '';
  position: absolute;
  bottom: -2px;
  left: 0;
  right: 0;
  height: 2px;
  background: #667eea;
}

.tab-content {
  padding: 2rem;
}

.loading-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 4rem 2rem;
  color: #666;
  width: 56.25rem;
}

.spinner {
  animation: spin 1s linear infinite;
  margin-bottom: 1rem;
}

@keyframes spin {
  from {
    transform: rotate(0deg);
  }
  to {
    transform: rotate(360deg);
  }
}

.publications-list {
  display: flex;
  flex-direction: column;
  gap: 1.5rem;
  width: 56.25rem;
}

.publication-card {
  border: 1px solid #e0e0e0;
  border-radius: 12px;
  overflow: hidden;
  transition: all 0.3s ease;
}

.publication-card:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  transform: translateY(-2px);
}

.publication-content {
  display: flex;
  gap: 1.5rem;
  padding: 1.5rem;
}

.publication-cover {
  width: 150px;
  height: 200px;
  object-fit: cover;
  border-radius: 8px;
  flex-shrink: 0;
}

.publication-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.publication-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 1rem;
}

.publication-title {
  font-size: 1.25rem;
  font-weight: 600;
  color: #1a1a1a;
  margin: 0 0 0.5rem 0;
}

.publication-description {
  color: #666;
  margin: 0;
}

.status-badge {
  padding: 0.25rem 0.75rem;
  border-radius: 12px;
  font-size: 0.875rem;
  font-weight: 500;
  white-space: nowrap;
}

.status-badge.approved {
  background: #d4edda;
  color: #155724;
}

.status-badge.pending {
  background: #fff3cd;
  color: #856404;
}

.publication-stats {
  display: flex;
  gap: 1.5rem;
  flex-wrap: wrap;
}

.stat {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  color: #666;
  font-size: 0.875rem;
}

.stat svg {
  color: #999;
}

.publication-actions {
  display: flex;
  gap: 0.75rem;
  margin-top: auto;
}

.action-btn {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  padding: 0.5rem 1rem;
  border: 1px solid #ddd;
  border-radius: 6px;
  background: white;
  color: #333;
  font-size: 0.875rem;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s ease;
}

.action-btn:hover {
  background: #f5f5f5;
  border-color: #bbb;
}

.action-btn.danger {
  color: #dc3545;
  border-color: #dc3545;
}

.action-btn.danger:hover {
  background: #dc3545;
  color: white;
}

.bookmarks-grid {
  display: flex;
  flex-direction: column;
  gap: 1.5rem;
  width: 56.25rem;
}

.bookmark-card {
  border: 1px solid #e0e0e0;
  border-radius: 12px;
  overflow: hidden;
  transition: all 0.3s ease;
  display: flex;
  flex-direction: row;
  height: 180px;
  position: relative;
}

.bookmark-card:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  transform: translateY(-4px);
}

.bookmark-cover-container,
.bookmark-info {
  cursor: pointer;
}

.delete-favorite-btn {
  position: absolute;
  top: 0.75rem;
  right: 0.75rem;
  width: 36px;
  height: 36px;
  padding: 0;
  background: rgba(255, 255, 255, 0.95);
  border: 1px solid #dc3545;
  border-radius: 8px;
  color: #dc3545;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.2s ease;
  z-index: 10;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.delete-favorite-btn:hover {
  background: #dc3545;
  color: white;
  transform: scale(1.1);
  box-shadow: 0 4px 8px rgba(220, 53, 69, 0.3);
}

.bookmark-cover-container {
  position: relative;
  width: 130px;
  min-width: 130px;
  height: 100%;
  overflow: hidden;
  flex-shrink: 0;
}

.bookmark-cover {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.book-count-badge {
  position: absolute;
  top: 0.5rem;
  right: 0.5rem;
  padding: 0.25rem 0.5rem;
  background: rgba(0, 0, 0, 0.7);
  color: white;
  border-radius: 4px;
  font-size: 0.75rem;
  font-weight: 500;
}

.bookmark-info {
  padding: 1.5rem;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  flex: 1;
  min-width: 0;
}

.bookmark-title {
  font-size: 1.125rem;
  font-weight: 600;
  color: #1a1a1a;
  margin: 0 0 0.5rem 0;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  line-height: 1.4;
}

.bookmark-author {
  font-size: 0.875rem;
  color: #666;
  margin: 0 0 auto 0;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.bookmark-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 0.875rem;
  color: #999;
  margin-top: 1rem;
  padding-top: 1rem;
  border-top: 1px solid #f0f0f0;
}

.rating {
  display: flex;
  align-items: center;
  gap: 0.25rem;
  color: #f39c12;
}

.saved-date {
  color: #999;
}

.comments-list {
  display: flex;
  flex-direction: column;
  gap: 1rem;
  width: 56.25rem;
}

.comment-card {
  border: 1px solid #e0e0e0;
  border-radius: 12px;
  padding: 1.5rem;
  background: white;
  transition: all 0.3s ease;
}

.comment-card:hover {
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.comment-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 1rem;
}

.comment-book-title {
  font-size: 1.125rem;
  font-weight: 600;
  color: #1a1a1a;
  margin: 0 0 0.5rem 0;
}

.comment-meta {
  display: flex;
  align-items: center;
  gap: 0.75rem;
}

.stars {
  display: flex;
  gap: 2px;
}

.star-filled {
  color: #f39c12;
}

.star-empty {
  color: #ddd;
}

.comment-date {
  font-size: 0.875rem;
  color: #999;
}

.comment-actions {
  display: flex;
  gap: 0.5rem;
}

.icon-btn {
  padding: 0.5rem;
  background: none;
  border: 1px solid #ddd;
  border-radius: 6px;
  cursor: pointer;
  transition: all 0.2s ease;
  display: flex;
  align-items: center;
  justify-content: center;
}

.icon-btn:hover {
  background: #f5f5f5;
}

.icon-btn.danger {
  color: #dc3545;
  border-color: #dc3545;
}

.icon-btn.danger:hover {
  background: #dc3545;
  color: white;
}

.comment-text {
  color: #333;
  line-height: 1.6;
  margin: 0 0 1rem 0;
}

.comment-stats {
  display: flex;
  align-items: center;
  gap: 1rem;
  font-size: 0.875rem;
  color: #666;
}

.comment-rating-label {
  font-weight: 500;
}

.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 4rem 2rem;
  text-align: center;
  color: #999;
}

.empty-state svg {
  margin-bottom: 1.5rem;
  color: #ddd;
}

.empty-state h3 {
  font-size: 1.5rem;
  font-weight: 600;
  color: #333;
  margin: 0 0 0.5rem 0;
}

.empty-state p {
  margin: 0 0 1.5rem 0;
}

@media (max-width: 768px) {
  .my-library-container {
    padding: 1rem;
  }

  .library-header {
    flex-direction: column;
    gap: 1rem;
  }

  .tabs-list {
    flex-direction: column;
  }

  .publication-content {
    flex-direction: column;
  }

  .publication-cover {
    width: 100%;
    max-width: 200px;
    margin: 0 auto;
  }

  .publication-actions {
    flex-direction: column;
  }

  .bookmarks-grid {
    grid-template-columns: repeat(auto-fill, minmax(150px, 1fr));
  }
}
</style>