package com.todoList.todo.controller;

import com.todoList.todo.dto.TodoItemDTO;
import com.todoList.todo.entities.TodoItem;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

import com.todoList.todo.repository.TodoItemRepository;

@RestController
@RequestMapping("/api/todos")
public class TodoController {

    @Autowired
    final private TodoItemRepository todoItemRepository;

    public TodoController(TodoItemRepository todoItemRepository) {
        this.todoItemRepository = todoItemRepository;
    }

    // Restituisce tutte le to-do come DTO per evitare serializzazioni cicliche
    @GetMapping
    public List<TodoItemDTO> getAllTodos() {
        List<TodoItem> todos = todoItemRepository.findAll();
        return todos.stream()
                .map(todo -> new TodoItemDTO(todo.getId(), todo.getTitle(), todo.getCompleted()))
                .collect(Collectors.toList());
    }

    @GetMapping("/incomplete") //endpoint per le todo non completate
    public List<TodoItemDTO> getIncompleteTodos() {
        List<TodoItem> todos = todoItemRepository.findByCompletedFalse();
        return todos.stream()
                .map(todo -> new TodoItemDTO(todo.getId(), todo.getTitle(), todo.getCompleted()))
                .collect(Collectors.toList());
    }
}
