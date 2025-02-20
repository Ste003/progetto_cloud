package com.todoList.todo.service;

import org.springframework.stereotype.Service;

import com.todoList.todo.entities.User;
import com.todoList.todo.repository.UserRepository;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // Recupera un utente tramite email
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    // Crea un nuovo utente nel database
    public User createUser(User user) {
        return userRepository.save(user);
    }
}
