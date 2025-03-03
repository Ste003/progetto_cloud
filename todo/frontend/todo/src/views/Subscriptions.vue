<template>
  <div class="subscriptions-page">
    <h1 class="title">Iscrizioni alle Todo</h1>

    <div class="subscriptions-container">
      <!-- Colonna sinistra: To-Do create dall'utente -->
      <section class="column">
        <h2>Le tue Todo con iscritti</h2>
        <div v-for="todo in createdTodos.filter(todo => !todo.completed)" :key="todo.id" class="todo-card">
          <h3>{{ todo.title }} <span v-if="todo.completed">(Completata ✅)</span></h3>
          <p><strong>Iscritti:</strong></p>
          <ul>
            <li v-for="sub in todo.subscribers" :key="sub.id">
              {{ sub.name }}
            </li>
          </ul>
        </div>
        <p v-if="createdTodos.filter(todo => !todo.completed).length === 0" class="empty-msg">
          Non hai creato alcuna todo.
        </p>
      </section>

      <!-- Colonna destra: To-Do a cui l'utente è iscritto -->
      <section class="column">
        <h2>Todo a cui sei iscritto con altri iscritti</h2>
        <div v-for="todo in subscribedTodos.filter(todo => !todo.completed)" :key="todo.id" class="todo-card">
          <h3>{{ todo.title }} <span v-if="todo.completed">(Completata ✅)</span></h3>
          <p><strong>Altri iscritti:</strong></p>
          <ul>
            <li v-for="sub in todo.subscribers" :key="sub.id">
              {{ sub.name }}
            </li>
          </ul>
        </div>
        <p v-if="subscribedTodos.filter(todo => !todo.completed).length === 0" class="empty-msg">
          Non sei iscritto a nessuna todo.
        </p>
      </section>
    </div>

    <!-- Sezione per le Todo completate -->
    <div v-if="showCompletedTodos" class="completed-todos-container">
      <!-- Le Todo completate create dall'utente -->
      <section class="column">
        <h2>Le tue Todo completate</h2>
        <div v-for="todo in createdTodos.filter(todo => todo.completed)" :key="todo.id" class="todo-card">
          <h3>{{ todo.title }} <span>(Completata ✅)</span></h3>
          <p><strong>Iscritti:</strong></p>
          <ul>
            <li v-for="sub in todo.subscribers" :key="sub.id">
              {{ sub.name }}
            </li>
          </ul>
        </div>
        <p v-if="createdTodos.filter(todo => todo.completed).length === 0" class="empty-msg">
          Non hai completato alcuna todo.
        </p>
      </section>

      <!-- Le Todo completate a cui l'utente è iscritto -->
      <section class="column">
        <h2>Todo completate a cui sei iscritto</h2>
        <div v-for="todo in subscribedTodos.filter(todo => todo.completed)" :key="todo.id" class="todo-card">
          <h3>{{ todo.title }} <span>(Completata ✅)</span></h3>
          <p><strong>Altri iscritti:</strong></p>
          <ul>
            <li v-for="sub in todo.subscribers" :key="sub.id">
              {{ sub.name }}
            </li>
          </ul>
        </div>
        <p v-if="subscribedTodos.filter(todo => todo.completed).length === 0" class="empty-msg">
          Non sei iscritto a nessuna todo completata.
        </p>
      </section>
    </div>

    <!-- Pulsante per mostrare/nascondere le todo completate -->
    <div class="toggle-completed-btn-container">
      <button @click="toggleCompletedTodos" class="action-btn">
        {{ showCompletedTodos ? 'Nascondi' : 'Mostra' }} Todo Completate
      </button>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import axios from 'axios';

const createdTodos = ref([]);
const subscribedTodos = ref([]);
const showCompletedTodos = ref(false);

const loadCreatedTodos = async () => {
  try {
    const response = await axios.get("http://localhost:8080/api/subscriptions/created-todos-subscribers", { withCredentials: true });
    createdTodos.value = response.data;
  } catch (error) {
    console.error("Errore nel recupero delle tue todo con iscritti:", error);
  }
};

const loadSubscribedTodos = async () => {
  try {
    const response = await axios.get("http://localhost:8080/api/subscriptions/subscribed-todos-subscribers", { withCredentials: true });
    subscribedTodos.value = response.data;
  } catch (error) {
    console.error("Errore nel recupero delle todo a cui sei iscritto con iscritti:", error);
  }
};

const toggleCompletedTodos = () => {
  showCompletedTodos.value = !showCompletedTodos.value;
};

onMounted(() => {
  loadCreatedTodos();
  loadSubscribedTodos();
});
</script>

<style>
/* Variabili per il tema chiaro (default) */
:root {
  --background-color: #ffffff;
  --text-color: #333333;
  --card-bg: #f8f9fa;
  --border-color: #ddd;
  --action-btn-bg: #007bff;
  --action-btn-hover-bg: #0056b3;
}

/* Variabili per il tema scuro */
.dark-theme {
  --background-color: #1e1e1e;
  --text-color: #f5f5f5;
  --card-bg: #333333;
  --border-color: #555;
  --action-btn-bg: #0d6efd;
  --action-btn-hover-bg: #0b5ed7;
}

.subscriptions-page {
  width: 75vw;
  padding: 20px;
  box-sizing: border-box;
  background-color: var(--background-color);
  color: var(--text-color);
}

.title {
  text-align: center;
  font-size: 2.5em;
  margin-bottom: 30px;
}

.subscriptions-container {
  display: flex;
  gap: 20px;
  justify-content: space-between;
}

.column {
  flex: 1;
  background: var(--card-bg);
  padding: 15px;
  border-radius: 10px;
  box-shadow: 2px 2px 10px var(--border-color);
  min-height: 300px;
}

.todo-card {
  border: 1px solid var(--border-color);
  padding: 15px;
  border-radius: 8px;
  margin-bottom: 15px;
  background-color: var(--card-bg);
  box-shadow: 0 2px 5px var(--border-color);
}

.todo-card h3 {
  margin: 0 0 10px;
  font-size: 1.2em;
}

.todo-card ul {
  list-style: none;
  padding: 0;
}

.todo-card li {
  font-size: 1em;
  margin-bottom: 5px;
  color: var(--text-color);
}

.empty-msg {
  text-align: center;
  color: var(--text-color);
  font-style: italic;
  margin-top: 20px;
}

.toggle-completed-btn-container {
  text-align: center;
  margin-top: 20px;
}

.action-btn {
  padding: 10px 20px;
  background-color: var(--action-btn-bg);
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 1.2em;
}

.action-btn:hover {
  background-color: var(--action-btn-hover-bg);
}

/* Responsive */
@media (max-width: 768px) {
  .subscriptions-container {
    flex-direction: column;
  }
}
</style>
