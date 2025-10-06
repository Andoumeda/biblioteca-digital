import { createRouter, createWebHistory } from 'vue-router';
//import Home from '../views/Home.vue';
//import Explore from '../views/Explore.vue';

const routes = [
  {
    path: '/',
    props: { msg : 'Welcome to DigiBooks Source' },
    name: 'Home',
    component: Home
  },
  {
    path: '/explore',
    props: { msg : 'Explore the Library' },
    name: 'Explore',
    component: Explore
  }
];

const router = createRouter({
  history: createWebHistory(),
  routes
});

export default router;