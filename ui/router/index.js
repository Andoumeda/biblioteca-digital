import { createRouter, createWebHistory } from 'vue-router';
import Home from '../src/views/Home.vue';
import NotFound from '../src/views/NotFound.vue';
import Login from '../src/views/Login.vue';
import Explore from '../src/views/Explore.vue';
import Moderation from '../src/views/Moderation.vue';
import MyLibrary from '../src/views/MyLibrary.vue';
import Settings from '../src/views/Settings.vue';
import Announcements from '../src/views/Announcements.vue';
import AnnouncementDetail from '../src/views/AnnouncementDetail.vue';

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: Login,
    meta: { requiresAuth: false }
  },
  {
    path: '/',
    name: 'Home',
    component: Home,
    meta: { requiresAuth: true }
  },
  {
    path: '/explore',
    name: 'Explore',
    component: Explore,
    meta: { requiresAuth: true }
  },
  {
    path: '/moderation',
    name: 'Moderation',
    component: Moderation,
    meta: { requiresAuth: true, requiresAdmin: true }
  },
  {
    path: '/my-library',
    name: 'MyLibrary',
    component: MyLibrary,
    meta: { requiresAuth: true }
  },
  {
    path: '/settings',
    name: 'Settings',
    component: Settings,
    meta: { requiresAuth: true }
  },
  {
    path: '/announcements',
    name: 'Announcements',
    component: Announcements,
    meta: { requiresAuth: true }
  },
  {
    path: '/announcements/:id',
    name: 'AnnouncementDetail',
    component: AnnouncementDetail,
    meta: { requiresAuth: true }
  },
  {
    path: '/moderation/announcements/:id',
    name: 'ModerationAnnouncementDetail',
    component: AnnouncementDetail,
    meta: { fromModeration: true, requiresAuth: true, requiresAdmin: true }
  },
  {
    path: '/:pathMatch(.*)*',
    name: 'NotFound',
    component: NotFound
  }
];

const router = createRouter({
  history: createWebHistory(),
  routes
});

// Navigation guards
router.beforeEach((to, from, next) => {
  // Importar dinámicamente para evitar dependencias circulares
  const token = localStorage.getItem('authToken');
  const userStr = localStorage.getItem('currentUser');

  let user = null;
  try {
    user = userStr ? JSON.parse(userStr) : null;
  } catch (e) {
    console.error('Error parsing user from localStorage', e);
  }

  const isAuthenticated = !!token;
  const isAdmin = user?.role === 'ADMIN' || false;

  // Si la ruta requiere autenticación
  if (to.meta.requiresAuth && !isAuthenticated) {
    console.log('Ruta protegida, redirigiendo al login');
    next('/login');
    return;
  }

  // Si la ruta requiere ser admin
  if (to.meta.requiresAdmin && !isAdmin) {
    console.log('Ruta solo para admin, redirigiendo a home');
    next('/');
    return;
  }

  // Si está autenticado y va al login, redirigir a home
  if (to.path === '/login' && isAuthenticated) {
    next('/');
    return;
  }

  next();
});

export default router;