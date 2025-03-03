package com.todoList.todo.controller;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.todoList.todo.dto.TodoItemDTO;
import com.todoList.todo.entities.TodoItem;
import com.todoList.todo.entities.User;
import com.todoList.todo.repository.TodoItemRepository;
import com.todoList.todo.repository.UserRepository;
import com.todoList.todo.service.TodoService;

@RestController
@RequestMapping("/api/todos")
public class TodoController {

    private final TodoItemRepository todoItemRepository;
    private final TodoService todoService;
    private final UserRepository userRepository;

    public TodoController(TodoItemRepository todoItemRepository, TodoService todoService, UserRepository userRepository) {
        this.todoItemRepository = todoItemRepository;
        this.todoService = todoService;
        this.userRepository = userRepository;
    }

    @GetMapping
    public List<TodoItemDTO> getAllTodos(@AuthenticationPrincipal OAuth2User principal) {
        String currentUserEmail = principal != null ? principal.getAttribute("email") : "";
        List<TodoItem> todos = todoItemRepository.findAll();
        return todos.stream()
                .map(todo -> new TodoItemDTO(
                        todo.getId(),
                        todo.getTitle(),
                        todo.getCompleted(),
                        (todo.getUser() != null ? todo.getUser().getEmail() : null),
                        todo.getSubscribers().stream().anyMatch(subscriber -> subscriber.getEmail().equalsIgnoreCase(currentUserEmail)),
                        (todo.getCompletedBy() != null ? todo.getCompletedBy().getEmail() : null),
                        (todo.getCompletedBy() != null ? todo.getCompletedBy().getName() : null)
                ))
                .collect(Collectors.toList());
    }

    @GetMapping("/incomplete")
    public List<TodoItemDTO> getIncompleteTodos(@AuthenticationPrincipal OAuth2User principal) {
        String currentUserEmail = principal != null ? principal.getAttribute("email") : "";
        List<TodoItem> todos = todoItemRepository.findByCompletedFalse();
        return todos.stream()
                .map(todo -> new TodoItemDTO(
                        todo.getId(),
                        todo.getTitle(),
                        todo.getCompleted(),
                        (todo.getUser() != null ? todo.getUser().getEmail() : null),
                        todo.getSubscribers().stream().anyMatch(subscriber -> subscriber.getEmail().equalsIgnoreCase(currentUserEmail)),
                        (todo.getCompletedBy() != null ? todo.getCompletedBy().getEmail() : null),
                        (todo.getCompletedBy() != null ? todo.getCompletedBy().getName() : null)
                ))
                .collect(Collectors.toList());
    }

    @PostMapping("/create")
    public ResponseEntity<TodoItem> createTodo(@RequestBody java.util.Map<String, String> payload,
            @AuthenticationPrincipal OAuth2User principal) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        String title = payload.get("title");
        if (title == null || title.trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        String email = principal.getAttribute("email");
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (!userOpt.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        User user = userOpt.get();

        TodoItem todo = new TodoItem();
        todo.setTitle(title);
        todo.setCompleted(false);
        todo.setUser(user); // Imposta il creatore

        TodoItem savedTodo = todoService.createTodo(todo);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedTodo);
    }
}
