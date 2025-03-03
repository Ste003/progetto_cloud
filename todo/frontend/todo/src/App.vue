<template>
  <div id="app">
    <Navbar />
    <!-- Pulsante per il cambio tema -->
    <button @click="toggleTheme" class="theme-toggle-btn">
      {{ isDarkTheme ? 'Tema Chiaro' : 'Tema Scuro' }}
    </button>
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
  },
  data() {
    return {
      isDarkTheme: false,
    };
  },
  created() {
    // Legge il tema da localStorage e lo applica al caricamento dell'app
    this.isDarkTheme = localStorage.getItem('theme') === 'dark';
    this.applyTheme();
  },
  methods: {
    toggleTheme() {
      this.isDarkTheme = !this.isDarkTheme;
      localStorage.setItem('theme', this.isDarkTheme ? 'dark' : 'light');
      this.applyTheme();
    },
    applyTheme() {
      if (this.isDarkTheme) {
        document.documentElement.classList.add('dark-theme');
      } else {
        document.documentElement.classList.remove('dark-theme');
      }
    }
  }
};
</script>

<style>
/* Variabili per il tema chiaro (default) */
:root {
  --background-color: #ffffff;
  --text-color: #333333;
  /* Altre variabili, se necessario */
}

/* Variabili per il tema scuro */
.dark-theme {
  --background-color: #1e1e1e;
  --text-color: #f5f5f5;
}

/* Applica le variabili globalmente */
html, body, #app {
  margin: 0;
  padding: 0;
  width: 80%;
  height: 100%;
  background-color: var(--background-color);
  color: var(--text-color);
}

/* La navbar è fissata con larghezza 200px, quindi il contenuto parte a destra */
.main-content {
  margin-left: 220px; /* spazio per la navbar */
  padding: 20px;
  max-width: 80%;  /* Larghezza massima per il contenuto */
  margin-right: auto;
  margin-top: 20px;
}

/* Stile per il pulsante di cambio tema */
.theme-toggle-btn {
  position: absolute;
  top: 20px;
  right: 20px;
  padding: 8px 16px;
  background-color: var(--text-color);
  color: var(--background-color);
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 1em;
}

.theme-toggle-btn:hover {
  opacity: 0.8;
}
</style>
