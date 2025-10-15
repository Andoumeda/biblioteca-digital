<template>
  <div v-if="showLayout">
    <Topbar />

    <div class="layout">
      <Sidebar />

      <div class="main">
        <router-view />
      </div>
    </div>
  </div>

  <div v-else>
    <router-view />
  </div>
</template>

<script>
import Sidebar from "./components/Sidebar.vue";
import Topbar from "./components/Topbar.vue";
import { computed } from 'vue';
import { useRoute } from 'vue-router';

export default {
  name: "App",
  components: { Sidebar, Topbar },
  setup() {
    const route = useRoute();

    // Ocultar layout en páginas de autenticación
    const showLayout = computed(() => {
      return route.name !== 'Login' && route.name !== 'NotFound';
    });

    return {
      showLayout
    };
  }
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