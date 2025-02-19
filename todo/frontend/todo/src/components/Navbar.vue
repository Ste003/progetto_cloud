<template>
    <nav class="navbar">
      <router-link to="/" class="nav-item">Login</router-link>
      <router-link to="/home" class="nav-item">Home</router-link>
      <router-link to="/dashboard" class="nav-item">Dashboard</router-link>
    </nav>
  </template>
  
  <style scoped>
  .navbar {
    display: flex;
    justify-content: center;
    background: #333;
    padding: 10px;
  }
  
  .nav-item {
    color: white;
    text-decoration: none;
    margin: 0 15px;
    font-weight: bold;
    padding: 5px 10px;
  }
  
  .nav-item:hover {
    background: #555;
    border-radius: 5px;
  }
  </style>
