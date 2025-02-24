<template>
  <div class="login-page">
    <h1>Login</h1>
    <div v-if="!user">
      <!-- Se l'utente NON è autenticato, mostra il pulsante di login -->
      <button class="btn" @click="handleLogin">Login con Google</button>
    </div>
    <div v-else>
      <!-- Se l'utente è già autenticato, mostra il pulsante di logout -->
      <p>Sei loggato come: <strong>{{ user.email }}</strong></p>
      <button class="btn" @click="handleLogout">Logout</button>
    </div>
  </div>
</template>

<script>
import { loginWithGoogle, getUser } from '../api';

export default {
  name: "Login",
  data() {
    return {
      user: null,
      loading: true,  // Variabile per gestire lo stato di caricamento
    };
  },
  async created() {
    try {
      // Verifica se l'utente è già autenticato
      this.user = await getUser();
    } catch (error) {
      console.error("Errore nel recupero dell'utente:", error);
      this.user = null;  // In caso di errore, non ci sarà un utente autenticato
    } finally {
      this.loading = false;  // Fine del caricamento
    }
  },
  methods: {
    handleLogin() {
      loginWithGoogle(); // Chiamata al login Google
    },
    async handleLogout() {
      try {
        await fetch("http://localhost:8080/logout", {
          method: "POST",
          credentials: "include"
        });

        // 🔥 Rimuovi l'utente dal frontend e reindirizza manualmente
        this.user = null;
        window.location.href = "/";
      } catch (error) {
        console.error("Errore durante il logout:", error);
      }
    }
  }
};
</script>


<style scoped>
.login-page {
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-top: 100px;
}

.btn {
  background-color: #4285f4;
  color: white;
  border: none;
  padding: 10px 20px;
  margin: 10px;
  font-size: 16px;
  border-radius: 5px;
  cursor: pointer;
}

.btn:hover {
  background-color: #357ae8;
}
</style>
