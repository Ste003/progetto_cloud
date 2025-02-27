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
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Value;

import com.todoList.todo.dto.TodoItemDTO;
import com.todoList.todo.dto.UserDTO;
import com.todoList.todo.entities.TodoItem;
import com.todoList.todo.entities.User;
import com.todoList.todo.repository.TodoItemRepository;
import com.todoList.todo.repository.UserRepository;

import jakarta.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/profile")
public class ProfileController {

    //DA RIMUOVERE SUCCESSIVAMETNE -- SERVE SOLO PER I LOG
    private static final Logger logger = LoggerFactory.getLogger(ProfileController.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TodoItemRepository todoItemRepository;

    @Value("${admin.email}")
    private String adminEmail;

    //DA RIMUOVERE SUCCESSIVAMETNE
    @PostConstruct
    public void init() {
        logger.info("Valore di admin.email letto da application.properties: {}", adminEmail);
    }

    // Recupera il profilo completo
    // Endpoint per ottenere il profilo completo con le todo create e le iscrizioni
    // @GetMapping
    // public ResponseEntity<UserDTO> getUserProfile(@AuthenticationPrincipal OAuth2User principal) {
    //     if (principal == null) {
    //         return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
    //     }
    //     String email = principal.getAttribute("email");
    //     User user = userRepository.findByEmail(email)
    //             .orElseThrow(() -> new RuntimeException("Utente non trovato"));
    //     List<TodoItemDTO> createdTodos = user.getTodoItems().stream()
    //             .map(todo -> new TodoItemDTO(
    //             todo.getId(),
    //             todo.getTitle(),
    //             todo.getCompleted(),
    //             (todo.getUser() != null ? todo.getUser().getEmail() : null)))
    //             .collect(Collectors.toList());
    //     List<TodoItemDTO> subscribedTodos = user.getSubscribedTodos().stream()
    //             .map(todo -> new TodoItemDTO(
    //             todo.getId(),
    //             todo.getTitle(),
    //             todo.getCompleted(),
    //             (todo.getUser() != null ? todo.getUser().getEmail() : null)))
    //             .collect(Collectors.toList());
    //     UserDTO userDTO = new UserDTO(user.getId(), user.getName(), user.getEmail(), createdTodos, subscribedTodos);
    //     return ResponseEntity.ok(userDTO);
    // }
    @GetMapping
    public ResponseEntity<UserDTO> getUserProfile(@AuthenticationPrincipal OAuth2User principal) {
        //DA RIMUOVERE SUCCESSIVAMETNE
        logger.info("Admin email (valore letto da properties): {}", adminEmail);

        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
        String email = principal.getAttribute("email");
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utente non trovato"));

        List<TodoItemDTO> createdTodos = user.getTodoItems().stream()
                .map(todo -> new TodoItemDTO(todo.getId(), todo.getTitle(), todo.getCompleted(),
                (todo.getUser() != null ? todo.getUser().getEmail() : null)))
                .collect(Collectors.toList());

        List<TodoItemDTO> subscribedTodos = user.getSubscribedTodos().stream()
                .map(todo -> new TodoItemDTO(todo.getId(), todo.getTitle(), todo.getCompleted(),
                (todo.getUser() != null ? todo.getUser().getEmail() : null)))
                .collect(Collectors.toList());

        UserDTO userDTO = new UserDTO(user.getId(), user.getName(), user.getEmail(), createdTodos, subscribedTodos);
        return ResponseEntity.ok(userDTO);
    }

    @GetMapping("/admin-email")
    public ResponseEntity<String> getAdminEmail() {
        return ResponseEntity.ok(adminEmail);  // adminEmail è iniettato da application.properties
    }

    // Recupera le todo create dall'utente
    @GetMapping("/created-todos")
    public ResponseEntity<List<TodoItemDTO>> getCreatedTodos(@AuthenticationPrincipal OAuth2User principal) {
        String email = principal.getAttribute("email");
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utente non trovato"));
        List<TodoItemDTO> createdTodos = user.getTodoItems().stream()
                .map(todo -> new TodoItemDTO(todo.getId(), todo.getTitle(), todo.getCompleted(),
                (todo.getUser() != null ? todo.getUser().getEmail() : null)))
                .collect(Collectors.toList());
        return ResponseEntity.ok(createdTodos);
    }

    // Recupera le todo a cui l'utente è iscritto
    @GetMapping("/subscribed-todos")
    public ResponseEntity<List<TodoItemDTO>> getSubscribedTodos(@AuthenticationPrincipal OAuth2User principal) {
        String email = principal.getAttribute("email");
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utente non trovato"));
        List<TodoItemDTO> subscribedTodos = user.getSubscribedTodos().stream()
                .map(todo -> new TodoItemDTO(todo.getId(), todo.getTitle(), todo.getCompleted(),
                (todo.getUser() != null ? todo.getUser().getEmail() : null)))
                .collect(Collectors.toList());
        return ResponseEntity.ok(subscribedTodos);
    }

    // Endpoint per iscriversi a una todo
    @PostMapping("/todos/{todoId}/subscribe")
    public ResponseEntity<String> subscribeToTodo(@PathVariable Long todoId,
            @AuthenticationPrincipal OAuth2User principal) {
        String email = principal.getAttribute("email");
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utente non trovato"));
        TodoItem todo = todoItemRepository.findById(todoId)
                .orElseThrow(() -> new RuntimeException("Todo non trovata"));

        if (!todo.getSubscribers().contains(user)) {
            todo.getSubscribers().add(user);
            todoItemRepository.save(todo);
            return ResponseEntity.ok("Iscrizione avvenuta con successo");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Già iscritto");
        }
    }

    // Endpoint per completare una todo
    @PutMapping("/todos/{todoId}/complete")
    public ResponseEntity<String> completeTodo(@PathVariable Long todoId,
            @AuthenticationPrincipal OAuth2User principal) {
        TodoItem todo = todoItemRepository.findById(todoId)
                .orElseThrow(() -> new RuntimeException("Todo non trovata"));
        String email = principal.getAttribute("email");
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utente non trovato"));

        // Se l'utente è admin, può completare tutte le todo
        boolean isAdmin = user.getEmail().equals(adminEmail);
        if (!isAdmin && (todo.getUser() == null || !todo.getUser().getEmail().equalsIgnoreCase(user.getEmail()))) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Non puoi modificare questa to-do.");
        }
        todo.setCompleted(true);
        todoItemRepository.save(todo);
        return ResponseEntity.ok("Todo completata con successo");
    }

    // Endpoint per permettere all'admin di chiudere tutte le todo
    @PatchMapping("/todos/complete")
    public ResponseEntity<Void> completeAllTodos(@AuthenticationPrincipal OAuth2User principal) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String email = principal.getAttribute("email");
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (!userOpt.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        User user = userOpt.get();
        // Controllo amministratore, confrontando anche il nome e l'email
        boolean isAdmin = user.getEmail().equals(adminEmail);
        if (!isAdmin) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        List<TodoItem> allTodos = todoItemRepository.findAll();
        for (TodoItem todo : allTodos) {
            if (!todo.getCompleted()) {
                todo.setCompleted(true);
                todoItemRepository.save(todo);
            }
        }
        return ResponseEntity.ok().build();
    }

    // All'interno del metodo getAllTodos (per esempio)
    @GetMapping("/todos")
    public ResponseEntity<List<TodoItemDTO>> getAllTodos(@AuthenticationPrincipal OAuth2User principal) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        String email = principal.getAttribute("email");
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (!userOpt.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        User user = userOpt.get();
        // Controllo basato solo sull'email
        boolean isAdmin = user.getEmail().equalsIgnoreCase(adminEmail);
        if (!isAdmin) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        List<TodoItemDTO> todos = todoItemRepository.findAll().stream()
                .map(todo -> new TodoItemDTO(
                todo.getId(),
                todo.getTitle(),
                todo.getCompleted(),
                (todo.getUser() != null ? todo.getUser().getEmail() : null),
                false)) // Flag subscribed impostato a false
                .collect(Collectors.toList());
        return ResponseEntity.ok(todos);
    }

}
