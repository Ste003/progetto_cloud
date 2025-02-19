<template>
  <div id="app">
    <h1>Benvenuto</h1>
    <div v-if="!user">
      <a href="http://localhost:8080/oauth2/authorization/google">Login with Google</a>
    </div>
    <div v-else>
      <p>{{ user }}</p>
    </div>
  </div>
</template>

<script>
import axios from 'axios';

export default {
  data() {
    return {
      user: null
    };
  },
  mounted() {
    // Prova a recuperare le informazioni dell'utente dal backend
    axios.get('http://localhost:8080/user')
      .then(response => {
        this.user = response.data;
      })
      .catch(error => {
        console.error("Errore nel recuperare l'utente:", error);
      });
  }
};
</script>
