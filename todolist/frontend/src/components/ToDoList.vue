<template>
    <div>
        <h1>To-Do List</h1>
        <ul>
            <li v-for="todo in todos" :key="todo.id">{{ todo.title }}</li>
        </ul>
    </div>
</template>

<script>
import apiClient from "../api";

export default {
    data() {
        return {
            todos: [],
        };
    },
    created() {
        apiClient.get('/todos') // Assicurati che il tuo backend abbia questo endpoint
            .then(response => {
                this.todos = response.data;
            })
            .catch(error => {
                console.error(error);
            });
    },
};
</script>
