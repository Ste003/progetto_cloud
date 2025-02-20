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
    this.user = await getUser();
  },
};
</script>

<style scoped>
.navbar {
  position: fixed;
  left: 0;
  top: 0;
  width: 200px; /* Larghezza sidebar */
  height: 100vh; /* Occupa tutta l'altezza della pagina */
  background: #333;
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 20px;
}

.nav-links {
  display: flex;
  flex-direction: column;
  width: 100%;
}

.nav-item {
  color: white;
  text-decoration: none;
  padding: 10px;
  text-align: center;
  width: 100%;
}

.nav-item:hover {
  background: #555;
  border-radius: 5px;
}

.user-info {
  margin-top: auto;
  color: white;
  font-weight: bold;
}
</style>
