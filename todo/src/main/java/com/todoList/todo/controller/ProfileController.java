package com.todoList.todo.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
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
    
    // @Autowired
    // private UserService userService;

    // Endpoint per ottenere il profilo completo con le todo create e le iscrizioni
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
        // Mappa le todo create
        List<TodoItemDTO> createdTodos = user.getTodoItems().stream()
                .map(todo -> new TodoItemDTO(
                        todo.getId(), 
                        todo.getTitle(), 
                        todo.getCompleted(), 
                        (todo.getUser() != null ? todo.getUser().getEmail() : null)))
                .collect(Collectors.toList());
        // Mappa le todo a cui l'utente è iscritto
        List<TodoItemDTO> subscribedTodos = user.getSubscribedTodos().stream()
                .map(todo -> new TodoItemDTO(
                        todo.getId(), 
                        todo.getTitle(), 
                        todo.getCompleted(), 
                        (todo.getUser() != null ? todo.getUser().getEmail() : null)))
                .collect(Collectors.toList());
        
        UserDTO userDTO = new UserDTO();
        userDTO.setId(user.getId());
        userDTO.setName(user.getName());
        userDTO.setEmail(user.getEmail());
        userDTO.setTodoItems(createdTodos);
        userDTO.setSubscribedTodos(subscribedTodos);
        
        return ResponseEntity.ok(userDTO);
    }
    
    // Endpoint per ottenere le todo create dall'utente
    @GetMapping("/created-todos")
    public ResponseEntity<List<TodoItemDTO>> getCreatedTodos(@AuthenticationPrincipal OAuth2User principal) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
        String email = principal.getAttribute("email");
        User user = userRepository.findByEmail(email);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
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
        User user = userRepository.findByEmail(email);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        List<TodoItemDTO> subscribedTodos = user.getSubscribedTodos().stream()
                .map(todo -> new TodoItemDTO(
                        todo.getId(), 
                        todo.getTitle(), 
                        todo.getCompleted(), 
                        (todo.getUser() != null ? todo.getUser().getEmail() : null)))
                .collect(Collectors.toList());
        return ResponseEntity.ok(subscribedTodos);
    }
    
    // Endpoint per completare una todo
    @PutMapping("/todos/{todoId}/complete")
    public ResponseEntity<String> completeTodo(@PathVariable Long todoId,
                                               @AuthenticationPrincipal OAuth2User principal) {
        TodoItem todo = todoItemRepository.findById(todoId)
                .orElseThrow(() -> new RuntimeException("Todo non trovata"));
        String email = principal.getAttribute("email");
        User user = userRepository.findByEmail(email);
        if (todo.getUser() == null || !todo.getUser().getEmail().equalsIgnoreCase(user.getEmail())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Non puoi modificare questa to-do.");
        }
        todo.setCompleted(true);
        todoItemRepository.save(todo);
        return ResponseEntity.ok("Todo completata con successo");
    }
}
