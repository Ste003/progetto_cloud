<template>
  <div class="home">
    <h1>Home Page - Lista dei Todo (non completate)</h1>
    <ul>
      <li v-for="todo in todos" :key="todo.id">
        {{ todo.title }} - Completato: {{ todo.completed ? 'Sì' : 'No' }}
      </li>
    </ul>
    <p v-if="todos.length === 0">Nessun Todo non completata disponibile.</p>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import axios from 'axios';

const todos = ref([]);

const loadTodos = async () => {
  try {
    const response = await axios.get("http://localhost:8080/api/todos/incomplete", { withCredentials: true });
    todos.value = response.data;
  } catch (error) {
    console.error("Errore nel recupero dei Todo:", error);
  }
};

onMounted(loadTodos);
</script>

<style scoped>
.home {
  padding: 20px;
}
ul {
  list-style: none;
  padding: 0;
}
li {
  padding: 5px;
  border-bottom: 1px solid #ddd;
}
</style>
