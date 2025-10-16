<template>
  <div class="explore-container">
    <div class="explore-header">
      <h2 class="explore-title">Explorar Publicaciones</h2>
      <p class="explore-subtitle">Descubre las mejores publicaciones de la comunidad</p>
    </div>

    <div class="filters-section">
      <select v-model="sortBy" class="select-input">
        <option value="trending">Más populares</option>
        <option value="recent">Más recientes</option>
        <option value="rating">Mejor valoradas</option>
        <option value="popular">Más favoritos</option>
      </select>

      <div class="filter-buttons">
        <button @click="handleFilterChange('all')" :class="['filter-btn', { active: currentFilter === 'all' }]">
          Todas
        </button>
        <button @click="handleFilterChange('approved')" :class="['filter-btn', { active: currentFilter === 'approved' }]">
          Aprobadas
        </button>
      </div>
    </div>

    <div class="results-info">
      {{ store.loading ? 'Cargando...' : `${sortedPublications.length} publicaciones encontradas` }}
    </div>

    <div v-if="store.error" class="error-message">
      {{ store.error }}
    </div>

    <div class="publications-grid">
      <div
        v-for="publication in sortedPublications"
        :key="publication.id"
        class="publication-card"
        @click="handlePublicationClick(publication)"
      >
        <!-- Imagen de portada -->
        <div class="cover-image">
          <img
            :src="publication.coverImage || '/programming-book-cover.png'"
            :alt="publication.title"
            @error="handleImageError"
          />
          <span class="category-badge-overlay">{{ publication.categories?.[0]?.name || 'General' }}</span>
        </div>

        <!-- Contenido de la tarjeta -->
        <div class="card-content">
          <div class="publication-header">
            <div class="user-info">
              <div class="user-avatar">
                {{ publication.userProfile?.user?.username?.[0]?.toUpperCase() || 'U' }}
              </div>
              <div class="user-details">
                <p class="user-name">{{ publication.userProfile?.displayName || 'Desconocido' }}</p>
                <p class="username">@{{ publication.userProfile?.user?.username || 'usuario' }}</p>
              </div>
            </div>
            <span class="publication-date">{{ formatDate(publication.createdAt) }}</span>
          </div>

          <h3 class="publication-title">{{ publication.title }}</h3>
          <p class="publication-description">{{ publication.description }}</p>

          <div class="publication-categories">
            <span
              v-for="category in publication.categories?.slice(0, 3)"
              :key="category.id"
              class="category-badge"
            >
              {{ category.name }}
            </span>
          </div>

          <div class="publication-stats">
            <div class="stat-item">
              <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <path d="M4 19.5v-15A2.5 2.5 0 0 1 6.5 2H20v20H6.5a2.5 2.5 0 0 1 0-5H20"/>
              </svg>
              <span>{{ publication.books?.length || 0 }}</span>
            </div>
            <div class="stat-item">
              <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <path d="M19 21l-7-5-7 5V5a2 2 0 0 1 2-2h10a2 2 0 0 1 2 2z"/>
              </svg>
              <span>{{ publication.favoritesCount || 0 }}</span>
            </div>
            <div class="stat-item">
              <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <path d="M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z"/>
              </svg>
              <span>{{ publication.totalRatings || 0 }}</span>
            </div>
          </div>

          <div class="publication-actions">
            <button @click.stop="handleShare(publication)" class="action-button">
              <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <circle cx="18" cy="5" r="3"/>
                <circle cx="6" cy="12" r="3"/>
                <circle cx="18" cy="19" r="3"/>
                <line x1="8.59" x2="15.42" y1="13.51" y2="17.49"/>
                <line x1="15.41" x2="8.59" y1="6.51" y2="10.49"/>
              </svg>
              Compartir
            </button>
            <button @click.stop="handleBookmark(publication)" class="action-button primary">
              <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <path d="M19 21l-7-5-7 5V5a2 2 0 0 1 2-2h10a2 2 0 0 1 2 2z"/>
              </svg>
              Guardar
            </button>
          </div>
        </div>
      </div>
    </div>

    <div v-if="sortedPublications.length === 0 && !store.loading" class="no-results">
      <svg xmlns="http://www.w3.org/2000/svg" width="64" height="64" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
        <circle cx="11" cy="11" r="8"/>
        <path d="m21 21-4.35-4.35"/>
      </svg>
      <p>No se encontraron publicaciones</p>
    </div>

    <!-- Paginación -->
    <div class="pagination" v-if="!store.loading && store.totalPages > 1">
      <button @click="store.previousPage()" :disabled="store.currentPage === 0" class="pagination-btn">
        <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
          <polyline points="15 18 9 12 15 6"/>
        </svg>
        Anterior
      </button>
      <span class="pagination-info">Página {{ store.currentPage + 1 }} de {{ store.totalPages }}</span>
      <button @click="store.nextPage()" :disabled="!store.hasMore" class="pagination-btn">
        Siguiente
        <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
          <polyline points="9 18 15 12 9 6"/>
        </svg>
      </button>
    </div>
  </div>
</template>

<script>
import { usePublicationsStore } from '../stores/publicationsStore';
import { useFavoritesStore } from '../stores/favorites';
import { useBooksStore } from '../stores/books';
import { computed, onMounted, ref, watch } from 'vue';

export default {
  name: 'Explore',
  setup() {
    const store = usePublicationsStore();
    const favoritesStore = useFavoritesStore();
    const booksStore = useBooksStore();
    const searchTerm = ref('');
    const sortBy = ref('trending');
    const currentFilter = ref('all');

    const filteredPublications = computed(() => {
      let pubs = store.publications.filter(pub => {
        const matchesSearch =
          pub.title?.toLowerCase().includes(searchTerm.value.toLowerCase()) ||
          pub.description?.toLowerCase().includes(searchTerm.value.toLowerCase()) ||
          pub.userProfile?.displayName?.toLowerCase().includes(searchTerm.value.toLowerCase()) ||
          pub.userProfile?.user?.username?.toLowerCase().includes(searchTerm.value.toLowerCase());
        return matchesSearch;
      });

      // Add favorites count and book ratings
      return pubs.map(pub => {
        const books = pub.books || [];
        const totalBooksRating = books.reduce((sum, book) => {
          const rating = booksStore.getBookRating(book.id);
          return sum + (rating.average || 0);
        }, 0);
        const averageRating = books.length > 0 ? totalBooksRating / books.length : 0;

        return {
          ...pub,
          favoritesCount: favoritesStore.getFavoriteCountByPublication(pub.id),
          coverImage: books[0]?.coverImg || '/programming-book-cover.jpg',
          averageRating: parseFloat(averageRating.toFixed(1)),
          totalRatings: books.reduce((sum, book) => {
            const rating = booksStore.getBookRating(book.id);
            return sum + (rating.count || 0);
          }, 0)
        };
      });
    });

    const sortedPublications = computed(() => {
      const pubs = [...filteredPublications.value];

      switch (sortBy.value) {
        case 'recent':
          return pubs.sort((a, b) => new Date(b.createdAt) - new Date(a.createdAt));
        case 'rating':
          return pubs.sort((a, b) => (b.averageRating || 0) - (a.averageRating || 0));
        case 'popular':
          return pubs.sort((a, b) => (b.favoritesCount || 0) - (a.favoritesCount || 0));
        case 'trending':
        default:
          return pubs.sort((a, b) => (b.books?.length || 0) - (a.books?.length || 0));
      }
    });

    const formatDate = (dateString) => {
      if (!dateString) return '';
      const date = new Date(dateString);
      return date.toLocaleDateString('es-ES', {
        year: 'numeric',
        month: 'short',
        day: 'numeric'
      });
    };

    const handlePublicationClick = (publication) => {
      console.log('Publication clicked:', publication);
      // TODO: Navigate to publication detail
    };

    const handleShare = (publication) => {
      console.log('Share publication:', publication.title);
      // TODO: Implement share functionality
    };

    const handleBookmark = (publication) => {
      console.log('Bookmark publication:', publication.title);
      // TODO: Implement bookmark functionality
    };

    const handleImageError = (event) => {
      event.target.src = '/programming-book-cover.jpg';
    };

    const handleFilterChange = async (filter) => {
      currentFilter.value = filter;
      await store.setFilter(filter);
      await store.fetchCurrentPublicationsFavorites();
      await loadBooksRatings();
    };

    const loadBooksRatings = async () => {
      // Obtener todos los IDs de libros de las publicaciones actuales
      const bookIds = store.publications.flatMap(pub =>
        (pub.books || []).map(book => book.id)
      );

      // Cargar ratings de todos los libros
      if (bookIds.length > 0) {
        await booksStore.fetchMultipleBookRatings(bookIds);
      }
    };

    // Watch for search changes with debounce
    let searchTimeout = null;
    watch(searchTerm, () => {
      clearTimeout(searchTimeout);
      searchTimeout = setTimeout(async () => {
        if (searchTerm.value.trim()) {
          await store.searchPublications(searchTerm.value);
        } else {
          await store.fetchPublications();
        }
        await store.fetchCurrentPublicationsFavorites();
        await loadBooksRatings();
      }, 500);
    });

    onMounted(async () => {
      await store.fetchPublications();
      await favoritesStore.fetchFavorites();
      await store.fetchCurrentPublicationsFavorites();
      await loadBooksRatings();
    });

    return {
      store,
      searchTerm,
      sortBy,
      currentFilter,
      filteredPublications,
      sortedPublications,
      formatDate,
      handlePublicationClick,
      handleShare,
      handleBookmark,
      handleImageError,
      handleFilterChange
    };
  }
}
</script>

<style scoped>
.explore-container {
  padding: 24px;
  max-width: 1400px;
  margin: 0 auto;
}

.explore-header {
  margin-bottom: 24px;
}

.explore-title {
  font-size: 32px;
  font-weight: bold;
  color: #1a202c;
  margin-bottom: 8px;
}

.explore-subtitle {
  color: #718096;
  font-size: 16px;
}

.filters-section {
  display: flex;
  gap: 16px;
  flex-wrap: wrap;
  margin-bottom: 20px;
  align-items: center;
}


.select-input {
  padding: 10px 16px;
  border: 1px solid #e2e8f0;
  border-radius: 8px;
  font-size: 14px;
  background: white;
  cursor: pointer;
  min-width: 180px;
}

.select-input:focus {
  outline: none;
  border-color: #667eea;
}

.filter-buttons {
  display: flex;
  gap: 8px;
}

.filter-btn {
  padding: 10px 20px;
  border: 1px solid #e2e8f0;
  border-radius: 8px;
  background: white;
  color: #4a5568;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.2s;
}

.filter-btn:hover {
  border-color: #667eea;
  color: #667eea;
}

.filter-btn.active {
  background: #667eea;
  color: white;
  border-color: #667eea;
}

.results-info {
  color: #718096;
  font-size: 14px;
  margin-bottom: 20px;
}

.error-message {
  background-color: #fee;
  color: #c33;
  padding: 1rem;
  border-radius: 8px;
  margin-bottom: 1rem;
}

.publications-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
  gap: 24px;
  margin-bottom: 32px;
}

.publication-card {
  background: white;
  border: 1px solid #e2e8f0;
  border-radius: 12px;
  overflow: hidden;
  cursor: pointer;
  transition: all 0.3s;
}

.publication-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 12px 24px rgba(0, 0, 0, 0.1);
  border-color: #667eea;
}

.cover-image {
  position: relative;
  width: 100%;
  aspect-ratio: 16/9;
  overflow: hidden;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.cover-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s;
}

.publication-card:hover .cover-image img {
  transform: scale(1.05);
}

.category-badge-overlay {
  position: absolute;
  top: 12px;
  right: 12px;
  padding: 6px 12px;
  background: rgba(0, 0, 0, 0.7);
  backdrop-filter: blur(8px);
  color: white;
  border-radius: 16px;
  font-size: 12px;
  font-weight: 500;
}

.card-content {
  padding: 20px;
}

.publication-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 12px;
}

.user-info {
  display: flex;
  gap: 10px;
  align-items: center;
}

.user-avatar {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 600;
  font-size: 14px;
}

.user-details {
  display: flex;
  flex-direction: column;
}

.user-name {
  font-weight: 600;
  color: #2d3748;
  font-size: 13px;
  margin: 0;
}

.username {
  color: #718096;
  font-size: 12px;
  margin: 0;
}

.publication-date {
  font-size: 11px;
  color: #a0aec0;
}

.publication-title {
  font-size: 18px;
  font-weight: 600;
  color: #1a202c;
  margin-bottom: 8px;
  line-height: 1.4;
}

.publication-description {
  color: #4a5568;
  font-size: 14px;
  line-height: 1.5;
  margin-bottom: 12px;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.publication-categories {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
  margin-bottom: 12px;
}

.category-badge {
  padding: 4px 10px;
  background: #edf2f7;
  color: #4a5568;
  border-radius: 12px;
  font-size: 11px;
  font-weight: 500;
}

.publication-stats {
  display: flex;
  gap: 16px;
  margin-bottom: 12px;
  padding-top: 12px;
  border-top: 1px solid #e2e8f0;
}

.stat-item {
  display: flex;
  align-items: center;
  gap: 4px;
  color: #718096;
  font-size: 13px;
}

.stat-item svg {
  color: #a0aec0;
}

.publication-actions {
  display: flex;
  gap: 8px;
}

.action-button {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
  padding: 8px 12px;
  border: 1px solid #e2e8f0;
  border-radius: 6px;
  background: white;
  color: #4a5568;
  font-size: 13px;
  cursor: pointer;
  transition: all 0.2s;
}

.action-button:hover {
  background: #f7fafc;
  border-color: #cbd5e0;
}

.action-button.primary {
  background: #667eea;
  color: white;
  border-color: #667eea;
}

.action-button.primary:hover {
  background: #5568d3;
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