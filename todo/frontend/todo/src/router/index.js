import { createRouter, createWebHistory } from 'vue-router';
import { getUser } from '../api';
import Home from '../views/Home.vue';
import Subscriptions from '../views/Subscriptions.vue';
import Profile from '../views/Profile.vue';
import Login from '../views/Login.vue';

const routes = [
  { path: '/', component: Login },
  { path: '/home', component: Home },
  { path: '/subscriptions', component: Subscriptions },
  {
    path: '/profile',
    component: Profile,
    meta: { requiresAuth: true }  // Aggiungi un flag per il controllo
  }
];

const router = createRouter({
  history: createWebHistory(),
  routes
});

router.beforeEach(async (to, from, next) => {
  if (to.matched.some(record => record.meta.requiresAuth)) {
    const user = await getUser();  // Chiamata al backend per recuperare l'utente
    if (!user) {
      next({ path: '/' });  // Se l'utente non è autenticato, reindirizza al login
    } else {
      next();  // Se l'utente è autenticato, lascia passare
    }
  } else {
    next();  // Se la rotta non richiede autenticazione, lascia passare
  }
});

export default router;
