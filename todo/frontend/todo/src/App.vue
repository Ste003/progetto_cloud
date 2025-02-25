<template>
  <div id="app">
    <Navbar />
    <div class="main-content">
      <router-view />
    </div>
  </div>
</template>

<script>
import Navbar from './components/Navbar.vue';

export default {
  components: {
    Navbar
  }
};
</script>

<style>
/* La navbar è fissata con larghezza 200px, quindi il contenuto parte a destra */
.main-content {
  margin-left: 220px; /* spazio per la navbar */
  padding: 20px;
  max-width: 1200px;  /* Larghezza massima per il contenuto */
  margin-right: auto;
  margin-top: 20px;
}
</style>
