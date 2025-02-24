package com.todoList.todo.controller;

import com.todoList.todo.entities.TodoItem;
import com.todoList.todo.entities.User;
import com.todoList.todo.repository.TodoItemRepository;
import com.todoList.todo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/profile")
public class ProfileController {
    
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TodoItemRepository todoItemRepository;

    @GetMapping("/created-todos")
    public List<TodoItem> getCreatedTodos(@AuthenticationPrincipal OAuth2User principal) {
        String email = principal.getAttribute("email");
        User user = userRepository.findByEmail(email);
        return todoItemRepository.findByUser(user);
    }

    @GetMapping("/subscribed-todos")
    public List<TodoItem> getSubscribedTodos(@AuthenticationPrincipal OAuth2User principal) {
        String email = principal.getAttribute("email");
        User user = userRepository.findByEmail(email);
        return user.getSubscribedTodos();
    }

    @PutMapping("/todos/{todoId}/complete")
    public ResponseEntity<String> completeTodo(@PathVariable Long todoId, @AuthenticationPrincipal OAuth2User principal) {
        TodoItem todo = todoItemRepository.findById(todoId).orElseThrow(() -> new RuntimeException("Todo non trovata"));
        
        String email = principal.getAttribute("email");
        User user = userRepository.findByEmail(email);

        if (!todo.getUser().equals(user)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Non puoi modificare questa to-do.");
        }

        todo.setCompleted(true);
        todoItemRepository.save(todo);
        return ResponseEntity.ok("Todo completata con successo");
    }
}

