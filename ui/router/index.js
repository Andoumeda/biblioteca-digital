import { createRouter, createWebHistory } from 'vue-router';
import Home from '../src/views/Home.vue';
import NotFound from '../src/views/NotFound.vue';
import Login from '../src/views/Login.vue';
import Explore from '../src/views/Explore.vue';
import Moderation from '../src/views/Moderation.vue';

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: Login
  },
  {
    path: '/',
    name: 'Home',
    component: Home
  },
  {
    path: '/explore',
    name: 'Explore',
    component: Explore
  },
  {
    path: '/moderation',
    name: 'Moderation',
    component: Moderation
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

export default router;