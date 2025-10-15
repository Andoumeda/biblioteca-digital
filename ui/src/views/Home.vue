<template>
  <div class="home-container">
    <!-- Hero Section -->
    <div class="hero-section">
      <div class="hero-content">
        <h1 class="hero-title">Bienvenido a DigiBooks Source</h1>
        <p class="hero-subtitle">
          Descubre, comparte y disfruta de miles de publicaciones y libros digitales de la comunidad
        </p>
        <div class="hero-actions">
          <button @click="$router.push('/explore')" class="btn-primary">
            <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <circle cx="11" cy="11" r="8"/>
              <path d="m21 21-4.35-4.35"/>
            </svg>
            Explorar Publicaciones
          </button>
          <button @click="showUploadModal = true" class="btn-secondary">
            <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4"/>
              <polyline points="17 8 12 3 7 8"/>
              <line x1="12" x2="12" y1="3" y2="15"/>
            </svg>
            Subir Publicación
          </button>
        </div>
      </div>
      <div class="hero-illustration">
        <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 400 300" class="illustration-svg">
          <rect x="50" y="50" width="120" height="180" rx="8" fill="#667eea" opacity="0.8"/>
          <rect x="90" y="70" width="120" height="180" rx="8" fill="#764ba2" opacity="0.8"/>
          <rect x="130" y="90" width="120" height="180" rx="8" fill="#667eea" opacity="0.9"/>
          <circle cx="320" cy="100" r="40" fill="#f6ad55" opacity="0.6"/>
          <circle cx="340" cy="200" r="30" fill="#fc8181" opacity="0.6"/>
        </svg>
      </div>
    </div>

    <!-- Stats Section -->
    <div class="stats-section">
      <div class="stat-card">
        <div class="stat-icon" style="background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);">
          <svg xmlns="http://www.w3.org/2000/svg" width="32" height="32" viewBox="0 0 24 24" fill="none" stroke="white" stroke-width="2">
            <path d="M4 19.5v-15A2.5 2.5 0 0 1 6.5 2H20v20H6.5a2.5 2.5 0 0 1 0-5H20"/>
          </svg>
        </div>
        <div class="stat-content">
          <h3 class="stat-number">{{ stats.totalPublications }}</h3>
          <p class="stat-label">Publicaciones</p>
        </div>
      </div>

      <div class="stat-card">
        <div class="stat-icon" style="background: linear-gradient(135deg, #f6ad55 0%, #fc8181 100%);">
          <svg xmlns="http://www.w3.org/2000/svg" width="32" height="32" viewBox="0 0 24 24" fill="none" stroke="white" stroke-width="2">
            <path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"/>
            <circle cx="9" cy="7" r="4"/>
            <path d="M23 21v-2a4 4 0 0 0-3-3.87"/>
            <path d="M16 3.13a4 4 0 0 1 0 7.75"/>
          </svg>
        </div>
        <div class="stat-content">
          <h3 class="stat-number">{{ stats.totalUsers }}</h3>
          <p class="stat-label">Usuarios Activos</p>
        </div>
      </div>

      <div class="stat-card">
        <div class="stat-icon" style="background: linear-gradient(135deg, #48bb78 0%, #38b2ac 100%);">
          <svg xmlns="http://www.w3.org/2000/svg" width="32" height="32" viewBox="0 0 24 24" fill="none" stroke="white" stroke-width="2">
            <path d="M19 21l-7-5-7 5V5a2 2 0 0 1 2-2h10a2 2 0 0 1 2 2z"/>
          </svg>
        </div>
        <div class="stat-content">
          <h3 class="stat-number">{{ stats.totalFavorites }}</h3>
          <p class="stat-label">Favoritos</p>
        </div>
      </div>

      <div class="stat-card">
        <div class="stat-icon" style="background: linear-gradient(135deg, #ed64a6 0%, #9f7aea 100%);">
          <svg xmlns="http://www.w3.org/2000/svg" width="32" height="32" viewBox="0 0 24 24" fill="none" stroke="white" stroke-width="2">
            <path d="M12 2v20M17 5H9.5a3.5 3.5 0 0 0 0 7h5a3.5 3.5 0 0 1 0 7H6"/>
          </svg>
        </div>
        <div class="stat-content">
          <h3 class="stat-number">{{ stats.totalCategories }}</h3>
          <p class="stat-label">Categorías</p>
        </div>
      </div>
    </div>

    <!-- Featured Publications Section -->
    <div class="featured-section">
      <div class="section-header">
        <h2 class="section-title">Publicaciones Destacadas</h2>
        <button @click="$router.push('/explore')" class="view-all-btn">
          Ver todas
          <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <polyline points="9 18 15 12 9 6"/>
          </svg>
        </button>
      </div>

      <div class="featured-grid" v-if="!loading">
        <div
          v-for="publication in featuredPublications"
          :key="publication.id"
          class="featured-card"
          @click="handlePublicationClick(publication)"
        >
          <div class="featured-image">
            <img
              :src="publication.coverImage || '/programming-book-cover.jpg'"
              :alt="publication.title"
              @error="handleImageError"
            />
            <div class="featured-overlay">
              <span class="featured-badge">Destacado</span>
            </div>
          </div>
          <div class="featured-content">
            <h3 class="featured-title">{{ publication.title }}</h3>
            <p class="featured-author">por {{ publication.userProfile?.displayName || 'Desconocido' }}</p>
            <div class="featured-meta">
              <span class="meta-item">
                <svg xmlns="http://www.w3.org/2000/svg" width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <path d="M4 19.5v-15A2.5 2.5 0 0 1 6.5 2H20v20H6.5a2.5 2.5 0 0 1 0-5H20"/>
                </svg>
                {{ publication.books?.length || 0 }}
              </span>
              <span class="meta-item">
                <svg xmlns="http://www.w3.org/2000/svg" width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <path d="M19 21l-7-5-7 5V5a2 2 0 0 1 2-2h10a2 2 0 0 1 2 2z"/>
                </svg>
                {{ publication.favoritesCount || 0 }}
              </span>
            </div>
          </div>
        </div>
      </div>

      <div v-else class="loading-state">
        <p>Cargando publicaciones destacadas...</p>
      </div>
    </div>

    <!-- Categories Section -->
    <div class="categories-section">
      <div class="section-header">
        <h2 class="section-title">Explorar por Categoría</h2>
      </div>

      <div class="categories-grid">
        <div
          v-for="category in categories"
          :key="category.id"
          class="category-card"
          @click="handleCategoryClick(category)"
        >
          <div class="category-icon">
            {{ getCategoryEmoji(category.name) }}
          </div>
          <h3 class="category-name">{{ category.name }}</h3>
          <p class="category-count">{{ getCategoryCount(category.id) }} publicaciones</p>
        </div>
      </div>
    </div>

    <!-- Recent Activity Section -->
    <div class="activity-section">
      <div class="section-header">
        <h2 class="section-title">Actividad Reciente</h2>
      </div>

      <div class="activity-list">
        <div
          v-for="publication in recentPublications"
          :key="publication.id"
          class="activity-item"
        >
          <div class="activity-avatar">
            {{ publication.userProfile?.user?.username?.[0]?.toUpperCase() || 'U' }}
          </div>
          <div class="activity-content">
            <p class="activity-text">
              <strong>{{ publication.userProfile?.displayName || 'Usuario' }}</strong>
              publicó
              <strong>{{ publication.title }}</strong>
            </p>
            <p class="activity-time">{{ formatTimeAgo(publication.createdAt) }}</p>
          </div>
          <button @click="handlePublicationClick(publication)" class="activity-action">
            Ver
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { usePublicationsStore } from '../stores/publicationsStore';
import { useFavoritesStore } from '../stores/favorites';
import { useUsersStore } from '../stores/users';
import { useBooksStore } from '../stores/books';
import { computed, onMounted, ref } from 'vue';
import { useRouter } from 'vue-router';

export default {
  name: 'Home',
  setup() {
    const router = useRouter();
    const publicationsStore = usePublicationsStore();
    const favoritesStore = useFavoritesStore();
    const usersStore = useUsersStore();
    const booksStore = useBooksStore();
    const loading = ref(true);
    const showUploadModal = ref(false);

    const stats = computed(() => ({
      totalPublications: publicationsStore.totalItems || 0,
      totalUsers: usersStore.totalUsers || 0,
      totalFavorites: Object.values(favoritesStore.favoritesCount).reduce((a, b) => a + b, 0) || 0,
      totalCategories: publicationsStore.categories?.length || 0
    }));

    const featuredPublications = computed(() => {
      return publicationsStore.publications
        .slice(0, 6)
        .map(pub => ({
          ...pub,
          favoritesCount: favoritesStore.getFavoriteCountByPublication(pub.id),
          coverImage: pub.books?.[0]?.coverImg || '/programming-book-cover.jpg'
        }));
    });

    const recentPublications = computed(() => {
      return [...publicationsStore.publications]
        .sort((a, b) => new Date(b.createdAt) - new Date(a.createdAt))
        .slice(0, 5);
    });

    const categories = computed(() => publicationsStore.categories || []);

    const getCategoryEmoji = (name) => {
      const emojis = {
        'Tecnología': '💻',
        'Ficción': '📚',
        'Ciencia': '🔬',
        'Historia': '📜',
        'Arte': '🎨',
        'Cocina': '🍳',
        'Deportes': '⚽',
        'Música': '🎵',
        'Programación': '👨‍💻',
        'Ciencia Ficción': '🚀',
        'Fantasía': '🧙‍♂️',
        'Comedia': '😄'
      };
      return emojis[name] || '📖';
    };

    const getCategoryCount = (categoryId) => {
      return publicationsStore.publications.filter(pub =>
        pub.categories?.some(cat => cat.id === categoryId)
      ).length;
    };

    const formatTimeAgo = (dateString) => {
      if (!dateString) return '';
      const date = new Date(dateString);
      const now = new Date();
      const diffMs = now - date;
      const diffMins = Math.floor(diffMs / 60000);
      const diffHours = Math.floor(diffMins / 60);
      const diffDays = Math.floor(diffHours / 24);

      if (diffMins < 60) return `hace ${diffMins} minutos`;
      if (diffHours < 24) return `hace ${diffHours} horas`;
      return `hace ${diffDays} días`;
    };

    const handlePublicationClick = (publication) => {
      console.log('Publication clicked:', publication);
      // TODO: Navigate to publication detail
    };

    const handleCategoryClick = (category) => {
      router.push('/explore');
      // TODO: Filter by category
    };

    const handleImageError = (event) => {
      event.target.src = '/programming-book-cover.jpg';
    };

    onMounted(async () => {
      loading.value = true;

      // Cargar datos en paralelo
      await Promise.all([
        publicationsStore.fetchCategories(),
        publicationsStore.fetchPublications(),
        favoritesStore.fetchFavorites(),
        usersStore.fetchUsers(),
        usersStore.fetchUserProfiles(),
        booksStore.fetchBooks()
      ]);

      await publicationsStore.fetchCurrentPublicationsFavorites();

      loading.value = false;
    });

    return {
      loading,
      showUploadModal,
      stats,
      featuredPublications,
      recentPublications,
      categories,
      getCategoryEmoji,
      getCategoryCount,
      formatTimeAgo,
      handlePublicationClick,
      handleCategoryClick,
      handleImageError
    };
  }
}
</script>

<style scoped>
.home-container {
  padding: 24px;
  max-width: 1400px;
  margin: 0 auto;
}

/* Hero Section */
.hero-section {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 48px;
  align-items: center;
  padding: 60px 0;
  margin-bottom: 60px;
}

.hero-content {
  animation: fadeInUp 0.8s ease-out;
}

@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(30px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.hero-title {
  font-size: 48px;
  font-weight: bold;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  margin-bottom: 20px;
  line-height: 1.2;
}

.hero-subtitle {
  font-size: 20px;
  color: #718096;
  margin-bottom: 32px;
  line-height: 1.6;
}

.hero-actions {
  display: flex;
  gap: 16px;
  flex-wrap: wrap;
}

.btn-primary,
.btn-secondary {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 14px 28px;
  font-size: 16px;
  font-weight: 600;
  border-radius: 10px;
  cursor: pointer;
  transition: all 0.3s;
  border: none;
}

.btn-primary {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
}

.btn-primary:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 20px rgba(102, 126, 234, 0.4);
}

.btn-secondary {
  background: white;
  color: #667eea;
  border: 2px solid #667eea;
}

.btn-secondary:hover {
  background: #f7fafc;
  transform: translateY(-2px);
}

.hero-illustration {
  animation: float 3s ease-in-out infinite;
}

@keyframes float {
  0%, 100% { transform: translateY(0px); }
  50% { transform: translateY(-20px); }
}

.illustration-svg {
  width: 100%;
  max-width: 400px;
  height: auto;
}

/* Stats Section */
.stats-section {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 24px;
  margin-bottom: 60px;
}

.stat-card {
  display: flex;
  align-items: center;
  gap: 20px;
  background: white;
  padding: 24px;
  border-radius: 12px;
  border: 1px solid #e2e8f0;
  transition: all 0.3s;
}

.stat-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.1);
}

.stat-icon {
  width: 64px;
  height: 64px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.stat-number {
  font-size: 32px;
  font-weight: bold;
  color: #1a202c;
  margin: 0;
}

.stat-label {
  font-size: 14px;
  color: #718096;
  margin: 0;
}

/* Section Headers */
.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.section-title {
  font-size: 28px;
  font-weight: bold;
  color: #1a202c;
}

.view-all-btn {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 8px 16px;
  background: none;
  border: 1px solid #e2e8f0;
  border-radius: 8px;
  color: #667eea;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s;
}

.view-all-btn:hover {
  background: #f7fafc;
  border-color: #667eea;
}

/* Featured Publications */
.featured-section {
  margin-bottom: 60px;
}

.featured-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 24px;
}

.featured-card {
  background: white;
  border: 1px solid #e2e8f0;
  border-radius: 12px;
  overflow: hidden;
  cursor: pointer;
  transition: all 0.3s;
}

.featured-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 12px 24px rgba(0, 0, 0, 0.1);
}

.featured-image {
  position: relative;
  width: 100%;
  aspect-ratio: 4/3;
  overflow: hidden;
}

.featured-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s;
}

.featured-card:hover .featured-image img {
  transform: scale(1.1);
}

.featured-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(to bottom, rgba(0,0,0,0.3), transparent);
  display: flex;
  align-items: flex-start;
  padding: 12px;
}

.featured-badge {
  padding: 6px 12px;
  background: #f6ad55;
  color: white;
  border-radius: 16px;
  font-size: 12px;
  font-weight: 600;
}

.featured-content {
  padding: 16px;
}

.featured-title {
  font-size: 16px;
  font-weight: 600;
  color: #1a202c;
  margin-bottom: 6px;
  line-height: 1.4;
}

.featured-author {
  font-size: 14px;
  color: #718096;
  margin-bottom: 12px;
}

.featured-meta {
  display: flex;
  gap: 16px;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 13px;
  color: #718096;
}

/* Categories */
.categories-section {
  margin-bottom: 60px;
}

.categories-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: 16px;
}

.category-card {
  background: white;
  border: 1px solid #e2e8f0;
  border-radius: 12px;
  padding: 24px;
  text-align: center;
  cursor: pointer;
  transition: all 0.3s;
}

.category-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.1);
  border-color: #667eea;
}

.category-icon {
  font-size: 48px;
  margin-bottom: 12px;
}

.category-name {
  font-size: 16px;
  font-weight: 600;
  color: #1a202c;
  margin-bottom: 4px;
}

.category-count {
  font-size: 13px;
  color: #718096;
  margin: 0;
}

/* Activity */
.activity-section {
  margin-bottom: 40px;
}

.activity-list {
  background: white;
  border: 1px solid #e2e8f0;
  border-radius: 12px;
  overflow: hidden;
}

.activity-item {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 16px 20px;
  border-bottom: 1px solid #e2e8f0;
  transition: background 0.2s;
}

.activity-item:last-child {
  border-bottom: none;
}

.activity-item:hover {
  background: #f7fafc;
}

.activity-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 600;
  flex-shrink: 0;
}

.activity-content {
  flex: 1;
}

.activity-text {
  font-size: 14px;
  color: #4a5568;
  margin: 0 0 4px 0;
}

.activity-time {
  font-size: 12px;
  color: #a0aec0;
  margin: 0;
}

.activity-action {
  padding: 6px 16px;
  background: #667eea;
  color: white;
  border: none;
  border-radius: 6px;
  font-size: 13px;
  cursor: pointer;
  transition: all 0.2s;
}

.activity-action:hover {
  background: #5568d3;
}

.loading-state {
  text-align: center;
  padding: 40px;
  color: #718096;
}

@media (max-width: 768px) {
  .hero-section {
    grid-template-columns: 1fr;
    gap: 32px;
  }

  .hero-title {
    font-size: 36px;
  }

  .hero-illustration {
    display: none;
  }

  .stats-section {
    grid-template-columns: repeat(2, 1fr);
  }
}
</style>