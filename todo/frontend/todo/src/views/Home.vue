<template>
  <div class="home">
    <h1 class="title">Home page - Todo List</h1>
    <div class="columns">
      <!-- Colonna sinistra: Todo non completate -->
      <div class="column incomplete">
        <h2>Todo non completate</h2>
        <ul>
          <li v-for="todo in incompleteTodos" :key="todo.id">
            <span class="todo-title">{{ todo.title }}</span>
            <!-- Il pulsante "Iscriviti" viene mostrato solo se la todo non è completata e l'utente non è già iscritto -->
            <button 
              v-if="!todo.completed && !todo.subscribed" 
              @click="subscribe(todo.id)"
              class="subscribe-btn"
            >
              Iscriviti
            </button>
          </li>
        </ul>
        <p v-if="incompleteTodos.length === 0" class="empty-msg">Nessuna todo non completata.</p>
      </div>
      
      <!-- Colonna destra: Todo completate -->
      <div class="column completed">
        <h2>Todo completate</h2>
        <ul>
          <li v-for="todo in completedTodos" :key="todo.id">
            <span class="todo-title">{{ todo.title }}</span>
          </li>
        </ul>
        <p v-if="completedTodos.length === 0" class="empty-msg">Nessuna todo completata.</p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import axios from 'axios';

const todos = ref([]);

const loadTodos = async () => {
  try {
    const response = await axios.get("http://localhost:8080/api/todos", { withCredentials: true });
    todos.value = response.data;
  } catch (error) {
    console.error("Errore nel recupero delle todo:", error);
  }
};

const subscribe = async (todoId) => {
  try {
    await axios.post(`http://localhost:8080/profile/todos/${todoId}/subscribe`, {}, { withCredentials: true });
    await loadTodos();
  } catch (error) {
    console.error("Errore nell'iscrizione alla todo:", error);
  }
};

const incompleteTodos = computed(() => todos.value.filter(todo => !todo.completed));
const completedTodos = computed(() => todos.value.filter(todo => todo.completed));

onMounted(loadTodos);
</script>

<style scoped>
.dashboard {
  max-width: 90%;
  margin: 20px auto;
  padding: 20px;
  box-sizing: border-box;
}

.title {
  text-align: center;
  font-size: 2.5em;
  margin-bottom: 30px;
}

.columns {
  display: flex;
  gap: 40px;
}

.column {
  flex: 1;
  padding: 20px;
  border: 1px solid #ddd;
  border-radius: 8px;
  box-shadow: 0 2px 5px rgba(0, 0, 0, 0.1);
}

.column h2 {
  font-size: 1.8em;
  margin-bottom: 20px;
  text-align: center;
}

ul {
  list-style: none;
  padding: 0;
}

li {
  font-size: 1.2em;
  margin-bottom: 15px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.todo-title {
  flex: 1;
}

.subscribe-btn {
  padding: 10px 15px;
  font-size: 1em;
  background-color: #007BFF;
  color: #fff;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

.subscribe-btn:hover {
  background-color: #0056b3;
}

.empty-msg {
  text-align: center;
  color: #777;
  font-style: italic;
}
</style>
