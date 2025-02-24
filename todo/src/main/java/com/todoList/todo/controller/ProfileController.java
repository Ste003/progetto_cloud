package com.todoList.todo.controller;

import com.todoList.todo.dto.TodoItemDTO;
import com.todoList.todo.dto.UserDTO;
import com.todoList.todo.entities.TodoItem;
import com.todoList.todo.entities.User;
import com.todoList.todo.repository.UserRepository;
import com.todoList.todo.repository.TodoItemRepository;
import com.todoList.todo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/profile")
public class ProfileController {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TodoItemRepository todoItemRepository;
    @Autowired
    private UserService userService;

    @GetMapping
    public ResponseEntity<UserDTO> getUserProfile(@AuthenticationPrincipal OAuth2User principal) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }

        String email = principal.getAttribute("email");
        User user = userRepository.findByEmail(email);

        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        // Mappa le Todo create dall'utente
        List<TodoItemDTO> createdTodos = user.getTodoItems().stream()
                .map(todo -> new TodoItemDTO(todo.getId(), todo.getTitle(), todo.getCompleted()))
                .collect(Collectors.toList());

        // Mappa le Todo a cui l'utente è iscritto
        List<TodoItemDTO> subscribedTodos = user.getSubscribedTodos().stream()
                .map(todo -> new TodoItemDTO(todo.getId(), todo.getTitle(), todo.getCompleted()))
                .collect(Collectors.toList());

        UserDTO userDTO = new UserDTO();
        userDTO.setName(user.getName());
        userDTO.setEmail(user.getEmail());
        userDTO.setTodoItems(createdTodos);
        userDTO.setSubscribedTodos(subscribedTodos);

        return ResponseEntity.ok(userDTO);
    }

    // Se desideri mantenere gli altri endpoint (ad esempio, per completare le to-do),
    // lasciali invariati.
    @PutMapping("/todos/{todoId}/complete")
    public ResponseEntity<String> completeTodo(@PathVariable Long todoId,
            @AuthenticationPrincipal OAuth2User principal) {
        // Trova la todo per id
        TodoItem todo = todoItemRepository.findById(todoId)
                .orElseThrow(() -> new RuntimeException("Todo non trovata"));

        // Estrai l'email dell'utente autenticato
        String email = principal.getAttribute("email");
        User user = userRepository.findByEmail(email);

        // Confronta le email (ignorando eventuali differenze di case)
        if (todo.getUser() == null || !todo.getUser().getEmail().equalsIgnoreCase(user.getEmail())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Non puoi modificare questa to-do.");
        }

        // Imposta la todo come completata e salvala
        todo.setCompleted(true);
        todoItemRepository.save(todo);
        return ResponseEntity.ok("Todo completata con successo");
    }

    @GetMapping("/created-todos")
    public List<TodoItemDTO> getCreatedTodos(@AuthenticationPrincipal OAuth2User principal) {
        String email = principal.getAttribute("email");
        User user = userRepository.findByEmail(email);

        return user.getTodoItems().stream()
                .map(todo -> new TodoItemDTO(todo.getId(), todo.getTitle(), todo.getCompleted(), 
                todo.getUser() != null ? todo.getUser().getEmail() : null))
                .collect(Collectors.toList());
    }

    @GetMapping("/subscribed-todos")
    public List<TodoItemDTO> getSubscribedTodos(@AuthenticationPrincipal OAuth2User principal) {
        String email = principal.getAttribute("email");
        User user = userRepository.findByEmail(email);

        return user.getSubscribedTodos().stream()
                .map(todo -> new TodoItemDTO(todo.getId(), todo.getTitle(), todo.getCompleted(), 
                todo.getUser() != null ? todo.getUser().getEmail() : null))
                .collect(Collectors.toList());
    }

    // Endpoint per iscriversi a una Todo
    @PostMapping("/todos/{todoId}/subscribe")
    public ResponseEntity<String> subscribeTodo(@PathVariable Long todoId,
                                                @AuthenticationPrincipal OAuth2User principal) {
        String email = principal.getAttribute("email");
        User currentUser = userRepository.findByEmail(email);
        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Utente non trovato");
        }
        
        TodoItem todo = todoItemRepository.findById(todoId)
                .orElseThrow(() -> new RuntimeException("Todo non trovata"));
        
        // Se l'utente corrente è il creatore, non può iscriversi
        if (todo.getUser() != null && todo.getUser().getId().equals(currentUser.getId())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Non puoi iscriverti alla tua todo");
        }
        
        // Verifica se l'utente è già iscritto
        boolean alreadySubscribed = todo.getSubscribers().stream()
                .anyMatch(u -> u.getId().equals(currentUser.getId()));
        if (alreadySubscribed) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Sei già iscritto a questa todo");
        }
        
        // Aggiungi l'utente ai sottoscrittori e salva la todo
        todo.getSubscribers().add(currentUser);
        todoItemRepository.save(todo);
        
        return ResponseEntity.ok("Iscrizione avvenuta con successo");
    }
}
