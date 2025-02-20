<template>
  <div>
    <h1>Login</h1>
    <button @click="handleLogin">Login con Google</button>
  </div>
</template>

<script>
import { loginWithGoogle } from '../api';

export default {
  name: "Login",
  methods: {
    handleLogin() {
      loginWithGoogle();
    }
  }
};
</script>
