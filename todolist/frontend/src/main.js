import { createApp } from 'vue';
import App from './App.vue';
import router from './router';  // Import del router

createApp(App)
  .use(router)  // Usa il router
  .mount('#app');
