<template>
  <aside class="sidebar">
    <nav>
      <router-link to="/" class="nav-link">
        <svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
          <path d="m3 9 9-7 9 7v11a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2z"/>
          <polyline points="9 22 9 12 15 12 15 22"/>
        </svg>
        Inicio
      </router-link>
      <router-link to="/explore" class="nav-link">
        <svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
          <circle cx="11" cy="11" r="8"/>
          <path d="m21 21-4.35-4.35"/>
        </svg>
        Explorar
      </router-link>
      <router-link to="/my-library" class="nav-link">
        <svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
          <path d="M4 19.5v-15A2.5 2.5 0 0 1 6.5 2H20v20H6.5a2.5 2.5 0 0 1 0-5H20"/>
        </svg>
        Mi Biblioteca
      </router-link>
      <router-link to="/moderation" class="nav-link">
        <svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
          <path d="M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10"/>
        </svg>
        Moderación
      </router-link>
    </nav>

    <!-- Sección de Categorías -->
    <div class="categories-section">
      <h3>Categorías</h3>
      <div v-if="loading" class="loading">Cargando...</div>
      <div v-else-if="error" class="error">{{ error }}</div>
      <div v-else class="categories-list">
        <button
          @click="selectCategory(null)"
          :class="{ active: selectedCategory === null }"
          class="category-item"
        >
          📚 Todas
        </button>
        <button
          v-for="category in categories"
          :key="category.id"
          @click="selectCategory(category.id)"
          :class="{ active: selectedCategory === category.id }"
          class="category-item"
        >
          {{ category.name }}
        </button>
      </div>
    </div>
  </aside>
</template>

<script setup>
import { useCategories } from '../composables/useCategories';
import { usePublicationsStore } from '../stores/publicationsStore';
import { ref } from 'vue';

const { categories, loading, error } = useCategories();
const store = usePublicationsStore();
const selectedCategory = ref(null);

const selectCategory = async (categoryId) => {
  selectedCategory.value = categoryId;

  if (categoryId) {
    await store.fetchPublicationsByCategory(categoryId);
  } else {
    await store.fetchPublications();
  }

  // Cargar favoritos después de obtener las publicaciones
  await store.fetchCurrentPublicationsFavorites();
};
</script>

<style scoped>
.sidebar {
  background: white;
  border-right: 1px solid #e2e8f0;
  height: 100%;
  display: flex;
  flex-direction: column;
}

nav {
  display: flex;
  flex-direction: column;
  padding: 1rem 0;
}

.nav-link {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 0.75rem 1rem;
  color: #4a5568;
  text-decoration: none;
  font-size: 0.95rem;
  transition: all 0.2s;
  border-left: 3px solid transparent;
}

.nav-link:hover {
  background-color: #f7fafc;
  color: #667eea;
}

.nav-link svg {
  flex-shrink: 0;
}

.categories-section {
  margin-top: 1rem;
  padding-top: 1rem;
  border-top: 1px solid #e2e8f0;
  flex: 1;
  overflow-y: auto;
}

.categories-section h3 {
  font-size: 0.85rem;
  font-weight: 600;
  margin-bottom: 0.75rem;
  padding: 0 1rem;
  color: #718096;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.categories-list {
  display: flex;
  flex-direction: column;
  gap: 0.25rem;
}

.category-item {
  padding: 0.65rem 1rem;
  text-align: left;
  background: none;
  border: none;
  cursor: pointer;
  color: #4a5568;
  font-size: 0.9rem;
  transition: all 0.2s;
  border-left: 3px solid transparent;
}

.category-item:hover {
  background-color: #f7fafc;
  color: #667eea;
}

.category-item.active {
  background-color: #edf2f7;
  color: #667eea;
  font-weight: 500;
  border-left-color: #667eea;
}

.loading, .error {
  padding: 0.5rem 1rem;
  font-size: 0.85rem;
  color: #718096;
}

.error {
  color: #ef4444;
}
</style>