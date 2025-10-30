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
        Favoritos
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
      <button
        :class="['tab-button', { active: activeTab === 'announcements' }]"
        @click="activeTab = 'announcements'"
      >
        Anuncios
      </button>
    </div>

    <!-- Publications Tab -->
    <div v-if="activeTab === 'publications'" class="tab-content">
      <div class="section-header">
        <h3>Gestión de Publicaciones de todos los usuarios</h3>
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
        <h3>Gestión de Favoritos de todos los usuarios</h3>
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

      <div v-if="authors.length === 0" class="no-results">
        No hay autores disponibles
      </div>

      <div v-else class="crud-table">
        <table class="table">
          <thead>
            <tr>
              <th>ID</th>
              <th>Nombre Completo</th>
              <th>Fecha de Nacimiento</th>
              <th>Nacionalidad</th>
              <th>Acciones</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="author in authors" :key="author.id">
              <td>{{ author.id }}</td>
              <td>{{ author.fullName }}</td>
              <td>{{ author.birthDate ? formatDate(author.birthDate) : 'N/A' }}</td>
              <td>{{ author.nationality || 'N/A' }}</td>
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

      <!-- Pagination for Authors -->
      <div class="pagination-controls" v-if="authorsPagination.totalPages > 1">
        <button @click="loadAuthors(authorsPagination.currentPage - 1)" :disabled="authorsPagination.currentPage === 0" class="pagination-btn">
          <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <polyline points="15 18 9 12 15 6"/>
          </svg>
          Anterior
        </button>
        <span class="pagination-info">Página {{ authorsPagination.currentPage + 1 }} de {{ authorsPagination.totalPages }}</span>
        <button @click="loadAuthors(authorsPagination.currentPage + 1)" :disabled="authorsPagination.currentPage >= authorsPagination.totalPages - 1" class="pagination-btn">
          Siguiente
          <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <polyline points="9 18 15 12 9 6"/>
          </svg>
        </button>
      </div>
    </div>

    <!-- Books Tab -->
    <div v-if="activeTab === 'books'" class="tab-content">
      <div class="section-header">
        <h3>Gestión de Libros de todos los usuarios</h3>
      </div>

      <div v-if="books.length === 0" class="no-results">
        No hay libros disponibles
      </div>

      <div v-else class="books-grid">
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
              {{ book.author?.fullName || 'Autor desconocido' }}
            </p>
          </div>
          <div class="book-actions">
            <button @click="handleViewBookDetails(book)" class="btn-icon" title="Ver detalles">
              <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <path d="M2 12s3-7 10-7 10 7 10 7-3 7-10 7-10-7-10-7Z"/>
                <circle cx="12" cy="12" r="3"/>
              </svg>
            </button>
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
      <div class="pagination-section" v-if="booksPagination.totalPages > 0">
        <div class="pagination-controls">
          <button
            @click="goToPreviousBooksPage()"
            :disabled="booksPagination.currentPage === 0"
            class="pagination-btn"
            v-if="booksPagination.currentPage > 0"
          >
            <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <polyline points="15 18 9 12 15 6"/>
            </svg>
            Anterior
          </button>

          <div class="pagination-info-container">
            <span class="pagination-text">Página</span>
            <input
              type="number"
              v-model.number="manualBooksPage"
              @keyup.enter="goToManualBooksPage"
              @blur="goToManualBooksPage"
              :min="1"
              :max="booksPagination.totalPages"
              class="page-input"
            />
            <span class="pagination-text">de {{ booksPagination.totalPages }}</span>
          </div>

          <button
            @click="goToNextBooksPage()"
            :disabled="booksPagination.currentPage >= booksPagination.totalPages - 1"
            class="pagination-btn"
            v-if="booksPagination.currentPage < booksPagination.totalPages - 1"
          >
            Siguiente
            <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <polyline points="9 18 15 12 9 6"/>
            </svg>
          </button>
        </div>
      </div>
    </div>

    <!-- Ratings Tab -->
    <div v-if="activeTab === 'ratings'" class="tab-content">
      <div class="section-header">
        <h3>Gestión de Ratings de todos los usuarios</h3>
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
              <button @click="handleEditRating(rating)" class="btn-icon" title="Editar rating">
                <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <path d="M17 3a2.85 2.83 0 1 1 4 4L7.5 20.5 2 22l1.5-5.5Z"/>
                </svg>
              </button>
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

    <!-- Announcements Tab -->
    <div v-if="activeTab === 'announcements'" class="tab-content">
      <div class="section-header">
        <h3>Gestión de Anuncios</h3>
        <button @click="showCreateAnnouncementModal = true" class="btn btn-primary">
          <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <line x1="12" y1="5" x2="12" y2="19"/>
            <line x1="5" y1="12" x2="19" y2="12"/>
          </svg>
          Crear Anuncio
        </button>
      </div>

      <div v-if="announcementsLoading" class="loading-state">
        <svg xmlns="http://www.w3.org/2000/svg" width="48" height="48" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2" class="spinner">
          <path d="M21 12a9 9 0 1 1-6.219-8.56"/>
        </svg>
        <p>Cargando anuncios...</p>
      </div>

      <div v-else-if="announcements.length > 0" class="announcements-list">
        <div
          v-for="announcement in announcements"
          :key="announcement.id"
          class="announcement-card"
          @click="goToAnnouncementDetail(announcement.id)"
        >
          <div :class="['announcement-icon', `icon-${getAnnouncementTypeClass(announcement.type)}`]">
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
                <span :class="['badge', `badge-${getAnnouncementTypeClass(announcement.type)}`]">
                  {{ getAnnouncementTypeLabel(announcement.type) }}
                </span>
                <span :class="['badge', `badge-audience-${getAnnouncementAudienceClass(announcement.targetAudience)}`]">
                  {{ getAnnouncementAudienceLabel(announcement.targetAudience) }}
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

      <!-- Pagination for Announcements -->
      <div class="pagination-controls" v-if="announcementsPagination.totalPages > 1">
        <button @click="loadAnnouncementsPage(announcementsPagination.currentPage - 1)" :disabled="announcementsPagination.currentPage === 0" class="pagination-btn">
          <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <polyline points="15 18 9 12 15 6"/>
          </svg>
          Anterior
        </button>
        <span class="pagination-info">Página {{ announcementsPagination.currentPage + 1 }} de {{ announcementsPagination.totalPages }}</span>
        <button @click="loadAnnouncementsPage(announcementsPagination.currentPage + 1)" :disabled="announcementsPagination.currentPage >= announcementsPagination.totalPages - 1" class="pagination-btn">
          Siguiente
          <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <polyline points="9 18 15 12 9 6"/>
          </svg>
        </button>
      </div>
    </div>

    <!-- Category Modals -->
    <div v-if="showCreateCategoryModal" class="modal-overlay" @click="showCreateCategoryModal = false">
      <div class="modal-content" @click.stop>
        <h3>Crear Nueva Categoría</h3>
        <form @submit.prevent="handleCreateCategory" class="category-form">
          <div class="form-group">
            <label for="categoryName">Nombre de la Categoría</label>
            <input
              id="categoryName"
              v-model="newCategoryName"
              type="text"
              placeholder="Ej: Ciencia Ficción"
              required
              class="form-input"
            />
          </div>
          <div class="form-actions">
            <button type="button" @click="showCreateCategoryModal = false" class="btn btn-secondary">
              Cancelar
            </button>
            <button type="submit" class="btn btn-primary">
              Crear Categoría
            </button>
          </div>
        </form>
      </div>
    </div>

    <div v-if="showEditCategoryModal" class="modal-overlay" @click="showEditCategoryModal = false">
      <div class="modal-content" @click.stop>
        <h3>Editar Categoría</h3>
        <form @submit.prevent="handleUpdateCategory" class="category-form">
          <div class="form-group">
            <label for="editCategoryName">Nombre de la Categoría</label>
            <input
              id="editCategoryName"
              v-model="editingCategory.name"
              type="text"
              placeholder="Ej: Ciencia Ficción"
              required
              class="form-input"
            />
          </div>
          <div class="form-actions">
            <button type="button" @click="showEditCategoryModal = false" class="btn btn-secondary">
              Cancelar
            </button>
            <button type="submit" class="btn btn-primary">
              Guardar Cambios
            </button>
          </div>
        </form>
      </div>
    </div>

    <!-- Author Modals -->
    <div v-if="showCreateAuthorModal" class="modal-overlay" @click="showCreateAuthorModal = false">
      <div class="modal-content modal-content-large" @click.stop>
        <h3>Crear Nuevo Autor</h3>
        <form @submit.prevent="handleCreateAuthor" class="author-form">
          <div class="form-group">
            <label for="authorFullName">Nombre Completo *</label>
            <input
              id="authorFullName"
              v-model="newAuthor.fullName"
              type="text"
              placeholder="Ej: Gabriel García Márquez"
              required
              class="form-input"
            />
          </div>
          <div class="form-group">
            <label for="authorBio">Biografía</label>
            <textarea
              id="authorBio"
              v-model="newAuthor.bio"
              placeholder="Escribe una breve biografía del autor..."
              rows="4"
              class="form-input form-textarea"
            />
          </div>
          <div class="form-group">
            <label for="authorBirthDate">Fecha de Nacimiento</label>
            <input
              id="authorBirthDate"
              v-model="newAuthor.birthDate"
              type="date"
              class="form-input"
            />
          </div>
          <div class="form-group">
            <label for="authorNationality">Nacionalidad</label>
            <input
              id="authorNationality"
              v-model="newAuthor.nationality"
              type="text"
              placeholder="Ej: Colombiana"
              class="form-input"
            />
          </div>
          <div class="form-actions">
            <button type="button" @click="showCreateAuthorModal = false" class="btn btn-secondary">
              Cancelar
            </button>
            <button type="submit" class="btn btn-primary">
              Crear Autor
            </button>
          </div>
        </form>
      </div>
    </div>

    <div v-if="showEditAuthorModal" class="modal-overlay" @click="showEditAuthorModal = false">
      <div class="modal-content modal-content-large" @click.stop>
        <h3>Editar Autor</h3>
        <form @submit.prevent="handleUpdateAuthor" class="author-form">
          <div class="form-group">
            <label for="editAuthorFullName">Nombre Completo *</label>
            <input
              id="editAuthorFullName"
              v-model="editingAuthor.fullName"
              type="text"
              placeholder="Ej: Gabriel García Márquez"
              required
              class="form-input"
            />
          </div>
          <div class="form-group">
            <label for="editAuthorBio">Biografía</label>
            <textarea
              id="editAuthorBio"
              v-model="editingAuthor.bio"
              placeholder="Escribe una breve biografía del autor..."
              rows="4"
              class="form-input form-textarea"
            />
          </div>
          <div class="form-group">
            <label for="editAuthorBirthDate">Fecha de Nacimiento</label>
            <input
              id="editAuthorBirthDate"
              v-model="editingAuthor.birthDate"
              type="date"
              class="form-input"
            />
          </div>
          <div class="form-group">
            <label for="editAuthorNationality">Nacionalidad</label>
            <input
              id="editAuthorNationality"
              v-model="editingAuthor.nationality"
              type="text"
              placeholder="Ej: Colombiana"
              class="form-input"
            />
          </div>
          <div class="form-actions">
            <button type="button" @click="showEditAuthorModal = false" class="btn btn-secondary">
              Cancelar
            </button>
            <button type="submit" class="btn btn-primary">
              Guardar Cambios
            </button>
          </div>
        </form>
      </div>
    </div>

    <!-- Edit Book Modal -->
    <div v-if="showEditBookModal" class="modal-overlay" @click="showEditBookModal = false">
      <div class="modal-content modal-content-large" @click.stop>
        <h3>Editar Libro</h3>
        <form @submit.prevent="handleUpdateBook" class="book-form">
          <div class="form-group">
            <label for="editBookTitle">Título del Libro *</label>
            <input
              id="editBookTitle"
              v-model="editingBook.title"
              type="text"
              placeholder="Ej: Clean Code"
              required
              class="form-input"
            />
          </div>
          <div class="form-group">
            <label for="editBookDescription">Descripción</label>
            <textarea
              id="editBookDescription"
              v-model="editingBook.description"
              placeholder="Descripción del libro..."
              rows="3"
              class="form-input form-textarea"
            />
          </div>
          <div class="form-group">
            <label for="editBookUrl">URL del Libro *</label>
            <input
              id="editBookUrl"
              v-model="editingBook.bookUrl"
              type="url"
              placeholder="https://ejemplo.com/libro.pdf"
              required
              class="form-input"
            />
          </div>
          <div class="form-group">
            <label for="editBookCoverImg">URL de la Portada</label>
            <input
              id="editBookCoverImg"
              v-model="editingBook.coverImg"
              type="url"
              placeholder="https://ejemplo.com/portada.jpg"
              class="form-input"
            />
          </div>
          <div class="form-actions">
            <button type="button" @click="showEditBookModal = false" class="btn btn-secondary">
              Cancelar
            </button>
            <button type="submit" class="btn btn-primary">
              Guardar Cambios
            </button>
          </div>
        </form>
      </div>
    </div>

    <!-- Edit Rating Modal -->
    <div v-if="showEditRatingModal" class="modal-overlay" @click="showEditRatingModal = false">
      <div class="modal-content modal-content-large" @click.stop>
        <h3>Editar Rating</h3>
        <form @submit.prevent="handleUpdateRating" class="rating-form">
          <div class="form-group">
            <label>Calificación *</label>
            <div class="rating-input-container">
              <RatingSystem
                :current-rating="editingRating.valoration"
                :readonly="false"
                size="lg"
                :show-value="true"
                @rating-change="handleRatingChange"
              />
            </div>
          </div>
          <div class="form-group">
            <label for="editRatingComment">Comentario</label>
            <textarea
              id="editRatingComment"
              v-model="editingRating.comment"
              placeholder="Escribe tu comentario sobre el libro..."
              rows="4"
              class="form-input form-textarea"
            />
          </div>
          <div class="form-actions">
            <button type="button" @click="showEditRatingModal = false" class="btn btn-secondary">
              Cancelar
            </button>
            <button type="submit" class="btn btn-primary">
              Guardar Cambios
            </button>
          </div>
        </form>
      </div>
    </div>

    <!-- Create Announcement Modal -->
    <div v-if="showCreateAnnouncementModal" class="modal-overlay" @click="showCreateAnnouncementModal = false">
      <div class="modal-content" @click.stop>
        <h3>Crear Nuevo Anuncio</h3>
        <form @submit.prevent="handleCreateAnnouncement" class="announcement-form">
          <div class="form-group">
            <label for="newAnnouncementTitle">Título *</label>
            <input
              id="newAnnouncementTitle"
              v-model="newAnnouncement.title"
              type="text"
              placeholder="Título del anuncio"
              required
              class="form-input"
            />
          </div>
          <div class="form-group">
            <label for="newAnnouncementMessage">Mensaje *</label>
            <textarea
              id="newAnnouncementMessage"
              v-model="newAnnouncement.message"
              placeholder="Escribe el mensaje del anuncio..."
              rows="6"
              required
              class="form-input form-textarea"
            />
          </div>
          <div class="form-row">
            <div class="form-group">
              <label for="newAnnouncementType">Tipo *</label>
              <select
                id="newAnnouncementType"
                v-model="newAnnouncement.type"
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
              <label for="newAnnouncementTargetAudience">Audiencia *</label>
              <select
                id="newAnnouncementTargetAudience"
                v-model="newAnnouncement.targetAudience"
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
            <button type="button" @click="showCreateAnnouncementModal = false" class="btn btn-secondary">
              Cancelar
            </button>
            <button type="submit" class="btn btn-primary" :disabled="isSubmittingAnnouncement">
              {{ isSubmittingAnnouncement ? 'Creando...' : 'Crear Anuncio' }}
            </button>
          </div>
        </form>
      </div>
    </div>

    <!-- Book Detail Modal -->
    <BookDetailModal
      v-if="selectedBook"
      :book="selectedBook"
      :is-open="showBookDetailModal"
      @close="showBookDetailModal = false"
      @rating-added="handleRatingAdded"
    />
  </div>
</template>

<script>
import { ref, onMounted, computed, watch } from 'vue';
import { useRouter } from 'vue-router';
import { publicationsAPI } from '../api/publicationsService';
import { categoriesAPI, favoritesAPI } from '../api/publicationsService';
import { booksAPI, ratingsAPI, authorsAPI } from '../api/booksService';
import { useAnnouncementsStore } from '../stores/announcements';
import { DEFAULT_BOOK_COVER } from '../utils/constants';
import BookDetailModal from '../components/BookDetailModal.vue';
import RatingSystem from '../components/RatingSystem.vue';

// Hardcoded UserProfile ID (por ahora, hasta implementar login)
const HARDCODED_USER_PROFILE_ID = 1;

export default {
  name: 'Moderation',
  components: {
    BookDetailModal,
    RatingSystem
  },
  setup() {
    const router = useRouter();
    const announcementsStore = useAnnouncementsStore();

    const activeTab = ref('publications');
    const filterStatus = ref('all');
    const publications = ref([]);
    const categories = ref([]);
    const allFavorites = ref([]);
    const authors = ref([]);
    const books = ref([]);
    const ratings = ref([]);
    const announcements = ref([]);
    const announcementsLoading = ref(false);

    const showCreateCategoryModal = ref(false);
    const showEditCategoryModal = ref(false);
    const showCreateAuthorModal = ref(false);
    const showEditAuthorModal = ref(false);
    const showEditBookModal = ref(false);
    const showBookDetailModal = ref(false);
    const showEditRatingModal = ref(false);
    const showCreateAnnouncementModal = ref(false);
    const selectedBook = ref(null);

    // Form states
    const newCategoryName = ref('');
    const editingCategory = ref({ id: null, name: '' });
    const newAuthor = ref({ fullName: '', bio: '', birthDate: '', nationality: '' });
    const editingAuthor = ref({ id: null, fullName: '', bio: '', birthDate: '', nationality: '' });
    const editingBook = ref({ id: null, title: '', description: '', bookUrl: '', coverImg: '' });
    const editingRating = ref({ id: null, valoration: 0, comment: '', bookId: null, userProfileId: null });
    const newAnnouncement = ref({ title: '', message: '', type: '', targetAudience: '' });
    const isSubmittingAnnouncement = ref(false);

    // Pagination states
    const favoritesPagination = ref({ currentPage: 0, totalPages: 1, pageSize: 12 });
    const authorsPagination = ref({ currentPage: 0, totalPages: 1, pageSize: 20 });
    const announcementsPagination = ref({ currentPage: 0, totalPages: 1, pageSize: 20 });
    const booksPagination = ref({ currentPage: 0, totalPages: 1, pageSize: 12 });
    const ratingsPagination = ref({ currentPage: 0, totalPages: 1, pageSize: 20 });
    const manualBooksPage = ref(1);

    // Watch for books page changes to update manual page input
    watch(() => booksPagination.value.currentPage, (newPage) => {
      manualBooksPage.value = newPage + 1;
    });

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

    const loadAuthors = async (page = 0) => {
      try {
        const response = await authorsAPI.getAuthorsByFilters({}, page);
        authors.value = response.data?.data || [];

        // Update pagination info
        authorsPagination.value.currentPage = page;
        authorsPagination.value.totalPages = response.data?.totalPages || 1;
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

    const handleEditCategory = (category) => {
      editingCategory.value = { ...category };
      showEditCategoryModal.value = true;
    };

    const handleCreateCategory = async () => {
      try {
        await categoriesAPI.create({ name: newCategoryName.value });
        showCreateCategoryModal.value = false;
        newCategoryName.value = '';
        await loadCategories();
        alert('Categoría creada exitosamente');
      } catch (error) {
        console.error('Error creating category:', error);
        alert('Error al crear la categoría');
      }
    };

    const handleUpdateCategory = async () => {
      try {
        await categoriesAPI.update(editingCategory.value.id, { name: editingCategory.value.name });
        showEditCategoryModal.value = false;
        editingCategory.value = { id: null, name: '' };
        await loadCategories();
        alert('Categoría actualizada exitosamente');
      } catch (error) {
        console.error('Error updating category:', error);
        alert('Error al actualizar la categoría');
      }
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

    const handleEditAuthor = (author) => {
      editingAuthor.value = { ...author };
      showEditAuthorModal.value = true;
    };

    const handleCreateAuthor = async () => {
      try {
        // Ensure bio is always sent (empty string if not provided)
        const authorData = {
          fullName: newAuthor.value.fullName,
          bio: newAuthor.value.bio || '',
          birthDate: newAuthor.value.birthDate,
          nationality: newAuthor.value.nationality
        };

        await authorsAPI.create(authorData);
        showCreateAuthorModal.value = false;
        newAuthor.value = { fullName: '', bio: '', birthDate: '', nationality: '' };
        await loadAuthors(authorsPagination.value.currentPage);
        alert('Autor creado exitosamente');
      } catch (error) {
        console.error('Error creating author:', error);
        alert('Error al crear el autor');
      }
    };

    const handleUpdateAuthor = async () => {
      try {
        // Ensure bio is always sent (empty string if not provided)
        const authorData = {
          fullName: editingAuthor.value.fullName,
          bio: editingAuthor.value.bio || '',
          birthDate: editingAuthor.value.birthDate,
          nationality: editingAuthor.value.nationality
        };

        await authorsAPI.update(editingAuthor.value.id, authorData);
        showEditAuthorModal.value = false;
        editingAuthor.value = { id: null, fullName: '', bio: '', birthDate: '', nationality: '' };
        await loadAuthors(authorsPagination.value.currentPage);
        alert('Autor actualizado exitosamente');
      } catch (error) {
        console.error('Error updating author:', error);
        alert('Error al actualizar el autor');
      }
    };

    const handleDeleteAuthor = async (authorId) => {
      if (confirm('¿Está seguro de eliminar este autor?')) {
        try {
          await authorsAPI.delete(authorId);
          await loadAuthors(authorsPagination.value.currentPage);
          alert('Autor eliminado exitosamente');
        } catch (error) {
          console.error('Error deleting author:', error);
          alert('Error al eliminar el autor');
        }
      }
    };

    const handleViewBookDetails = (book) => {
      selectedBook.value = book;
      showBookDetailModal.value = true;
    };

    const handleEditBook = (book) => {
      editingBook.value = { ...book };
      showEditBookModal.value = true;
    };

    const handleUpdateBook = async () => {
      try {
        const bookData = {
          title: editingBook.value.title,
          description: editingBook.value.description,
          bookUrl: editingBook.value.bookUrl,
          coverImg: editingBook.value.coverImg,
          publicationId: editingBook.value.publicationId,
          authorIds: editingBook.value.author ? [editingBook.value.author.id] : []
        };

        await booksAPI.updateBook(editingBook.value.id, bookData);
        showEditBookModal.value = false;
        editingBook.value = { id: null, title: '', description: '', bookUrl: '', coverImg: '' };
        await loadBooksPage(booksPagination.value.currentPage);
        alert('Libro actualizado exitosamente');
      } catch (error) {
        console.error('Error updating book:', error);
        alert('Error al actualizar el libro');
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

    // Pagination functions for books
    const goToPreviousBooksPage = async () => {
      if (booksPagination.value.currentPage > 0) {
        await loadBooksPage(booksPagination.value.currentPage - 1);
        window.scrollTo({ top: 0, behavior: 'smooth' });
      }
    };

    const goToNextBooksPage = async () => {
      if (booksPagination.value.currentPage < booksPagination.value.totalPages - 1) {
        await loadBooksPage(booksPagination.value.currentPage + 1);
        window.scrollTo({ top: 0, behavior: 'smooth' });
      }
    };

    const goToManualBooksPage = async () => {
      // Validate page number
      if (manualBooksPage.value < 1) {
        manualBooksPage.value = 1;
        return;
      }
      if (manualBooksPage.value > booksPagination.value.totalPages) {
        manualBooksPage.value = booksPagination.value.totalPages;
        return;
      }

      const targetPage = manualBooksPage.value - 1; // Convert to 0-indexed
      if (targetPage !== booksPagination.value.currentPage) {
        await loadBooksPage(targetPage);
        window.scrollTo({ top: 0, behavior: 'smooth' });
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

    const handleEditRating = (rating) => {
      editingRating.value = {
        id: rating.id,
        valoration: rating.valoration,
        comment: rating.comment || '',
        bookId: rating.bookId,
        userProfileId: rating.userProfileId
      };
      showEditRatingModal.value = true;
    };

    const handleRatingChange = (newRating) => {
      editingRating.value.valoration = newRating;
    };

    const handleUpdateRating = async () => {
      try {
        const ratingData = {
          valoration: editingRating.value.valoration,
          comment: editingRating.value.comment,
          bookId: editingRating.value.bookId,
          userProfileId: editingRating.value.userProfileId
        };

        await ratingsAPI.updateRating(editingRating.value.id, ratingData);
        showEditRatingModal.value = false;
        editingRating.value = { id: null, valoration: 0, comment: '', bookId: null, userProfileId: null };
        await loadRatingsPage(ratingsPagination.value.currentPage);
        alert('Rating actualizado exitosamente');
      } catch (error) {
        console.error('Error updating rating:', error);
        alert('Error al actualizar el rating');
      }
    };

    const handleRatingAdded = async () => {
      // Recargar ratings cuando se agrega uno nuevo desde el BookDetailModal
      await loadRatingsPage(ratingsPagination.value.currentPage);
    };

    const handleImageError = (event) => {
      event.target.src = DEFAULT_BOOK_COVER;
    };

    // Announcements functions
    const loadAnnouncementsPage = async (page = 0) => {
      announcementsLoading.value = true;
      try {
        await announcementsStore.fetchAnnouncements(page);
        announcements.value = announcementsStore.announcements.sort((a, b) => new Date(b.createdAt) - new Date(a.createdAt));
        announcementsPagination.value.currentPage = announcementsStore.currentPage;
        announcementsPagination.value.totalPages = announcementsStore.totalPages;
      } catch (error) {
        console.error('Error loading announcements:', error);
      } finally {
        announcementsLoading.value = false;
      }
    };

    const handleCreateAnnouncement = async () => {
      if (!newAnnouncement.value.title || !newAnnouncement.value.message || !newAnnouncement.value.type || !newAnnouncement.value.targetAudience) {
        alert('Por favor completa todos los campos');
        return;
      }

      isSubmittingAnnouncement.value = true;

      try {
        const announcementData = {
          title: newAnnouncement.value.title,
          message: newAnnouncement.value.message,
          type: newAnnouncement.value.type,
          targetAudience: newAnnouncement.value.targetAudience,
          userProfileId: HARDCODED_USER_PROFILE_ID
        };

        await announcementsStore.createAnnouncement(announcementData);
        alert('¡Anuncio creado exitosamente!');

        // Reset form
        newAnnouncement.value = {
          title: '',
          message: '',
          type: '',
          targetAudience: ''
        };
        showCreateAnnouncementModal.value = false;
        await loadAnnouncementsPage(announcementsPagination.value.currentPage);
      } catch (error) {
        console.error('Error creating announcement:', error);
        alert('Error al crear el anuncio. Por favor intenta de nuevo.');
      } finally {
        isSubmittingAnnouncement.value = false;
      }
    };

    const goToAnnouncementDetail = (id) => {
      router.push({ name: 'ModerationAnnouncementDetail', params: { id } });
    };

    const getAnnouncementTypeClass = (type) => {
      const typeMap = {
        'ALERT': 'alert',
        'INFO': 'info',
        'WARNING': 'warning',
        'PROMO': 'promo'
      };
      return typeMap[type] || 'info';
    };

    const getAnnouncementTypeLabel = (type) => {
      const labelMap = {
        'ALERT': 'Alerta',
        'INFO': 'Información',
        'WARNING': 'Advertencia',
        'PROMO': 'Promoción'
      };
      return labelMap[type] || type;
    };

    const getAnnouncementAudienceClass = (audience) => {
      const audienceMap = {
        'ALL': 'all',
        'NEW_USERS': 'new',
        'ADMINS': 'admin'
      };
      return audienceMap[audience] || 'all';
    };

    const getAnnouncementAudienceLabel = (audience) => {
      const labelMap = {
        'ALL': 'Todos',
        'NEW_USERS': 'Nuevos usuarios',
        'ADMINS': 'Administradores'
      };
      return labelMap[audience] || audience;
    };

    onMounted(async () => {
      await loadPublications();
      await loadCategories();
      await loadFavoritesPage(0);
      await loadAuthors();
      await loadBooksPage(0);
      await loadRatingsPage(0);
      await loadAnnouncementsPage(0);
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
      announcements,
      announcementsLoading,
      favoritesPagination,
      authorsPagination,
      booksPagination,
      ratingsPagination,
      announcementsPagination,
      showCreateCategoryModal,
      showEditCategoryModal,
      showCreateAuthorModal,
      showEditAuthorModal,
      showEditBookModal,
      showBookDetailModal,
      showEditRatingModal,
      showCreateAnnouncementModal,
      selectedBook,
      newCategoryName,
      editingCategory,
      newAuthor,
      editingAuthor,
      editingBook,
      editingRating,
      newAnnouncement,
      isSubmittingAnnouncement,
      manualBooksPage,
      formatDate,
      getStatusLabel,
      handleApprove,
      handleReject,
      handleViewDetails,
      handleCreateCategory,
      handleUpdateCategory,
      handleEditCategory,
      handleDeleteCategory,
      handleDeleteFavorite,
      handleCreateAuthor,
      handleUpdateAuthor,
      handleEditAuthor,
      handleDeleteAuthor,
      handleViewBookDetails,
      handleEditBook,
      handleUpdateBook,
      handleDeleteBook,
      goToPreviousBooksPage,
      goToNextBooksPage,
      goToManualBooksPage,
      handleDeleteRating,
      handleEditRating,
      handleRatingChange,
      handleUpdateRating,
      handleRatingAdded,
      handleImageError,
      loadFavoritesPage,
      loadAuthors,
      loadBooksPage,
      loadRatingsPage,
      loadAnnouncementsPage,
      handleCreateAnnouncement,
      goToAnnouncementDetail,
      getAnnouncementTypeClass,
      getAnnouncementTypeLabel,
      getAnnouncementAudienceClass,
      getAnnouncementAudienceLabel
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

.modal-content-large {
  max-width: 600px;
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

/* Category Form Styles */
.category-form {
  margin-top: 20px;
}

.form-group {
  margin-bottom: 20px;
}

.form-group label {
  display: block;
  margin-bottom: 8px;
  font-weight: 600;
  color: #2d3748;
  font-size: 14px;
}

.form-input {
  width: 100%;
  padding: 10px 14px;
  border: 1px solid #e2e8f0;
  border-radius: 6px;
  font-size: 14px;
  transition: all 0.2s;
}

.form-textarea {
  resize: vertical;
  min-height: 100px;
  font-family: inherit;
  line-height: 1.5;
}

.form-input:focus {
  outline: none;
  border-color: #667eea;
  box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
}

.form-actions {
  display: flex;
  gap: 12px;
  justify-content: flex-end;
  margin-top: 24px;
}

.btn-secondary {
  padding: 10px 20px;
  background: #e2e8f0;
  color: #4a5568;
  border: none;
  border-radius: 6px;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s;
}

.btn-secondary:hover {
  background: #cbd5e0;
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
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.3);
}

/* CRUD Table Styles */
.crud-table {
  background: white;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
}

.table {
  width: 100%;
  border-collapse: collapse;
}

.table thead {
  background: #f7fafc;
  border-bottom: 2px solid #e2e8f0;
}

.table th {
  padding: 12px 16px;
  text-align: left;
  font-weight: 600;
  color: #2d3748;
  font-size: 14px;
}

.table td {
  padding: 12px 16px;
  border-bottom: 1px solid #e2e8f0;
  color: #4a5568;
  font-size: 14px;
}

.table tbody tr:hover {
  background: #f7fafc;
}

.btn-icon {
  background: none;
  border: none;
  padding: 8px;
  cursor: pointer;
  color: #4a5568;
  border-radius: 4px;
  transition: all 0.2s;
  display: inline-flex;
  align-items: center;
  justify-content: center;
}

.btn-icon:hover {
  background: #edf2f7;
  color: #2d3748;
}

.btn-icon.danger {
  color: #e53e3e;
}

.btn-icon.danger:hover {
  background: #fff5f5;
  color: #c53030;
}

/* Favorites Grid */
.favorites-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
  gap: 20px;
  margin-bottom: 24px;
}

.favorite-card {
  background: white;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
  transition: all 0.2s;
  position: relative;
}

.favorite-card:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
  transform: translateY(-2px);
}

.favorite-cover {
  width: 100%;
  height: 200px;
  overflow: hidden;
  background: #f7fafc;
}

.favorite-cover img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.favorite-content {
  padding: 16px;
}

.favorite-title {
  font-size: 16px;
  font-weight: 600;
  color: #2d3748;
  margin: 0 0 8px 0;
  line-height: 1.4;
}

.favorite-user,
.favorite-date {
  font-size: 13px;
  color: #718096;
  margin: 4px 0;
  display: flex;
  align-items: center;
  gap: 6px;
}

.delete-btn {
  position: absolute;
  top: 8px;
  right: 8px;
  background: rgba(255, 255, 255, 0.9);
  border: none;
  padding: 8px;
  border-radius: 6px;
  cursor: pointer;
  color: #e53e3e;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.2s;
  opacity: 0;
}

.favorite-card:hover .delete-btn {
  opacity: 1;
}

.delete-btn:hover {
  background: #fff5f5;
  color: #c53030;
}

/* Books Grid */
.books-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 20px;
  margin-bottom: 24px;
}

.book-card {
  background: white;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
  transition: all 0.2s;
  display: flex;
  gap: 16px;
  padding: 16px;
}

.book-card:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.book-cover {
  width: 80px;
  height: 120px;
  flex-shrink: 0;
  border-radius: 4px;
  overflow: hidden;
  background: #f7fafc;
}

.book-cover img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.book-content {
  flex: 1;
  min-width: 0;
}

.book-title {
  font-size: 15px;
  font-weight: 600;
  color: #2d3748;
  margin: 0 0 8px 0;
  line-height: 1.4;
}

.book-author {
  font-size: 13px;
  color: #718096;
  margin: 4px 0;
  display: flex;
  align-items: center;
  gap: 6px;
}

.book-actions {
  display: flex;
  gap: 8px;
  align-items: flex-start;
}

/* Ratings List */
.ratings-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.rating-card {
  background: white;
  border-radius: 8px;
  padding: 16px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
  transition: all 0.2s;
}

.rating-card:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
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
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 600;
  font-size: 16px;
}

.rating-book-title {
  font-size: 15px;
  font-weight: 600;
  color: #2d3748;
  margin: 0;
}

.rating-user-name {
  font-size: 13px;
  color: #718096;
  margin: 4px 0 0 0;
}

.rating-actions {
  display: flex;
  align-items: center;
  gap: 12px;
}

.stars-display {
  display: flex;
  gap: 2px;
}

.star-filled {
  color: #f59e0b;
}

.star-empty {
  color: #d1d5db;
}

.rating-comment {
  color: #4a5568;
  font-size: 14px;
  line-height: 1.6;
  margin: 0 0 12px 0;
}

.rating-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 13px;
  color: #718096;
}

.rating-date,
.rating-score {
  display: flex;
  align-items: center;
  gap: 6px;
}

/* Pagination */
.pagination-section {
  margin-top: 40px;
  padding: 24px;
  background: white;
  border-radius: 12px;
  border: 1px solid #e2e8f0;
}

.pagination-controls {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 20px;
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
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.3);
}

.pagination-btn:disabled {
  background: #cbd5e0;
  cursor: not-allowed;
  transform: none;
  opacity: 0.6;
}

.pagination-info-container {
  display: flex;
  align-items: center;
  gap: 12px;
  font-weight: 500;
  color: #4a5568;
}

.pagination-text {
  font-size: 14px;
}

.page-input {
  width: 60px;
  padding: 8px 12px;
  border: 2px solid #e2e8f0;
  border-radius: 6px;
  text-align: center;
  font-size: 14px;
  font-weight: 600;
  color: #2d3748;
  transition: all 0.2s;
}

.page-input:focus {
  outline: none;
  border-color: #667eea;
  box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
}

.page-input::-webkit-inner-spin-button,
.page-input::-webkit-outer-spin-button {
  opacity: 1;
}

.pagination-info {
  font-size: 14px;
  color: #4a5568;
  font-weight: 500;
}

/* Author Form Styles */
.author-form {
  margin-top: 20px;
}

.author-form .form-group {
  margin-bottom: 20px;
}

/* Rating Form Styles */
.rating-form {
  margin-top: 20px;
}

.rating-form .form-group {
  margin-bottom: 20px;
}

.rating-input-container {
  padding: 16px;
  background: #f7fafc;
  border-radius: 8px;
  display: flex;
  justify-content: center;
}

/* Announcements Styles */
.announcements-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.announcement-card {
  display: flex;
  gap: 16px;
  padding: 20px;
  background: white;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  transition: all 0.3s ease;
  cursor: pointer;
}

.announcement-card:hover {
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.12);
  transform: translateY(-2px);
}

.announcement-icon {
  flex-shrink: 0;
  width: 48px;
  height: 48px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
}

.icon-alert {
  background: linear-gradient(135deg, #ef4444 0%, #dc2626 100%);
}

.icon-info {
  background: linear-gradient(135deg, #3b82f6 0%, #2563eb 100%);
}

.icon-warning {
  background: linear-gradient(135deg, #f59e0b 0%, #d97706 100%);
}

.icon-promo {
  background: linear-gradient(135deg, #8b5cf6 0%, #7c3aed 100%);
}

.announcement-content {
  flex: 1;
  min-width: 0;
}

.announcement-header-row {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 12px;
  margin-bottom: 8px;
}

.announcement-title {
  font-size: 18px;
  font-weight: 600;
  color: #1a202c;
  margin: 0;
  flex: 1;
}

.announcement-badges {
  display: flex;
  gap: 8px;
  flex-shrink: 0;
}

.badge {
  padding: 4px 12px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 500;
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
  background: #ede9fe;
  color: #6b21a8;
}

.badge-audience-all {
  background: #e0e7ff;
  color: #3730a3;
}

.badge-audience-new {
  background: #d1fae5;
  color: #065f46;
}

.badge-audience-admin {
  background: #fce7f3;
  color: #9f1239;
}

.announcement-message {
  color: #4a5568;
  font-size: 14px;
  line-height: 1.6;
  margin: 0 0 12px 0;
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
  gap: 4px;
}

.loading-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 60px 20px;
  color: #718096;
}

.loading-state svg {
  animation: spin 1s linear infinite;
  margin-bottom: 16px;
}

@keyframes spin {
  from {
    transform: rotate(0deg);
  }
  to {
    transform: rotate(360deg);
  }
}

.loading-state p {
  font-size: 16px;
  margin: 0;
}

.announcement-form .form-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 16px;
}

.form-select {
  appearance: none;
  background-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' width='12' height='12' viewBox='0 0 12 12'%3E%3Cpath fill='%23666' d='M6 9L1 4h10z'/%3E%3C/svg%3E");
  background-repeat: no-repeat;
  background-position: right 12px center;
  padding-right: 36px;
}
</style>