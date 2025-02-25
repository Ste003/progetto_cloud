<template>
  <div class="home">
    <h1>Home Page - Lista di tutte le Todo</h1>
    <ul>
      <li v-for="todo in todos" :key="todo.id">
        {{ todo.title }} - Completato: {{ todo.completed ? 'Sì' : 'No' }}
        <!-- Mostra il pulsante "Iscriviti" solo se la todo non è completata e l'utente non è già iscritto -->
        <button 
          v-if="!todo.completed && !todo.subscribed" 
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

const loadTodos = async () => {
  try {
    const response = await axios.get("http://localhost:8080/api/todos", { withCredentials: true });
    todos.value = response.data;
  } catch (error) {
    console.error("Errore nel recupero delle Todo:", error);
  }
};

const subscribe = async (todoId) => {
  try {
    await axios.post(`http://localhost:8080/profile/todos/${todoId}/subscribe`, {}, { withCredentials: true });
    await loadTodos(); // Ricarica la lista dopo l'iscrizione
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
  display: flex;
  align-items: center;
  gap: 10px;
}
button {
  padding: 5px 10px;
  cursor: pointer;
}
</style>
