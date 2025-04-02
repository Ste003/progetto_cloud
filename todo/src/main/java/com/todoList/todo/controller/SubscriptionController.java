package com.todoList.todo.controller;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.todoList.todo.dto.TodoItemDTO;
import com.todoList.todo.dto.TodoWithSubscribersDTO;
import com.todoList.todo.dto.UserDTO;
import com.todoList.todo.entities.User;
import com.todoList.todo.repository.UserRepository;

/**
 * Controller per la gestione delle iscrizioni alle To-Do e del profilo utente.
 * 
 * Espone endpoint per:
 * - Recuperare le To-Do alle quali l'utente è iscritto
 * - Recuperare le To-Do create (con i relativi sottoscrittori)
 * - Iscrivere l'utente a una To-Do
 * - Completare le To-Do (incluso l'invio delle notifiche)
 * - Altri endpoint relativi al profilo
 */
@RestController
@RequestMapping("/api/subscriptions") // Mappa il controller sull'endpoint /api/subscriptions
public class SubscriptionController {

    // Repository per accedere ai dati degli utenti
    @Autowired
    private UserRepository userRepository;

    /**
     * Recupera le To-Do alle quali l'utente è iscritto.
     */
    @GetMapping
    public ResponseEntity<List<TodoItemDTO>> getUserSubscriptions(@AuthenticationPrincipal OAuth2User principal) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(); // Restituisce 401 se l'utente non è autenticato
        }
        String email = principal.getAttribute("email");
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (!userOpt.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build(); // Restituisce 404 se l'utente non è trovato
        }
        User user = userOpt.get();
        List<TodoItemDTO> subscriptions = user.getSubscribedTodos().stream()
                .map(todo -> new TodoItemDTO(
                        todo.getId(), 
                        todo.getTitle(), 
                        todo.getCompleted(), 
                        (todo.getUser() != null ? todo.getUser().getEmail() : null),
                        false,
                        (todo.getCompletedBy() != null ? todo.getCompletedBy().getEmail() : null),
                        (todo.getCompletedBy() != null ? todo.getCompletedBy().getName() : null)
                ))
                .collect(Collectors.toList());
        return ResponseEntity.ok(subscriptions); // Restituisce la lista delle To-Do alle quali l'utente è iscritto
    }

    /**
     * Recupera le To-Do create dall'utente con i relativi sottoscrittori.
     */
    @GetMapping("/created-todos-subscribers")
    public ResponseEntity<List<TodoWithSubscribersDTO>> getCreatedTodosWithSubscribers(@AuthenticationPrincipal OAuth2User principal) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(); // Restituisce 401 se l'utente non è autenticato
        }
        String email = principal.getAttribute("email");
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (!userOpt.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build(); // Restituisce 404 se l'utente non è trovato
        }
        User user = userOpt.get();
        List<TodoWithSubscribersDTO> dtos = user.getTodoItems().stream().map(todo -> {
            List<UserDTO> subscribers = todo.getSubscribers().stream()
                    .map(sub -> new UserDTO(sub.getId(), sub.getName(), sub.getEmail(), null, null))
                    .collect(Collectors.toList());
            return new TodoWithSubscribersDTO(todo.getId(), todo.getTitle(), todo.getCompleted(), subscribers);
        }).collect(Collectors.toList());
        return ResponseEntity.ok(dtos); // Restituisce la lista delle To-Do create dall'utente con i relativi sottoscrittori
    }
    
    /**
     * Recupera le To-Do alle quali l'utente è iscritto con i relativi sottoscrittori.
     */
    @GetMapping("/subscribed-todos-subscribers")
    public ResponseEntity<List<TodoWithSubscribersDTO>> getSubscribedTodosWithSubscribers(@AuthenticationPrincipal OAuth2User principal) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(); // Restituisce 401 se l'utente non è autenticato
        }
        String email = principal.getAttribute("email");
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (!userOpt.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build(); // Restituisce 404 se l'utente non è trovato
        }
        User user = userOpt.get();
        List<TodoWithSubscribersDTO> dtos = user.getSubscribedTodos().stream().map(todo -> {
            List<UserDTO> subscribers = todo.getSubscribers().stream()
                    .map(sub -> new UserDTO(sub.getId(), sub.getName(), sub.getEmail(), null, null))
                    .collect(Collectors.toList());
            return new TodoWithSubscribersDTO(todo.getId(), todo.getTitle(), todo.getCompleted(), subscribers);
        }).collect(Collectors.toList());
        return ResponseEntity.ok(dtos); // Restituisce la lista delle To-Do alle quali l'utente è iscritto con i relativi sottoscrittori
    }
}