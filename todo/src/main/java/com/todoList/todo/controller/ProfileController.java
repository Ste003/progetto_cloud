package com.todoList.todo.controller;

import java.util.List;
import java.util.stream.Collectors;

import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.todoList.todo.dto.TodoItemDTO;
import com.todoList.todo.dto.UserDTO;
import com.todoList.todo.entities.TodoItem;
import com.todoList.todo.entities.User;
import com.todoList.todo.repository.TodoItemRepository;
import com.todoList.todo.repository.UserRepository;

@RestController
@RequestMapping("/profile")
public class ProfileController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TodoItemRepository todoItemRepository;

    // Endpoint per ottenere il profilo completo con le todo create e le iscrizioni
    @GetMapping
    public ResponseEntity<UserDTO> getUserProfile(@AuthenticationPrincipal OAuth2User principal) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
        String email = principal.getAttribute("email");
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utente non trovato"));

        List<TodoItemDTO> createdTodos = user.getTodoItems().stream()
                .map(todo -> new TodoItemDTO(
                todo.getId(),
                todo.getTitle(),
                todo.getCompleted(),
                (todo.getUser() != null ? todo.getUser().getEmail() : null)))
                .collect(Collectors.toList());

        List<TodoItemDTO> subscribedTodos = user.getSubscribedTodos().stream()
                .map(todo -> new TodoItemDTO(
                todo.getId(),
                todo.getTitle(),
                todo.getCompleted(),
                (todo.getUser() != null ? todo.getUser().getEmail() : null)))
                .collect(Collectors.toList());

        UserDTO userDTO = new UserDTO(user.getId(), user.getName(), user.getEmail(), createdTodos, subscribedTodos);
        return ResponseEntity.ok(userDTO);
    }

    // Endpoint per ottenere le todo create dall'utente
    @GetMapping("/created-todos")
    public ResponseEntity<List<TodoItemDTO>> getCreatedTodos(@AuthenticationPrincipal OAuth2User principal) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
        String email = principal.getAttribute("email");
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utente non trovato"));
        List<TodoItemDTO> createdTodos = user.getTodoItems().stream()
                .map(todo -> new TodoItemDTO(
                todo.getId(),
                todo.getTitle(),
                todo.getCompleted(),
                (todo.getUser() != null ? todo.getUser().getEmail() : null)))
                .collect(Collectors.toList());
        return ResponseEntity.ok(createdTodos);
    }

    // Endpoint per ottenere le todo a cui l'utente è iscritto
    @GetMapping("/subscribed-todos")
    public ResponseEntity<List<TodoItemDTO>> getSubscribedTodos(@AuthenticationPrincipal OAuth2User principal) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
        String email = principal.getAttribute("email");
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utente non trovato"));
        List<TodoItemDTO> subscribedTodos = user.getSubscribedTodos().stream()
                .map(todo -> new TodoItemDTO(
                todo.getId(),
                todo.getTitle(),
                todo.getCompleted(),
                (todo.getUser() != null ? todo.getUser().getEmail() : null)))
                .collect(Collectors.toList());
        return ResponseEntity.ok(subscribedTodos);
    }

    @PostMapping("/todos/{todoId}/subscribe")
    public ResponseEntity<String> subscribeToTodo(@PathVariable Long todoId,
            @AuthenticationPrincipal OAuth2User principal) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        String email = principal.getAttribute("email");
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (!userOpt.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Utente non trovato");
        }
        User user = userOpt.get();

        Optional<TodoItem> todoOpt = todoItemRepository.findById(todoId);
        if (!todoOpt.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Todo non trovata");
        }
        TodoItem todo = todoOpt.get();

        // Se l'utente non è già iscritto, aggiungilo
        if (!todo.getSubscribers().contains(user)) {
            todo.getSubscribers().add(user);
            todoItemRepository.save(todo);
            return ResponseEntity.ok("Iscrizione avvenuta con successo");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Già iscritto");
        }
    }
}
