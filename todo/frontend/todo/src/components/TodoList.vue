<script setup>
import { ref, onMounted } from 'vue';
import { fetchTodos, addTodo } from '../api';

const todos = ref([]);
const newTodo = ref("");

const loadTodos = async () => {
    todos.value = await fetchTodos();
};

const createTodo = async () => {
    if (!newTodo.value.trim()) return;
    await addTodo(newTodo.value);
    newTodo.value = "";
    loadTodos();
};

onMounted(loadTodos);
</script>

<template>
    <div>
        <h1>Todo List</h1>
        <input v-model="newTodo" placeholder="Nuovo Todo" />
        <button @click="createTodo">Aggiungi</button>
        <ul>
            <li v-for="todo in todos" :key="todo.id">
                {{ todo.title }} - Completato: {{ todo.completed }}
            </li>
        </ul>
    </div>
</template>
