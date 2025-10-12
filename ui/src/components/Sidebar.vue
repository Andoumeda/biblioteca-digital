<template>
  <aside class="sidebar">
    <h2 class="logo">📘 DigiBooks Source</h2>
    <nav>
      <a href="#">🏠 Inicio</a>
      <a href="#" class="active">🔍 Explorar</a>
      <a href="#">📖 Mi Biblioteca</a>
      <a href="#">🛡 Moderación</a>
      <a href="#">⚙ Configuración</a>
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

const selectCategory = (categoryId) => {
  selectedCategory.value = categoryId;

  if (categoryId) {
    store.fetchPublicationsByCategory(categoryId);
  } else {
    store.fetchPublications();
  }
};
</script>

<style scoped>
.categories-section {
  margin-top: 2rem;
  padding-top: 1rem;
  border-top: 1px solid #e0e0e0;
}

.categories-section h3 {
  font-size: 0.9rem;
  font-weight: 600;
  margin-bottom: 0.5rem;
  padding: 0 1rem;
  color: #666;
}

.categories-list {
  display: flex;
  flex-direction: column;
  gap: 0.25rem;
}

.category-item {
  padding: 0.5rem 1rem;
  text-align: left;
  background: none;
  border: none;
  cursor: pointer;
  color: #333;
  font-size: 0.9rem;
  transition: background-color 0.2s;
}

.category-item:hover {
  background-color: #f0f0f0;
}

.category-item.active {
  background-color: #e3f2fd;
  color: #1976d2;
  font-weight: 500;
}

.loading, .error {
  padding: 0.5rem 1rem;
  font-size: 0.85rem;
  color: #666;
}

.error {
  color: #d32f2f;
}
</style>