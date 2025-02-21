package com.todoList.todo.controller;

import com.todoList.todo.entities.TodoItem;
import com.todoList.todo.entities.User;
import com.todoList.todo.repository.TodoItemRepository;
import com.todoList.todo.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/profile")
public class ProfileController {

    private final TodoItemRepository todoItemRepository;
    private final UserService userService;

    public ProfileController(TodoItemRepository todoItemRepository, UserService userService) {
        this.todoItemRepository = todoItemRepository;
        this.userService = userService;
    }

    // Endpoint per ottenere le todo completate create dall'utente
    @GetMapping("/completed-todos")
    public ResponseEntity<List<TodoItem>> getCompletedTodos(@AuthenticationPrincipal OAuth2User principal) {
        String email = principal.getAttribute("email");
        User user = userService.getUserByEmail(email);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        List<TodoItem> todos = todoItemRepository.findByUserIdAndCompletedTrue(user.getId());
        return ResponseEntity.ok(todos);
    }

    // Endpoint per segnare una todo come completata (se vuoi permettere l'azione dal profilo)
    @PutMapping("/todos/{todoId}/complete")
    public ResponseEntity<Void> completeTodo(@PathVariable Long todoId) {
        TodoItem todoItem = todoItemRepository.findById(todoId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Todo non trovato con id " + todoId));
        todoItem.setCompleted(true);
        todoItemRepository.save(todoItem);
        return ResponseEntity.noContent().build();
    }
}
