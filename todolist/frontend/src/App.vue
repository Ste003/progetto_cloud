<template>
  <div id="app">
    <ToDoList />
    <router-link to="/login">Login</router-link>
    <router-view></router-view>
  </div>
</template>

<script>
import ToDoList from './components/ToDoList.vue';

export default {
  name: 'App',
  components: {
    ToDoList,
  },
};
</script>
