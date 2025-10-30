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
    path: '/my-library',
    name: 'MyLibrary',
    component: MyLibrary
  },
  {
    path: '/settings',
    name: 'Settings',
    component: Settings
  },
  {
    path: '/announcements',
    name: 'Announcements',
    component: Announcements
  },
  {
    path: '/announcements/:id',
    name: 'AnnouncementDetail',
    component: AnnouncementDetail
  },
  {
    path: '/moderation/announcements/:id',
    name: 'ModerationAnnouncementDetail',
    component: AnnouncementDetail,
    meta: { fromModeration: true }
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