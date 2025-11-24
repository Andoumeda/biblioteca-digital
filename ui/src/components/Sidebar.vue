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
      <router-link v-if="authStore.isAdmin" to="/moderation" class="nav-link">
        <svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
          <path d="M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10"/>
        </svg>
        Moderación
      </router-link>
    </nav>
  </aside>
</template>

<script setup>
import { useAuthStore } from '../stores/authStore';

const authStore = useAuthStore();
</script>

<style scoped>
.sidebar {
  background: white;
  border-right: 1px solid #e2e8f0;
  height: 100%;
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

.nav-link.router-link-active {
  background-color: #edf2f7;
  color: #667eea;
  font-weight: 500;
  border-left-color: #667eea;
}

.nav-link svg {
  flex-shrink: 0;
}
</style>