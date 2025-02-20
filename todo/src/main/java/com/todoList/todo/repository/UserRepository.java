package com.todoList.todo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.todoList.todo.entities.User;

public interface UserRepository extends JpaRepository<User, Long> {
    // Metodo per trovare un utente tramite email
    User findByEmail(String email);
}
