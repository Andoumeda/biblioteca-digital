<template>
  <div class="layout">
    <Sidebar />

    <div class="main">
      <Topbar />

      <div class="content">
        <h2>Explorar Publicaciones</h2>

        <!-- Barra de búsqueda -->
        <div class="explore-header">
          <input
            type="text"
            v-model="searchQuery"
            @input="handleSearch"
            placeholder="Buscar por título, autor o usuario..."
          />
          <div class="filters">
            <button @click="handleFilterChange('all')" :class="{ active: currentFilter === 'all' }">
              Todos
            </button>
            <button
              @click="handleFilterChange('approved')"
              :class="{ active: currentFilter === 'approved' }"
            >
              Aprobados
            </button>
          </div>
          <div class="results">
            {{ store.loading ? 'Cargando...' : `${store.filteredPublications.length} publicaciones encontradas` }}
          </div>
        </div>

        <!-- Mensaje de error -->
        <div v-if="store.error" class="error-message">
          {{ store.error }}
        </div>

        <!-- Grid de publicaciones -->
        <BookGrid :books="mappedPublications" :loading="store.loading" />

        <!-- Paginación -->
        <div class="pagination" v-if="!store.loading && store.totalPages > 1">
          <button @click="store.previousPage()" :disabled="store.currentPage === 0">
            Anterior
          </button>
          <span>Página {{ store.currentPage + 1 }} de {{ store.totalPages }}</span>
          <button @click="store.nextPage()" :disabled="!store.hasMore">
            Siguiente
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { usePublicationsStore } from "./stores/publicationsStore";
import Sidebar from "./components/Sidebar.vue";
import Topbar from "./components/Topbar.vue";
import BookGrid from "./components/BookGrid.vue";
import { computed, onMounted, ref } from "vue";

export default {
  name: "App",
  components: { Sidebar, Topbar, BookGrid },
  setup() {
    const store = usePublicationsStore();
    const searchQuery = ref("");
    const currentFilter = ref("all");
    let searchTimeout = null;

    // Mapear publicaciones de la API al formato esperado por BookGrid
    const mappedPublications = computed(() => {
      return store.filteredPublications.map((pub) => ({
        id: pub.id,
        titulo: pub.title || "Sin título",
        autor: pub.userProfile?.displayName || "Desconocido",
        descripcion: pub.description || "Sin descripción",
        categoria: pub.categories?.[0]?.name || "Sin categoría",
        imagen: "/programming-book-cover.jpg", // Puedes agregar lógica para imágenes dinámicas
        rating: 0, // Puedes agregar lógica para ratings si está disponible
        votos: 0,
        favoritos: store.favoritesCount[pub.id] || 0, // Obtener del store
        comentarios: 0,
        usuario: `@${pub.userProfile?.user?.username || "usuario"}`,
        fecha: new Date(pub.createdAt).toLocaleDateString() || "Fecha desconocida",
        state: pub.state,
      }));
    });

    const handleSearch = async () => {
      // Debounce para evitar múltiples llamadas
      clearTimeout(searchTimeout);
      searchTimeout = setTimeout(async () => {
        if (searchQuery.value.trim()) {
          await store.searchPublications(searchQuery.value);
        } else {
          await store.fetchPublications();
        }
        // Cargar favoritos después de buscar
        await store.fetchCurrentPublicationsFavorites();
      }, 500);
    };

    const handleFilterChange = async (filter) => {
      currentFilter.value = filter;
      await store.setFilter(filter);
      // Cargar favoritos después de cambiar filtro
      await store.fetchCurrentPublicationsFavorites();
    };

    // Cargar datos al montar el componente
    onMounted(async () => {
      await store.fetchCategories();
      await store.fetchPublications();
      await store.fetchCurrentPublicationsFavorites(); // Cargar favoritos después de las publicaciones
    });

    return {
      store,
      searchQuery,
      currentFilter,
      mappedPublications,
      handleSearch,
      handleFilterChange,
    };
  },
};
</script>

<style scoped>
.error-message {
  background-color: #fee;
  color: #c33;
  padding: 1rem;
  border-radius: 4px;
  margin-bottom: 1rem;
}

.pagination {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 1rem;
  margin-top: 2rem;
  padding: 1rem;
}

.pagination button {
  padding: 0.5rem 1rem;
  background-color: #007bff;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

.pagination button:disabled {
  background-color: #ccc;
  cursor: not-allowed;
}

.pagination span {
  font-weight: 500;
}
</style>