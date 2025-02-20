<template>
  <nav class="navbar">
    <div class="nav-links">
      <router-link to="/" class="nav-item">Login</router-link>
      <router-link to="/home" class="nav-item">Home</router-link>
      <router-link to="/dashboard" class="nav-item">Dashboard</router-link>
    </div>
    <div class="user-info" v-if="user">
      <span>{{ user }}</span>
      <!-- Se vuoi aggiungere anche un pulsante per il logout:
      <a href="http://localhost:8080/logout" class="nav-item logout">Logout</a>
      -->
    </div>
  </nav>
</template>

<script>
import { getUser } from '../api';

export default {
  name: "Navbar",
  data() {
    return {
      user: null,
    };
  },
  async created() {
    // Chiamata per recuperare le informazioni dell'utente autenticato
    this.user = await getUser();
  },
};
</script>

<style scoped>
.navbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: #333;
  padding: 10px;
}

.nav-links {
  display: flex;
}

.nav-item {
  color: white;
  text-decoration: none;
  margin: 0 15px;
  font-weight: bold;
  padding: 5px 10px;
}

.nav-item:hover {
  background: #555;
  border-radius: 5px;
}

.user-info {
  color: white;
  font-weight: bold;
}
</style>
