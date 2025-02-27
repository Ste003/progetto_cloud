<template>
  <div class="profile">
    <h1>Profilo Utente</h1>
    <div v-if="user" class="user-info">
      <p><strong>Nome:</strong> {{ user.name }}</p>
      <p><strong>Email:</strong> {{ user.email }}</p>
    </div>
    <div v-else>
      <p>Caricamento dati utente...</p>
    </div>

    <!-- Sezione per utenti normali -->
    <div v-if="!isAdmin" class="todos-container">
      <div class="todos-section">
        <h2>Todo create da te</h2>
        <ul>
          <li v-for="todo in createdTodos" :key="todo.id">
            <span class="todo-title">{{ todo.title }}</span> -
            <span class="todo-status">{{ todo.completed ? 'Completata' : 'In sospeso' }}</span>
            <button v-if="!todo.completed" @click="completeTodo(todo.id)" class="action-btn">
              Chiudi
            </button>
          </li>
        </ul>
        <p v-if="createdTodos.length === 0" class="empty-msg">Non hai creato nessuna todo.</p>
      </div>
      <div class="todos-section">
        <h2>Todo a cui sei iscritto</h2>
        <ul>
          <li v-for="todo in subscribedTodos" :key="todo.id">
            <span class="todo-title">{{ todo.title }}</span> -
            <span class="todo-status">Completato: {{ todo.completed ? 'Sì' : 'No' }}</span>
          </li>
        </ul>
        <p v-if="subscribedTodos.length === 0" class="empty-msg">Non sei iscritto a nessuna todo.</p>
      </div>
    </div>

    <!-- Sezione per admin -->
    <div v-if="isAdmin" class="todos-container">
      <div class="todos-section">
        <h2>Todo Incompleti</h2>
        <ul>
          <li v-for="todo in incompleteTodos" :key="todo.id">
            <span class="todo-title">{{ todo.title }}</span> -
            <span class="todo-status">In sospeso</span>
            <button @click="completeTodo(todo.id)" class="action-btn">
              Chiudi
            </button>
          </li>
        </ul>
        <p v-if="incompleteTodos.length === 0" class="empty-msg">Tutte le todo sono state completate.</p>
      </div>

      <div class="todos-section">
        <h2>Todo Completate</h2>
        <ul>
          <li v-for="todo in completedTodos" :key="todo.id">
            <span class="todo-title">{{ todo.title }}</span> -
            <span class="todo-status">Completata</span>
          </li>
        </ul>
        <p v-if="completedTodos.length === 0" class="empty-msg">Non ci sono todo completate.</p>
      </div>
    </div>
  </div>
</template>

<script>
import axios from 'axios';

export default {
  name: "Profile",
  data() {
    return {
      user: null,
      createdTodos: [],
      subscribedTodos: [],
      allTodos: [],
      incompleteTodos: [],  // Todo non completate
      completedTodos: [],  // Todo completate
      isAdmin: false,
      adminEmail: ''  // Variabile per l'email dell'admin
    };
  },
  async created() {
    try {
      // Prima recupera l'email dell'admin
      const adminEmailResponse = await axios.get("http://localhost:8080/profile/admin-email", { withCredentials: true });
      this.adminEmail = adminEmailResponse.data;

      // Poi recupera i dati dell'utente
      const response = await axios.get("http://localhost:8080/profile", { withCredentials: true });
      this.user = response.data;

      if (this.user) {
        // Verifica se l'utente è admin basandosi sull'email
        this.isAdmin = this.user.email === this.adminEmail;
        if (this.isAdmin) {
          await this.fetchAllTodos();
          this.separateTodos();
        } else {
          await this.fetchCreatedTodos();
          await this.fetchSubscribedTodos();
        }
      }
    } catch (error) {
      console.error("Errore nel recupero del profilo utente:", error);
    }
  },
  methods: {
    async fetchAllTodos() {
      try {
        const response = await axios.get("http://localhost:8080/profile/todos", { withCredentials: true });
        this.allTodos = response.data;
        this.separateTodos(); // Separare le todo completate e non
      } catch (error) {
        console.error("Errore nel recupero di tutte le todo:", error);
      }
    },
    separateTodos() {
      this.incompleteTodos = this.allTodos.filter(todo => !todo.completed);
      this.completedTodos = this.allTodos.filter(todo => todo.completed);
    },
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
        if (this.isAdmin) {
          await this.fetchAllTodos();
          this.separateTodos();
        } else {
          await this.fetchCreatedTodos();
        }
      } catch (error) {
        console.error("Errore nel completamento della todo:", error);
      }
    },
    async completeAllTodos() {
      try {
        await axios.patch("http://localhost:8080/profile/todos/complete", {}, { withCredentials: true });
        await this.fetchAllTodos();
        this.separateTodos();
      } catch (error) {
        console.error("Errore nel chiudere tutte le todo:", error);
      }
    }
  }
};
</script>

<style scoped>
.profile {
  width: 70vw;
  padding: 20px;
  box-sizing: border-box;
}

.user-info {
  text-align: center;
  margin-bottom: 30px;
}

h1 {
  text-align: center;
  font-size: 2.5em;
  margin-bottom: 20px;
}

.todos-container {
  display: grid;
  grid-template-columns: 1fr 1fr;
  /* Due colonne per Admin */
  gap: 40px;
  width: 100%;
}

.todos-section {
  padding: 20px;
  border: 1px solid #ddd;
  border-radius: 8px;
  box-shadow: 0 2px 5px rgba(0, 0, 0, 0.1);
  box-sizing: border-box;
  display: flex;
  flex-direction: column;
}

.todos-section h2 {
  text-align: center;
  font-size: 1.8em;
  margin-bottom: 20px;
}

ul {
  list-style: none;
  padding: 0;
}

li {
  padding: 10px;
  margin-bottom: 10px;
  border-bottom: 1px solid #ccc;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.todo-title {
  flex: 1;
  font-size: 1.2em;
}

.todo-status {
  margin-left: 10px;
}

.action-btn {
  padding: 5px 10px;
  background-color: #28a745;
  color: #fff;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  margin-left: 10px;
}

.action-btn:hover {
  background-color: #218838;
}

.empty-msg {
  text-align: center;
  color: #777;
  font-style: italic;
  margin-top: 20px;
}

.close-all-container {
  text-align: center;
  margin-bottom: 20px;
}
</style>
