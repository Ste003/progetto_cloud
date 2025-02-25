package com.todoList.todo.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.todoList.todo.dto.TodoItemDTO;
import com.todoList.todo.entities.TodoItem;
import com.todoList.todo.repository.TodoItemRepository;

@RestController
@RequestMapping("/api/todos")
public class TodoController {

    @Autowired
    private TodoItemRepository todoItemRepository;

    @GetMapping
    public List<TodoItemDTO> getAllTodos(@AuthenticationPrincipal OAuth2User principal) {
        String currentUserEmail = principal != null ? principal.getAttribute("email") : "";
        List<TodoItem> todos = todoItemRepository.findAll();
        return todos.stream()
                .map(todo -> new TodoItemDTO(
                        todo.getId(),
                        todo.getTitle(),
                        todo.getCompleted(),
                        todo.getUser() != null ? todo.getUser().getEmail() : null,
                        // Calcola se l'utente corrente è già iscritto alla todo
                        todo.getSubscribers().stream()
                            .anyMatch(subscriber -> subscriber.getEmail().equalsIgnoreCase(currentUserEmail))
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
                        todo.getUser() != null ? todo.getUser().getEmail() : null,
                        todo.getSubscribers().stream()
                            .anyMatch(subscriber -> subscriber.getEmail().equalsIgnoreCase(currentUserEmail))
                ))
                .collect(Collectors.toList());
    }
}
