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

            <!-- Mostra "Già iscritto" se l'utente è già iscritto -->
            <span v-if="todo.subscribed" class="subscribed-label">Già iscritto</span>

            <!-- Mostra il pulsante solo se la todo non è completata e l'utente non è già iscritto -->
            <button v-if="!todo.completed && !todo.subscribed" @click="subscribe(todo.id)" class="subscribe-btn">
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
    <!-- Sezione per aggiungere una nuova Todo -->
    <div class="add-todo">
      <h2>Aggiungi una nuova Todo</h2>
      <input type="text" v-model="newTodoTitle" placeholder="Inserisci il titolo" class="input-field" />
      <button @click="addTodo" class="add-btn">Aggiungi</button>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import axios from 'axios';

const todos = ref([]);
const currentUserEmail = localStorage.getItem("userEmail") || "";
const newTodoTitle = ref("");

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


const addTodo = async () => {
  if (!newTodoTitle.value.trim()) return;
  try {
    const payload = { title: newTodoTitle.value };
    await axios.post("http://localhost:8080/api/todos/create", payload, {
      withCredentials: true,
      headers: { "Content-Type": "application/json" }
    });
    newTodoTitle.value = "";
    await loadTodos();
  } catch (error) {
    console.error("Errore nell'aggiunta della todo:", error);
  }
};

const incompleteTodos = computed(() => todos.value.filter(todo => !todo.completed));
const completedTodos = computed(() => todos.value.filter(todo => todo.completed));

onMounted(loadTodos);
</script>

<style scoped>
/* Rimuovi restrizioni fisse e usa tutta la larghezza del viewport */
.home {
  width: 80vw;
  padding: 20px;
  box-sizing: border-box;
}

.title {
  text-align: center;
  font-size: 2.5em;
  margin-bottom: 30px;
}

/* Distribuisci le colonne affiancate, senza limitazioni di larghezza */
.columns {
  display: flex;
  flex-direction: row;
  flex-wrap: nowrap;
  /* Non permettere il wrapping: rimangono affiancate */
  justify-content: space-between;
  gap: 20px;
  width: 100%;
}

.column {
  flex: 1;
  min-width: 45%;
  /* Ogni colonna occuperà almeno il 45% dello spazio */
  padding: 20px;
  border: 1px solid #ddd;
  border-radius: 8px;
  box-shadow: 0 2px 5px rgba(0, 0, 0, 0.1);
  box-sizing: border-box;
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

.subscribed-label {
  color: #28a745;
  font-weight: bold;
  margin-left: 10px;
}

.empty-msg {
  text-align: center;
  color: #777;
  font-style: italic;
}

.add-todo {
  margin-top: 40px;
  text-align: center;
  width: 100%;
}

.input-field {
  padding: 10px;
  width: 60%;
  font-size: 1em;
  border: 1px solid #ccc;
  border-radius: 5px;
  margin-right: 10px;
  box-sizing: border-box;
}

.add-btn {
  padding: 10px 15px;
  font-size: 1em;
  background-color: #28a745;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

.add-btn:hover {
  background-color: #218838;
}
</style>
