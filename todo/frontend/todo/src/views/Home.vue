<script setup>
import { ref, onMounted } from 'vue';
import axios from 'axios';

const todos = ref([]);

const loadTodos = async () => {
  try {
    const response = await axios.get("http://localhost:8080/api/todos", { withCredentials: true });
    todos.value = response.data;
  } catch (error) {
    console.error("Errore nel recuperare i Todo:", error);
  }
};

onMounted(() => {
  loadTodos();
});
</script>

<template>
  <div>
    <h1>Home Page - Lista dei Todo</h1>
    <ul>
      <li v-for="todo in todos" :key="todo.id">
        {{ todo.title }} - Completato: {{ todo.completed ? 'Sì' : 'No' }}
      </li>
    </ul>
    <p v-if="todos.length === 0">Nessun Todo disponibile.</p>
  </div>
</template>

<style scoped>
ul {
  list-style: none;
  padding: 0;
}
li {
  padding: 5px;
  border-bottom: 1px solid #ddd;
}
</style>
