<template>
  <div class="home">
    <h1>Home Page - Lista di tutte le Todo</h1>
    <ul>
      <li v-for="todo in todos" :key="todo.id">
        {{ todo.title }} - Completato: {{ todo.completed ? 'Sì' : 'No' }}
        <!-- Visualizza il pulsante solo se l'utente corrente non è il creatore -->
        <button 
          v-if="todo.creatorEmail !== currentUserEmail && !todo.completed" 
          @click="subscribe(todo.id)"
        >
          Iscriviti
        </button>
      </li>
    </ul>
    <p v-if="todos.length === 0">Nessuna Todo disponibile.</p>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import axios from 'axios';

const todos = ref([]);

// Recupera l'email dell'utente corrente dal localStorage
const currentUserEmail = localStorage.getItem("userEmail") || "";

const loadTodos = async () => {
  try {
    const response = await axios.get("http://localhost:8080/api/todos", { withCredentials: true });
    todos.value = response.data;
  } catch (error) {
    console.error("Errore nel recupero dei Todo:", error);
  }
};

const subscribe = async (todoId) => {
  try {
    const response = await axios.post(`http://localhost:8080/profile/todos/${todoId}/subscribe`, {}, { withCredentials: true });
    console.log(response.data);
    // Ricarica i Todo dopo l'iscrizione
    await loadTodos();
  } catch (error) {
    console.error("Errore nell'iscrizione alla Todo:", error);
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
button {
  margin-left: 10px;
}
</style>
