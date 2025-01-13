package com.todolist.todolist.controller;

import com.todolist.todolist.model.User;
import com.todolist.todolist.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.todolist.todolist.model.LoginRequest;

@RestController
@RequestMapping("/api/auth")
public class LoginController {

    private final UserRepository userRepository;

    @Autowired
    public LoginController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        System.out.println("Giuseppe " + loginRequest);
        // Trova l'utente nel database
        User user = userRepository.findByUsername(loginRequest.getUsername());

        // Verifica se l'utente esiste e la password è corretta
        if (user == null || !user.getPassword().equals(loginRequest.getPassword())) {
            return ResponseEntity.status(401).body("Credenziali non valide");
        }

        // Se il login ha successo
        return ResponseEntity.ok("Login riuscito!");
    }
}