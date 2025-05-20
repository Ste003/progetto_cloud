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
          <li v-for="todo in createdTodos.filter(todo => !todo.completed)" :key="todo.id">
            <span class="todo-title">{{ todo.title }}</span> -
            <span class="todo-status">In sospeso</span>
            <button @click="completeTodo(todo.id)" class="action-btn">Chiudi</button>
          </li>
        </ul>
        <p v-if="createdTodos.filter(todo => !todo.completed).length === 0" class="empty-msg">
          Non hai creato nessuna todo.
        </p>
      </div>
      <div class="todos-section">
        <h2>Todo a cui sei iscritto</h2>
        <ul>
          <li v-for="todo in subscribedTodos.filter(todo => !todo.completed)" :key="todo.id">
            <span class="todo-title">{{ todo.title }}</span> -
            <span class="todo-status">Completato: {{ todo.completed ? 'Sì' : 'No' }}</span>
            <button v-if="!todo.completed" @click="completeTodo(todo.id)" class="action-btn">Chiudi</button>
          </li>
        </ul>
        <p v-if="subscribedTodos.filter(todo => !todo.completed).length === 0" class="empty-msg">
          Non sei iscritto a nessuna todo.
        </p>
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
            <button @click="completeTodo(todo.id)" class="action-btn">Chiudi</button>
          </li>
        </ul>
        <p v-if="incompleteTodos.length === 0" class="empty-msg">
          Tutte le todo sono state completate.
        </p>
      </div>

      <div class="todos-section completed-todos">
        <h2>Todo Completate</h2>
        <ul>
          <li v-for="todo in completedTodos" :key="todo.id">
            <span class="todo-title">{{ todo.title }}</span> -
            <span class="todo-status">
              {{ todo.completedByName ? 'Completata da: ' + todo.completedByName : 'Completata' }}
            </span>
          </li>
        </ul>
        <p v-if="completedTodos.length === 0" class="empty-msg">
          Non ci sono todo completate.
        </p>
      </div>
    </div>

    <!-- Sezione per mostrare/nascondere le todo completate -->
    <div class="todos-section toggle-section">
      <h2 @click="toggleCompletedTodosVisibility" class="toggle-btn">
        <span>{{ showCompletedTodos ? 'Nascondi' : 'Mostra' }} Todo Completate</span>
      </h2>
      <div v-if="showCompletedTodos">
        <div v-if="!isAdmin">
          <div class="todos-section completed-todos">
            <h3>Todo Completate da te</h3>
            <ul>
              <li v-for="todo in completedCreatedTodos" :key="todo.id">
                <span class="todo-title">{{ todo.title }}</span> -
                <span class="todo-status">
                  {{ todo.completedByName ? 'Completata da: ' + todo.completedByName : 'Completata' }}
                </span>
              </li>
            </ul>
            <p v-if="completedCreatedTodos.length === 0" class="empty-msg">
              Non hai completato nessuna todo.
            </p>
          </div>
          <div class="todos-section completed-todos">
            <h3>Todo Completate a cui eri iscritto</h3>
            <ul>
              <li v-for="todo in completedSubscribedTodos" :key="todo.id">
                <span class="todo-title">{{ todo.title }}</span> -
                <span class="todo-status">
                  {{ todo.completedByName ? 'Completata da: ' + todo.completedByName : 'Completata' }}
                </span>
              </li>
            </ul>
            <p v-if="completedSubscribedTodos.length === 0" class="empty-msg">
              Non sei iscritto a nessuna todo completata.
            </p>
          </div>
        </div>
        <div v-else>
          <div class="todos-section completed-todos">
            <h3>Tutte le Todo Completate</h3>
            <ul>
              <li v-for="todo in completedTodos" :key="todo.id">
                <span class="todo-title">{{ todo.title }}</span> -
                <span class="todo-status">
                  {{ todo.completedByName ? 'Completata da: ' + todo.completedByName : 'Completata' }}
                </span>
              </li>
            </ul>
            <p v-if="completedTodos.length === 0" class="empty-msg">
              Non ci sono todo completate.
            </p>
          </div>
        </div>
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
      incompleteTodos: [],
      completedTodos: [],
      completedCreatedTodos: [],
      completedSubscribedTodos: [],
      isAdmin: false,
      adminEmail: '',
      showCompletedTodos: false,
    };
  },
  async created() {
    try {
      const adminEmailResponse = await axios.get("http://localhost:8080/profile/admin-email", { withCredentials: true });
      this.adminEmail = adminEmailResponse.data;

      const response = await axios.get("http://localhost:8080/profile", { withCredentials: true });
      this.user = response.data;

      if (this.user) {
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
        this.separateTodos();
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
          await this.fetchSubscribedTodos();
        }
      } catch (error) {
        console.error("Errore nel completamento della todo:", error);
      }
    },
    toggleCompletedTodosVisibility() {
      this.showCompletedTodos = !this.showCompletedTodos;
      if (!this.isAdmin) {
        this.completedCreatedTodos = this.createdTodos.filter(todo => todo.completed);
        this.completedSubscribedTodos = this.subscribedTodos.filter(todo => todo.completed);
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
  background-color: var(--card-bg, #f8f9fa);
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
  font-size: 1.1em;
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

.toggle-btn {
  cursor: pointer;
  font-size: 1.2em;
  color: #007bff;
  text-decoration: underline;
  margin-top: 20px;
}

.toggle-btn:hover {
  color: #0056b3;
}

.completed-todos {
  background-color: var(--card-bg, #f8f9fa);
  border: 2px solid var(--border-color, #ddd);
  box-shadow: 0 4px 6px var(--shadow-color, rgba(0, 0, 0, 0.1));
  border-radius: 8px;
  padding: 20px;
  margin-top: 20px;
}

.completed-todos h2,
.completed-todos h3 {
  font-size: 1.5em;
  margin-bottom: 15px;
  color: var(--text-color, #333);
}

.completed-todos ul {
  list-style: none;
  padding: 0;
}

.completed-todos li {
  padding: 10px;
  margin-bottom: 10px;
  border-bottom: 1px solid var(--border-color, #ccc);
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.completed-todos .todo-title {
  flex: 1;
  font-size: 1.2em;
}

.completed-todos .todo-status {
  margin-left: 10px;
}

.toggle-section {
  margin-top: 15px;
  /* distanza dai 2 paragrafi superiori */
}
</style>
