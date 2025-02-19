<template>
    <div>
      <h1>Login</h1>
      <!-- Il pulsante di login reindirizza al backend per il login OAuth2 -->
      <a href="http://localhost:8080/oauth2/authorization/google">Login with Google</a>
    </div>
  </template>
  
  <script>
  export default {
    name: "Login"
  };
  </script>
  