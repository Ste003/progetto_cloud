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
      <h2>Todo proprietarie dell'utente (completate)</h2>
      <ul>
        <li v-for="todo in completedTodos" :key="todo.id">
          {{ todo.title }} - 
          <span v-if="todo.completed">Completata</span>
          <button v-if="!todo.completed" @click="completeTodo(todo.id)">Chiudi</button>
        </li>
      </ul>
      <p v-if="completedTodos.length === 0">Nessuna todo completata.</p>
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
      completedTodos: []  // Todo completate create dall'utente
    };
  },
  async created() {
    this.user = await getUser();
    if (this.user) {
      this.fetchCompletedTodos();
    }
  },
  methods: {
    async fetchCompletedTodos() {
      try {
        const response = await axios.get("http://localhost:8080/profile/completed-todos", { withCredentials: true });
        this.completedTodos = response.data;
      } catch (error) {
        console.error("Errore nel recupero delle todo completate:", error);
      }
    },
    async completeTodo(todoId) {
      try {
        await axios.put(`http://localhost:8080/profile/todos/${todoId}/complete`, {}, { withCredentials: true });
        this.fetchCompletedTodos();  // Ricarica la lista per aggiornare lo stato
      } catch (error) {
        console.error("Errore nel completamento della todo:", error);
      }
    }
  }
};
</script>

<style scoped>
.profile {
  margin-left: 220px;
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
