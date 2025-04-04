package com.todoList.todo.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.todoList.todo.dto.TodoItemDTO;
import com.todoList.todo.dto.UserDTO;
import com.todoList.todo.service.UserService;
import com.todoList.todo.entities.User;

/**
 * Controller per la gestione degli utenti e delle loro iscrizioni alle To-Do.
 * 
 * Espone endpoint per:
 * - Recuperare il profilo utente
 * - Recuperare le informazioni di base dell'utente autenticato
 * - Recuperare le To-Do alle quali l'utente è iscritto
 */
@RestController
public class UserController {

    private final UserService userService;

    /**
     * Costruttore per iniezione delle dipendenze.
     */
    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Recupera il profilo utente dato l'ID dell'utente.
     */
    @GetMapping("/users/{userId}/profile")
    public UserDTO getUserProfile(@PathVariable Long userId) {
        UserDTO user = userService.getUserProfile(userId);
        return new UserDTO(user.getId(), user.getName(), user.getEmail(), null, null); // Restituisce il profilo utente
    }

    /**
     * Recupera le informazioni di base dell'utente autenticato.
     */
    @GetMapping("/user")
    public ResponseEntity<Map<String, Object>> getBasicUserInfo(@AuthenticationPrincipal OAuth2User principal) {
        if (principal != null) {
            Map<String, Object> user = new HashMap<>();
            user.put("name", principal.getAttribute("name"));
            user.put("email", principal.getAttribute("email"));
            return ResponseEntity.ok(user); // Restituisce le informazioni di base dell'utente
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null); // Restituisce 401 se l'utente non è autenticato
    }

    /**
     * Recupera le To-Do alle quali l'utente è iscritto.
     */
    @GetMapping("/subscriptions")
    public ResponseEntity<List<TodoItemDTO>> getUserSubscriptions(@AuthenticationPrincipal OAuth2User principal) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(); // Restituisce 401 se l'utente non è autenticato
        }
        String email = principal.getAttribute("email");
        Optional<User> userOpt = userService.getUserByEmail(email);
        if (!userOpt.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build(); // Restituisce 404 se l'utente non è trovato
        }
        User user = userOpt.get();
        List<TodoItemDTO> subscribedTodos = user.getSubscribedTodos().stream()
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

        return ResponseEntity.ok(subscribedTodos); // Restituisce la lista delle To-Do alle quali l'utente è iscritto
    }
}