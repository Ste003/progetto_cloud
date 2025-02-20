<template>
    <div class="profile">
      <h1>Profilo Utente</h1>
      <p v-if="user"><strong>Nome:</strong> {{ user.name }}</p>
      <p v-if="user"><strong>Email:</strong> {{ user.email }}</p>
      <p v-else>Caricamento dati utente...</p>
    </div>
  </template>
  
  <script>
  import { getUser } from '../api';  // Importa la funzione per ottenere l'utente
  
  export default {
    name: "Profile",
    data() {
      return {
        user: null
      };
    },
    async created() {
      // Recupera i dati utente quando la pagina viene caricata
      this.user = await getUser();
    }
  };
  </script>
  
  <style scoped>
  .profile {
    margin-left: 220px; /* Spazio per la navbar */
    padding: 20px;
  }
  </style>
  