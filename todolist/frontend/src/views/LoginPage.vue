<template>
    <div>
      <h1>Login</h1>
      <form @submit.prevent="submitLogin">
        <div>
          <label for="username">Username:</label>
          <input type="text" id="username" v-model="username" required />
        </div>
        <div>
          <label for="password">Password:</label>
          <input type="password" id="password" v-model="password" required />
        </div>
        <button type="submit">Login</button>
        <p v-if="errorMessage" style="color: red;">{{ errorMessage }}</p>
      </form>
    </div>
  </template>
  
  <script>
  import axios from "axios";
  
  export default {
    data() {
      return {
        username: "", // Nome utente inserito dall'utente
        password: "", // Password inserita dall'utente
        errorMessage: "", // Messaggio di errore per credenziali non valide
      };
    },
    methods: {
      async submitLogin() {
        try {
          const response = await axios.post("/api/auth/login", {
              username: this.username,
              password: this.password,
          });

          console.log("Login riuscito:", response.data);
  
          // Esegui il redirect alla pagina principale (es. dashboard)
          this.$router.push("/dashboard");
        } catch (error) {
          console.error("Errore di login:", error.response?.data || error.message);
          this.errorMessage = "Credenziali non valide";
        }
      },
    },
  };
  </script>
  
  <style>
  /* Aggiungi eventuale stile */
  </style>
  