<template>
  <div class="login-page">
    <h1>Login</h1>
    <div v-if="loading">
      <p>Verifica dello stato di autenticazione in corso...</p>
    </div>
    <div v-else>
      <div v-if="!user">
        <button class="btn" @click="handleLogin">Login con Google</button>
      </div>
      <div v-else>
        <p>Sei loggato come: <strong>{{ user.email }}</strong></p>
        <button class="btn" @click="handleLogout">Logout</button>
      </div>
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
      loading: true,
    };
  },
  async created() {
    try {
      this.user = await getUser();
    } catch (error) {
      console.error("Errore nel recupero dell'utente:", error);
      this.user = null;
    } finally {
      this.loading = false;
    }
  },
  methods: {
    handleLogin() {
      loginWithGoogle();
    },
    async handleLogout() {
      try {
        await fetch("http://localhost:8080/logout", {
          method: "POST",
          credentials: "include"
        });
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
  width: 50vw;
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  justify-content: center; /* Centra verticalmente */
  align-items: center;     /* Centra orizzontalmente */
  padding: 20px;
  box-sizing: border-box;
}

h1 {
  font-size: 2.5em;
  margin-bottom: 20px;
}

p {
  font-size: 1.2em;
  text-align: center;
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
