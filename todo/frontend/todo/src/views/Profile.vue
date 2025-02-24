<template>
  <div class="profile">
    <h1>Profilo Utente</h1>
    <div v-if="user">
      <p><strong>Nome:</strong> {{ user.name }}</p>
      <p><strong>Email:</strong> {{ user.email }}</p>
    </div>
    <div v-else>
      <p>Caricamento dati utente...</p>
    </div>

    <div class="todos-section">
      <h2>Todo create da te</h2>
      <ul>
        <li v-for="todo in createdTodos" :key="todo.id">
          {{ todo.title }} - 
          <span v-if="todo.completed">Completata</span>
          <button v-if="!todo.completed" @click="completeTodo(todo.id)">Chiudi</button>
        </li>
      </ul>
      <p v-if="createdTodos.length === 0">Non hai creato nessuna todo.</p>
    </div>

    <div class="todos-section">
      <h2>Todo a cui ti sei iscritto</h2>
      <ul>
        <li v-for="todo in subscribedTodos" :key="todo.id">
          {{ todo.title }} - Completato: {{ todo.completed ? 'Sì' : 'No' }}
        </li>
      </ul>
      <p v-if="subscribedTodos.length === 0">Non sei iscritto a nessuna todo.</p>
    </div>
  </div>
</template>

<script>
import axios from 'axios';
import { getUser } from '../api';

export default {
  name: "Profile",
  data() {
    return {
      user: null,
      createdTodos: [],
      subscribedTodos: []
    };
  },
  async created() {
    this.user = await getUser();
    if (this.user) {
      this.fetchCreatedTodos();
      this.fetchSubscribedTodos();
    }
  },
  methods: {
    async fetchCreatedTodos() {
      try {
        const response = await axios.get("http://localhost:8080/profile/created-todos", { withCredentials: true });
        this.createdTodos = response.data;
      } catch (error) {
        console.error("Errore nel recupero delle todo create:", error);
      }
    },
    async fetchSubscribedTodos() {
      try {
        const response = await axios.get("http://localhost:8080/profile/subscribed-todos", { withCredentials: true });
        this.subscribedTodos = response.data;
      } catch (error) {
        console.error("Errore nel recupero delle todo sottoscritte:", error);
      }
    },
    async completeTodo(todoId) {
      try {
        await axios.put(`http://localhost:8080/profile/todos/${todoId}/complete`, {}, { withCredentials: true });
        this.fetchCreatedTodos();
      } catch (error) {
        console.error("Errore nel completamento della todo:", error);
      }
    }
  }
};
</script>

<style scoped>
.profile {
  padding: 20px;
}
.todos-section {
  margin-top: 20px;
}
.todos-section ul {
  list-style: none;
  padding: 0;
}
.todos-section li {
  padding: 5px 0;
  border-bottom: 1px solid #ccc;
}
</style>
