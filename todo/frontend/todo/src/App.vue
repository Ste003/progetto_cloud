<template>
  <div id="app">
    <h1>Todo List</h1>
    <ul>
      <li v-for="todo in todos" :key="todo.id">
        {{ todo.title }} - Completato: {{ todo.completed ? "Sì" : "No" }}
      </li>
    </ul>
    <p v-if="todos.length === 0">Nessun Todo disponibile.</p>
  </div>
</template>

<script>
import axios from 'axios';

export default {
  name: 'App',
  data() {
    return {
      todos: []  // Inizialmente vuoto
    };
  },
  mounted() {
    // Assicurati che l'endpoint corrisponda a quello esposto dal backend
    axios.get('http://localhost:8080/api/todos')
      .then(response => {
        this.todos = response.data;
      })
      .catch(error => {
        console.error("Errore nel recuperare i Todo:", error);
      });
  }
};
</script>

<style>
#app {
  font-family: Arial, sans-serif;
  margin: 20px;
}
h1 {
  color: #4CAF50;
}
ul {
  list-style: none;
  padding: 0;
}
li {
  margin: 8px 0;
  padding: 10px;
  border: 1px solid #ddd;
  border-radius: 5px;
}
</style>
