<template>
  <div class="layout">
    <Sidebar />

    <div class="main">
      <Topbar />

      <div class="content">
        <h2>Explorar Libros</h2>

        <!-- Barra de búsqueda -->
        <div class="explore-header">
          <input
            type="text"
            v-model="query"
            placeholder="Buscar por título, autor o usuario..."
          />
          <div class="filters">
            <button @click="filter = 'all'" :class="{ active: filter === 'all' }">
              Todos
            </button>
            <button
              @click="filter = 'trend'"
              :class="{ active: filter === 'trend' }"
            >
              Tendencia
            </button>
          </div>
          <div class="results">{{ filteredBooks.length }} libros encontrados</div>
        </div>

        <!-- Grid de libros -->
        <BookGrid :books="filteredBooks" />
      </div>
    </div>
  </div>
</template>

<script>
import Sidebar from "./components/Sidebar.vue";
import Topbar from "./components/Topbar.vue";
import BookGrid from "./components/BookGrid.vue";

export default {
  name: "App",
  components: { Sidebar, Topbar, BookGrid },
  data() {
    return {
      query: "",
      filter: "all",
      books: [
        {
          id: 1,
          titulo: "Cocina Vegana Moderna",
          autor: "María Chef",
          descripcion: "Recetas innovadoras y deliciosas...",
          categoria: "Cocina",
          imagen: "/vegan-cooking-book-cover.png",
          rating: 4.9,
          votos: 203,
          likes: 234,
          comentarios: 67,
          usuario: "@maria_chef",
          fecha: "9/3/2024",
        },
        {
          id: 2,
          titulo: "Mindfulness y Bienestar",
          autor: "Dr. Paz Mental",
          descripcion: "Técnicas de meditación y mindfulness...",
          categoria: "Salud",
          imagen: "/mindfulness-book-cover.png",
          rating: 4.7,
          votos: 156,
          likes: 198,
          comentarios: 34,
          usuario: "@dr_paz",
          fecha: "7/3/2024",
        },
        {
          id: 3,
          titulo: "El Arte de la Programación",
          autor: "Ana Desarrolladora",
          descripcion: "Una guía completa para dominar fundamentos...",
          categoria: "Tecnología",
          imagen: "/programming-book-cover.jpg",
          rating: 4.8,
          votos: 124,
          likes: 89,
          comentarios: 23,
          usuario: "@ana_dev",
          fecha: "14/3/2024",
        },
      ],
    };
  },
  computed: {
    filteredBooks() {
      return this.books.filter((b) =>
        b.titulo.toLowerCase().includes(this.query.toLowerCase())
      );
    },
  },
};
</script>