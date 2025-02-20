<template>
  <nav class="navbar">
    <div class="nav-links">
      <router-link to="/home" class="nav-item">Home</router-link>
      <router-link to="/" class="nav-item">Login</router-link>
      <router-link to="/dashboard" class="nav-item">Dashboard</router-link>
      <router-link to="/profile" class="nav-item">Profilo</router-link> <!-- Nuovo link -->
    </div>
  </nav>
</template>

<style scoped>
.navbar {
  position: fixed;
  left: 0;
  top: 0;
  width: 200px; /* Larghezza sidebar */
  height: 100vh;
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
</style>
