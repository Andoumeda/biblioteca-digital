<template>
  <div class="moderation-container">
    <div class="moderation-header">
      <h2 class="moderation-title">Panel de Moderación</h2>
    </div>

    <div class="tabs">
      <button
        :class="['tab-button', { active: activeTab === 'publications' }]"
        @click="activeTab = 'publications'"
      >
        Publicaciones
      </button>
      <button
        :class="['tab-button', { active: activeTab === 'categories' }]"
        @click="activeTab = 'categories'"
      >
        Categorías
      </button>
      <button
        :class="['tab-button', { active: activeTab === 'favorites' }]"
        @click="activeTab = 'favorites'"
      >
        Favoritos (Todos)
      </button>
      <button
        :class="['tab-button', { active: activeTab === 'authors' }]"
        @click="activeTab = 'authors'"
      >
        Autores
      </button>
      <button
        :class="['tab-button', { active: activeTab === 'books' }]"
        @click="activeTab = 'books'"
      >
        Libros
      </button>
      <button
        :class="['tab-button', { active: activeTab === 'ratings' }]"
        @click="activeTab = 'ratings'"
      >
        Ratings
      </button>
    </div>

    <!-- Publications Tab -->
    <div v-if="activeTab === 'publications'" class="tab-content">
      <div class="section-header">
        <h3>Gestión de Publicaciones</h3>
        <select v-model="filterStatus" class="filter-select">
          <option value="all">Todos los estados</option>
          <option value="PENDING">Pendientes</option>
          <option value="APPROVED">Aprobados</option>
          <option value="REJECTED">Rechazados</option>
        </select>
      </div>

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
            <span :class="['status-badge', publication.state?.toLowerCase()]">
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
              v-if="publication.state === 'PENDING' || publication.state === 'pending'"
              @click="handleApprove(publication.id)"
              class="btn btn-approve"
            >
              Aprobar
            </button>
            <button
              v-if="publication.state === 'PENDING' || publication.state === 'pending'"
              @click="handleReject(publication.id)"
              class="btn btn-reject"
            >
              Rechazar
            </button>
            <button @click="handleViewDetails(publication)" class="btn btn-view">
              Ver detalles
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- Categories Tab -->
    <div v-if="activeTab === 'categories'" class="tab-content">
      <div class="section-header">
        <h3>Gestión de Categorías</h3>
        <button @click="showCreateCategoryModal = true" class="btn btn-primary">
          <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <line x1="12" y1="5" x2="12" y2="19"/>
            <line x1="5" y1="12" x2="19" y2="12"/>
          </svg>
          Nueva Categoría
        </button>
      </div>

      <div class="crud-table">
        <table class="table">
          <thead>
            <tr>
              <th>ID</th>
              <th>Nombre</th>
              <th>Acciones</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="category in categories" :key="category.id">
              <td>{{ category.id }}</td>
              <td>{{ category.name }}</td>
              <td>
                <button @click="handleEditCategory(category)" class="btn-icon">
                  <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                    <path d="M17 3a2.85 2.83 0 1 1 4 4L7.5 20.5 2 22l1.5-5.5Z"/>
                  </svg>
                </button>
                <button @click="handleDeleteCategory(category.id)" class="btn-icon danger">
                  <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                    <path d="M3 6h18"/>
                    <path d="M19 6v14c0 1-1 2-2 2H7c-1 0-2-1-2-2V6"/>
                  </svg>
                </button>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>

    <!-- Favorites Tab -->
    <div v-if="activeTab === 'favorites'" class="tab-content">
      <div class="section-header">
        <h3>Gestión de Favoritos (Todos los usuarios)</h3>
      </div>

      <div class="favorites-grid">
        <div v-for="favorite in allFavorites" :key="favorite.id" class="favorite-card">
          <div class="favorite-cover">
            <img
              :src="favorite.publication?.books?.[0]?.coverImg || '/default-book-cover.svg'"
              :alt="favorite.publication?.title"
              @error="handleImageError"
            />
          </div>
          <div class="favorite-content">
            <h4 class="favorite-title">{{ favorite.publication?.title || 'Publicación #' + favorite.publicationId }}</h4>
            <p class="favorite-user">
              <svg xmlns="http://www.w3.org/2000/svg" width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <path d="M19 21v-2a4 4 0 0 0-4-4H9a4 4 0 0 0-4 4v2"/>
                <circle cx="12" cy="7" r="4"/>
              </svg>
              {{ favorite.userProfile?.displayName || 'Usuario desconocido' }}
            </p>
            <p class="favorite-date">
              <svg xmlns="http://www.w3.org/2000/svg" width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <circle cx="12" cy="12" r="10"/>
                <polyline points="12 6 12 12 16 14"/>
              </svg>
              {{ formatDate(favorite.createdAt) }}
            </p>
          </div>
          <button @click="handleDeleteFavorite(favorite.id)" class="delete-btn" title="Eliminar favorito">
            <svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M3 6h18"/>
              <path d="M19 6v14c0 1-1 2-2 2H7c-1 0-2-1-2-2V6"/>
              <path d="M8 6V4c0-1 1-2 2-2h4c1 0 2 1 2 2v2"/>
              <line x1="10" x2="10" y1="11" y2="17"/>
              <line x1="14" x2="14" y1="11" y2="17"/>
            </svg>
          </button>
        </div>
      </div>

      <!-- Pagination for Favorites -->
      <div class="pagination-controls" v-if="favoritesPagination.totalPages > 1">
        <button @click="loadFavoritesPage(favoritesPagination.currentPage - 1)" :disabled="favoritesPagination.currentPage === 0" class="pagination-btn">
          <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <polyline points="15 18 9 12 15 6"/>
          </svg>
          Anterior
        </button>
        <span class="pagination-info">Página {{ favoritesPagination.currentPage + 1 }} de {{ favoritesPagination.totalPages }}</span>
        <button @click="loadFavoritesPage(favoritesPagination.currentPage + 1)" :disabled="favoritesPagination.currentPage >= favoritesPagination.totalPages - 1" class="pagination-btn">
          Siguiente
          <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <polyline points="9 18 15 12 9 6"/>
          </svg>
        </button>
      </div>
    </div>

    <!-- Authors Tab -->
    <div v-if="activeTab === 'authors'" class="tab-content">
      <div class="section-header">
        <h3>Gestión de Autores</h3>
        <button @click="showCreateAuthorModal = true" class="btn btn-primary">
          <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <line x1="12" y1="5" x2="12" y2="19"/>
            <line x1="5" y1="12" x2="19" y2="12"/>
          </svg>
          Nuevo Autor
        </button>
      </div>

      <div class="crud-table">
        <table class="table">
          <thead>
            <tr>
              <th>ID</th>
              <th>Nombre</th>
              <th>Acciones</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="author in authors" :key="author.id">
              <td>{{ author.id }}</td>
              <td>{{ author.name }}</td>
              <td>
                <button @click="handleEditAuthor(author)" class="btn-icon">
                  <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                    <path d="M17 3a2.85 2.83 0 1 1 4 4L7.5 20.5 2 22l1.5-5.5Z"/>
                  </svg>
                </button>
                <button @click="handleDeleteAuthor(author.id)" class="btn-icon danger">
                  <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                    <path d="M3 6h18"/>
                    <path d="M19 6v14c0 1-1 2-2 2H7c-1 0-2-1-2-2V6"/>
                    <line x1="10" x2="10" y1="11" y2="17"/>
                    <line x1="14" x2="14" y1="11" y2="17"/>
                  </svg>
                </button>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>

    <!-- Books Tab -->
    <div v-if="activeTab === 'books'" class="tab-content">
      <div class="section-header">
        <h3>Gestión de Libros</h3>
        <button @click="showCreateBookModal = true" class="btn btn-primary">
          <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <line x1="12" y1="5" x2="12" y2="19"/>
            <line x1="5" y1="12" x2="19" y2="12"/>
          </svg>
          Nuevo Libro
        </button>
      </div>

      <div class="books-grid">
        <div v-for="book in books" :key="book.id" class="book-card">
          <div class="book-cover">
            <img
              :src="book.coverImg || '/default-book-cover.svg'"
              :alt="book.title"
              @error="handleImageError"
            />
          </div>
          <div class="book-content">
            <h4 class="book-title">{{ book.title }}</h4>
            <p class="book-author">
              <svg xmlns="http://www.w3.org/2000/svg" width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"/>
                <circle cx="12" cy="7" r="4"/>
              </svg>
              {{ book.author?.name || 'Autor desconocido' }}
            </p>
            <p class="book-isbn">
              <svg xmlns="http://www.w3.org/2000/svg" width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <rect width="16" height="20" x="4" y="2" rx="2" ry="2"/>
                <line x1="8" x2="16" y1="6" y2="6"/>
                <line x1="8" x2="16" y1="10" y2="10"/>
                <line x1="8" x2="16" y1="14" y2="14"/>
                <line x1="8" x2="16" y1="18" y2="18"/>
              </svg>
              ISBN: {{ book.isbn || 'N/A' }}
            </p>
          </div>
          <div class="book-actions">
            <button @click="handleEditBook(book)" class="btn-icon" title="Editar">
              <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <path d="M17 3a2.85 2.83 0 1 1 4 4L7.5 20.5 2 22l1.5-5.5Z"/>
              </svg>
            </button>
            <button @click="handleDeleteBook(book.id)" class="btn-icon danger" title="Eliminar">
              <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <path d="M3 6h18"/>
                <path d="M19 6v14c0 1-1 2-2 2H7c-1 0-2-1-2-2V6"/>
                <line x1="10" x2="10" y1="11" y2="17"/>
                <line x1="14" x2="14" y1="11" y2="17"/>
              </svg>
            </button>
          </div>
        </div>
      </div>

      <!-- Pagination for Books -->
      <div class="pagination-controls" v-if="booksPagination.totalPages > 1">
        <button @click="loadBooksPage(booksPagination.currentPage - 1)" :disabled="booksPagination.currentPage === 0" class="pagination-btn">
          <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <polyline points="15 18 9 12 15 6"/>
          </svg>
          Anterior
        </button>
        <span class="pagination-info">Página {{ booksPagination.currentPage + 1 }} de {{ booksPagination.totalPages }}</span>
        <button @click="loadBooksPage(booksPagination.currentPage + 1)" :disabled="booksPagination.currentPage >= booksPagination.totalPages - 1" class="pagination-btn">
          Siguiente
          <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <polyline points="9 18 15 12 9 6"/>
          </svg>
        </button>
      </div>
    </div>

    <!-- Ratings Tab -->
    <div v-if="activeTab === 'ratings'" class="tab-content">
      <div class="section-header">
        <h3>Gestión de Ratings</h3>
      </div>

      <div class="ratings-list">
        <div v-for="rating in ratings" :key="rating.id" class="rating-card">
          <div class="rating-header">
            <div class="rating-user-info">
              <div class="user-avatar-small">
                {{ rating.userProfile?.displayName?.[0]?.toUpperCase() || 'U' }}
              </div>
              <div>
                <h4 class="rating-book-title">{{ rating.book?.title || 'Libro #' + rating.bookId }}</h4>
                <p class="rating-user-name">{{ rating.userProfile?.displayName || 'Usuario desconocido' }}</p>
              </div>
            </div>
            <div class="rating-actions">
              <div class="stars-display">
                <svg v-for="star in 5" :key="star" xmlns="http://www.w3.org/2000/svg" width="14" height="14" viewBox="0 0 24 24"
                  :fill="star <= rating.valoration ? 'currentColor' : 'none'"
                  stroke="currentColor"
                  stroke-width="2"
                  :class="star <= rating.valoration ? 'star-filled' : 'star-empty'">
                  <polygon points="12 2 15.09 8.26 22 9.27 17 14.14 18.18 21.02 12 17.77 5.82 21.02 7 14.14 2 9.27 8.91 8.26 12 2"/>
                </svg>
              </div>
              <button @click="handleDeleteRating(rating.id)" class="btn-icon danger" title="Eliminar rating">
                <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <path d="M3 6h18"/>
                  <path d="M19 6v14c0 1-1 2-2 2H7c-1 0-2-1-2-2V6"/>
                  <line x1="10" x2="10" y1="11" y2="17"/>
                  <line x1="14" x2="14" y1="11" y2="17"/>
                </svg>
              </button>
            </div>
          </div>
          <p class="rating-comment">{{ rating.comment || 'Sin comentario' }}</p>
          <div class="rating-footer">
            <span class="rating-date">{{ formatDate(rating.createdAt) }}</span>
            <span class="rating-score">Calificación: {{ rating.valoration }}/5</span>
          </div>
        </div>
      </div>

      <!-- Pagination for Ratings -->
      <div class="pagination-controls" v-if="ratingsPagination.totalPages > 1">
        <button @click="loadRatingsPage(ratingsPagination.currentPage - 1)" :disabled="ratingsPagination.currentPage === 0" class="pagination-btn">
          <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <polyline points="15 18 9 12 15 6"/>
          </svg>
          Anterior
        </button>
        <span class="pagination-info">Página {{ ratingsPagination.currentPage + 1 }} de {{ ratingsPagination.totalPages }}</span>
        <button @click="loadRatingsPage(ratingsPagination.currentPage + 1)" :disabled="ratingsPagination.currentPage >= ratingsPagination.totalPages - 1" class="pagination-btn">
          Siguiente
          <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <polyline points="9 18 15 12 9 6"/>
          </svg>
        </button>
      </div>
    </div>

    <!-- Simple Modals (placeholders for CRUD operations) -->
    <div v-if="showCreateCategoryModal" class="modal-overlay" @click="showCreateCategoryModal = false">
      <div class="modal-content" @click.stop>
        <h3>Crear Nueva Categoría</h3>
        <p>Funcionalidad de creación de categoría (por implementar con formulario completo)</p>
        <button @click="showCreateCategoryModal = false" class="btn">Cerrar</button>
      </div>
    </div>

    <div v-if="showCreateAuthorModal" class="modal-overlay" @click="showCreateAuthorModal = false">
      <div class="modal-content" @click.stop>
        <h3>Crear Nuevo Autor</h3>
        <p>Funcionalidad de creación de autor (por implementar con formulario completo)</p>
        <button @click="showCreateAuthorModal = false" class="btn">Cerrar</button>
      </div>
    </div>

    <div v-if="showCreateBookModal" class="modal-overlay" @click="showCreateBookModal = false">
      <div class="modal-content" @click.stop>
        <h3>Crear Nuevo Libro</h3>
        <p>Funcionalidad de creación de libro (por implementar con formulario completo)</p>
        <button @click="showCreateBookModal = false" class="btn">Cerrar</button>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, onMounted, computed } from 'vue';
import { publicationsAPI } from '../api/publicationsService';
import { categoriesAPI, favoritesAPI } from '../api/publicationsService';
import { booksAPI, ratingsAPI } from '../api/booksService';
import { DEFAULT_BOOK_COVER } from '../utils/constants';

export default {
  name: 'Moderation',
  setup() {
    const activeTab = ref('publications');
    const filterStatus = ref('all');
    const publications = ref([]);
    const categories = ref([]);
    const allFavorites = ref([]);
    const authors = ref([]);
    const books = ref([]);
    const ratings = ref([]);

    const showCreateCategoryModal = ref(false);
    const showCreateAuthorModal = ref(false);
    const showCreateBookModal = ref(false);

    // Pagination states
    const favoritesPagination = ref({ currentPage: 0, totalPages: 1, pageSize: 12 });
    const booksPagination = ref({ currentPage: 0, totalPages: 1, pageSize: 12 });
    const ratingsPagination = ref({ currentPage: 0, totalPages: 1, pageSize: 20 });

    const filteredPublications = computed(() => {
      let pubs = publications.value;

      if (filterStatus.value !== 'all') {
        pubs = pubs.filter(pub => pub.state === filterStatus.value);
      }

      return pubs;
    });

    const loadPublications = async () => {
      try {
        const response = await publicationsAPI.getPublications(0, 100);
        publications.value = response.data?.data || [];
      } catch (error) {
        console.error('Error loading publications:', error);
      }
    };

    const loadCategories = async () => {
      try {
        const response = await categoriesAPI.getAll(0, 100);
        categories.value = response.data?.data || [];
      } catch (error) {
        console.error('Error loading categories:', error);
      }
    };

    const loadFavoritesPage = async (page) => {
      try {
        const response = await favoritesAPI.getAllFavorites(page, favoritesPagination.value.pageSize);
        const favs = response.data?.data || [];

        // Update pagination info
        favoritesPagination.value.currentPage = page;
        favoritesPagination.value.totalPages = response.data?.totalPages || 1;

        // Enrich with publication data
        for (const fav of favs) {
          try {
            const pubResponse = await publicationsAPI.getById(fav.publicationId);
            fav.publication = pubResponse.data;
          } catch (error) {
            console.error('Error loading publication:', error);
          }
        }

        allFavorites.value = favs;
      } catch (error) {
        console.error('Error loading favorites:', error);
      }
    };

    const loadAuthors = async () => {
      try {
        const response = await booksAPI.getBooksByFilters({}, 0);
        const booksData = response.data?.data || [];
        const authorsMap = new Map();

        booksData.forEach(book => {
          if (book.author && !authorsMap.has(book.author.id)) {
            authorsMap.set(book.author.id, book.author);
          }
        });

        authors.value = Array.from(authorsMap.values());
      } catch (error) {
        console.error('Error loading authors:', error);
      }
    };

    const loadBooksPage = async (page) => {
      try {
        const response = await booksAPI.getBooksByFilters({}, page);
        books.value = response.data?.data || [];

        // Update pagination info
        booksPagination.value.currentPage = page;
        booksPagination.value.totalPages = response.data?.totalPages || 1;
      } catch (error) {
        console.error('Error loading books:', error);
      }
    };

    const loadRatingsPage = async (page) => {
      try {
        const response = await ratingsAPI.getRatingsByFilters({}, page);
        const ratingsData = response.data?.data || [];

        // Update pagination info
        ratingsPagination.value.currentPage = page;
        ratingsPagination.value.totalPages = response.data?.totalPages || 1;

        // Enrich with book data
        for (const rating of ratingsData) {
          try {
            const bookResponse = await booksAPI.getBookById(rating.bookId);
            rating.book = bookResponse.data;
          } catch (error) {
            console.error('Error loading book:', error);
          }
        }

        ratings.value = ratingsData;
      } catch (error) {
        console.error('Error loading ratings:', error);
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

    const getStatusLabel = (state) => {
      const labels = {
        'PENDING': 'Pendiente',
        'pending': 'Pendiente',
        'APPROVED': 'Aprobado',
        'approved': 'Aprobado',
        'REJECTED': 'Rechazado',
        'rejected': 'Rechazado'
      };
      return labels[state] || state;
    };

    const handleApprove = async (publicationId) => {
      try {
        await publicationsAPI.updateState(publicationId, 'APPROVED');
        await loadPublications();
      } catch (error) {
        console.error('Error approving publication:', error);
      }
    };

    const handleReject = async (publicationId) => {
      try {
        await publicationsAPI.updateState(publicationId, 'REJECTED');
        await loadPublications();
      } catch (error) {
        console.error('Error rejecting publication:', error);
      }
    };

    const handleViewDetails = (publication) => {
      console.log('View details:', publication);
    };

    const handleDeleteCategory = async (categoryId) => {
      if (confirm('¿Está seguro de eliminar esta categoría?')) {
        try {
          await categoriesAPI.delete(categoryId);
          await loadCategories();
          alert('Categoría eliminada exitosamente');
        } catch (error) {
          console.error('Error deleting category:', error);
          alert('Error al eliminar la categoría');
        }
      }
    };

    const handleDeleteFavorite = async (favoriteId) => {
      if (confirm('¿Está seguro de eliminar este favorito?')) {
        try {
          await favoritesAPI.delete(favoriteId);
          await loadFavoritesPage(favoritesPagination.value.currentPage);
          alert('Favorito eliminado exitosamente');
        } catch (error) {
          console.error('Error deleting favorite:', error);
          alert('Error al eliminar el favorito');
        }
      }
    };

    const handleDeleteBook = async (bookId) => {
      if (confirm('¿Está seguro de eliminar este libro?')) {
        try {
          await booksAPI.deleteBook(bookId);
          await loadBooksPage(booksPagination.value.currentPage);
          alert('Libro eliminado exitosamente');
        } catch (error) {
          console.error('Error deleting book:', error);
          alert('Error al eliminar el libro');
        }
      }
    };

    const handleDeleteRating = async (ratingId) => {
      if (confirm('¿Está seguro de eliminar este rating?')) {
        try {
          await ratingsAPI.deleteRating(ratingId);
          await loadRatingsPage(ratingsPagination.value.currentPage);
          alert('Rating eliminado exitosamente');
        } catch (error) {
          console.error('Error deleting rating:', error);
          alert('Error al eliminar el rating');
        }
      }
    };

    const handleImageError = (event) => {
      event.target.src = DEFAULT_BOOK_COVER;
    };

    onMounted(async () => {
      await loadPublications();
      await loadCategories();
      await loadFavoritesPage(0);
      await loadAuthors();
      await loadBooksPage(0);
      await loadRatingsPage(0);
    });

    return {
      activeTab,
      filterStatus,
      filteredPublications,
      categories,
      allFavorites,
      authors,
      books,
      ratings,
      favoritesPagination,
      booksPagination,
      ratingsPagination,
      showCreateCategoryModal,
      showCreateAuthorModal,
      showCreateBookModal,
      formatDate,
      getStatusLabel,
      handleApprove,
      handleReject,
      handleViewDetails,
      handleEditCategory,
      handleDeleteCategory,
      handleDeleteFavorite,
      handleEditAuthor,
      handleDeleteAuthor,
      handleEditBook,
      handleDeleteBook,
      handleDeleteRating,
      handleImageError,
      loadFavoritesPage,
      loadBooksPage,
      loadRatingsPage
    };
  }
};
</script>

<style scoped>
.moderation-container {
  padding: 2rem;
  max-width: 1200px;
  margin: 0 auto;
}

.moderation-header {
  margin-bottom: 2rem;
}

.moderation-title {
  font-size: 2rem;
  font-weight: 700;
  color: #1a1a1a;
}

.tabs {
  display: flex;
  gap: 1rem;
  margin-bottom: 2rem;
  border-bottom: 2px solid #e5e7eb;
  padding-bottom: 0.5rem;
}

.tab-button {
  padding: 0.75rem 1.5rem;
  border: none;
  background: none;
  color: #6b7280;
  font-weight: 500;
  cursor: pointer;
  border-radius: 0.5rem;
  transition: all 0.3s ease;
}

.tab-button:hover {
  background: #f3f4f6;
  color: #1f2937;
}

.tab-button.active {
  background: #667eea;
  color: white;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1.5rem;
}

.filter-select {
  padding: 0.5rem 1rem;
  border: 1px solid #d1d5db;
  border-radius: 0.5rem;
  background: white;
}

.moderation-card {
  background: white;
  border: 1px solid #e5e7eb;
  border-radius: 0.75rem;
  padding: 1.5rem;
  margin-bottom: 1rem;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 1rem;
}

.publication-title {
  font-size: 1.25rem;
  font-weight: 600;
  margin: 0;
  color: #1f2937;
}

.publication-meta {
  font-size: 0.875rem;
  color: #6b7280;
  margin: 0.25rem 0 0 0;
}

.status-badge {
  padding: 0.25rem 0.75rem;
  border-radius: 9999px;
  font-size: 0.875rem;
  font-weight: 500;
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
  color: #4b5563;
  margin-bottom: 1rem;
}

.publication-details {
  display: flex;
  gap: 1.5rem;
  margin-bottom: 1rem;
}

.detail-item {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  color: #6b7280;
  font-size: 0.875rem;
}

.categories {
  display: flex;
  flex-wrap: wrap;
  gap: 0.5rem;
  margin-bottom: 1rem;
}

.category-tag {
  background: #f3f4f6;
  color: #4b5563;
  padding: 0.25rem 0.75rem;
  border-radius: 9999px;
  font-size: 0.875rem;
}

.action-buttons {
  display: flex;
  gap: 0.75rem;
}

.btn {
  padding: 0.5rem 1rem;
  border-radius: 0.5rem;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s ease;
  border: 1px solid transparent;
}

.btn-approve {
  background: #059669;
  color: white;
}

.btn-approve:hover {
  background: #047857;
}

.btn-reject {
  background: #dc2626;
  color: white;
}

.btn-reject:hover {
  background: #b91c1c;
}

.btn-view {
  background: white;
  border-color: #d1d5db;
  color: #374151;
}

.btn-view:hover {
  background: #f3f4f6;
}

.no-results,
.reports-info,
.users-info {
  text-align: center;
  padding: 60px 20px;
  color: #718096;
  font-size: 16px;
}

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
  padding: 24px;
  border-radius: 12px;
  max-width: 500px;
  width: 100%;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.modal-content h3 {
  margin-top: 0;
}

.modal-content p {
  color: #4a5568;
  margin: 12px 0;
}

.modal-content .btn {
  margin-top: 12px;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.section-header h3 {
  font-size: 20px;
  font-weight: 600;
  color: #1a202c;
  margin: 0;
}

.filter-select {
  padding: 10px 16px;
  border: 1px solid #e2e8f0;
  border-radius: 6px;
  font-size: 14px;
  background: white;
  cursor: pointer;
}

.btn-primary {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px 20px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border: none;
  border-radius: 8px;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s;
}

.btn-primary:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.4);
}

.crud-table {
  background: white;
  border: 1px solid #e2e8f0;
  border-radius: 12px;
  overflow: hidden;
}

.table {
  width: 100%;
  border-collapse: collapse;
}

.table thead {
  background: #f7fafc;
}

.table th {
  padding: 14px 16px;
  text-align: left;
  font-weight: 600;
  font-size: 13px;
  color: #4a5568;
  text-transform: uppercase;
  letter-spacing: 0.05em;
  border-bottom: 2px solid #e2e8f0;
}

.table td {
  padding: 14px 16px;
  font-size: 14px;
  color: #1a202c;
  border-bottom: 1px solid #e2e8f0;
}

.table tbody tr:last-child td {
  border-bottom: none;
}

.table tbody tr:hover {
  background: #f9fafb;
}

.btn-icon {
  padding: 8px;
  background: none;
  border: 1px solid #e2e8f0;
  border-radius: 6px;
  color: #4a5568;
  cursor: pointer;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  transition: all 0.2s;
  margin-right: 8px;
}

.btn-icon:hover {
  background: #f7fafc;
  border-color: #cbd5e0;
}

.btn-icon.danger {
  color: #ef4444;
  border-color: #ef4444;
}

.btn-icon.danger:hover {
  background: #ef4444;
  color: white;
}

.favorites-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(220px, 1fr));
  gap: 16px;
}

.favorite-card {
  background: white;
  border: 1px solid #e2e8f0;
  border-radius: 12px;
  overflow: hidden;
  transition: all 0.2s;
}

.favorite-card:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
}

.favorite-cover {
  height: 120px;
  display: flex;
  justify-content: center;
  align-items: center;
  background: #f7fafc;
}

.favorite-cover img {
  max-height: 100%;
  max-width: 100%;
  object-fit: cover;
}

.favorite-content {
  padding: 16px;
}

.favorite-title {
  font-size: 16px;
  font-weight: 500;
  color: #1a202c;
  margin: 0 0 8px 0;
}

.favorite-user,
.favorite-date {
  font-size: 14px;
  color: #718096;
  margin: 0;
  display: flex;
  align-items: center;
  gap: 6px;
}

.delete-btn {
  background: none;
  border: none;
  color: #ef4444;
  cursor: pointer;
  transition: all 0.2s;
}

.delete-btn:hover {
  transform: scale(1.1);
}

.books-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(220px, 1fr));
  gap: 16px;
}

.book-card {
  background: white;
  border: 1px solid #e2e8f0;
  border-radius: 12px;
  overflow: hidden;
  transition: all 0.2s;
}

.book-card:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
}

.book-cover {
  height: 120px;
  display: flex;
  justify-content: center;
  align-items: center;
  background: #f7fafc;
}

.book-cover img {
  max-height: 100%;
  max-width: 100%;
  object-fit: cover;
}

.book-content {
  padding: 16px;
}

.book-title {
  font-size: 16px;
  font-weight: 500;
  color: #1a202c;
  margin: 0 0 8px 0;
}

.book-author,
.book-isbn {
  font-size: 14px;
  color: #718096;
  margin: 0;
  display: flex;
  align-items: center;
  gap: 6px;
}

.book-actions {
  display: flex;
  gap: 8px;
  padding-top: 12px;
  border-top: 1px solid #e2e8f0;
}

.ratings-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.rating-card {
  background: white;
  border: 1px solid #e2e8f0;
  border-radius: 12px;
  padding: 16px;
  transition: all 0.2s;
}

.rating-card:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
}

.rating-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.rating-user-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.user-avatar-small {
  width: 32px;
  height: 32px;
  background: #edf2f7;
  color: #4a5568;
  border-radius: 50%;
  display: flex;
  justify-content: center;
  align-items: center;
  font-size: 14px;
  font-weight: 500;
}

.rating-actions {
  display: flex;
  align-items: center;
  gap: 8px;
}

.stars-display {
  display: flex;
  align-items: center;
  gap: 2px;
}

.star-filled {
  color: #fbbf24;
}

.star-empty {
  color: #e2e8f0;
}

.rating-comment {
  color: #4a5568;
  font-size: 14px;
  margin: 8px 0;
}

.rating-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 13px;
  color: #718096;
}

.pagination-controls {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 12px;
  margin-top: 16px;
}

.pagination-btn {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 10px 16px;
  background: #f7fafc;
  color: #4a5568;
  border: 1px solid #e2e8f0;
  border-radius: 6px;
  cursor: pointer;
  transition: all 0.2s;
}

.pagination-btn:hover {
  background: #edf2f7;
}

.pagination-info {
  font-size: 14px;
  color: #4a5568;
}
</style>