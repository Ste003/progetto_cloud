package com.todoList.todo.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.todoList.todo.dto.TodoItemDTO;
import com.todoList.todo.dto.UserDTO;
import com.todoList.todo.entities.User;
import com.todoList.todo.repository.UserRepository;

/**
 * Servizio per la gestione degli utenti.
 */
@Service
public class UserService {

    private final UserRepository userRepository;

    /**
     * Costruttore per iniezione delle dipendenze.
     */
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Trova un utente per email.
     */
    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }   

    /**
     * Crea un nuovo utente.
     */
    public User createUser(User user) {
        return userRepository.save(user);
    }
    
    /**
     * Recupera il profilo utente dato l'ID dell'utente.
     */
    public UserDTO getUserProfile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found")); // Eccezione se l'utente non è trovato

        List<TodoItemDTO> createdTodos = user.getCreatedTodos().stream()
                .map(todo -> new TodoItemDTO(
                        todo.getId(),
                        todo.getTitle(),
                        todo.getCompleted(),
                        null,
                        false,
                        (todo.getCompletedBy() != null ? todo.getCompletedBy().getEmail() : null),
                        (todo.getCompletedBy() != null ? todo.getCompletedBy().getName() : null)
                ))
                .collect(Collectors.toList()); // Mappa le To-Do create dall'utente

        List<TodoItemDTO> subscribedTodos = user.getSubscribedTodos().stream()
                .map(todo -> new TodoItemDTO(
                        todo.getId(),
                        todo.getTitle(),
                        todo.getCompleted(),
                        null,
                        false,
                        (todo.getCompletedBy() != null ? todo.getCompletedBy().getEmail() : null),
                        (todo.getCompletedBy() != null ? todo.getCompletedBy().getName() : null)
                ))
                .collect(Collectors.toList()); // Mappa le To-Do alle quali l'utente è iscritto
                
        return new UserDTO(user.getId(), user.getName(), user.getEmail(), createdTodos, subscribedTodos); // Restituisce il profilo utente
    }
}