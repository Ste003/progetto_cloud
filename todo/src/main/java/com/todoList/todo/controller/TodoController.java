package com.todoList.todo.controller;

import com.todoList.todo.entities.TodoItem;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import com.todoList.todo.repository.TodoItemRepository;

@RestController
@RequestMapping("/api/todos")
public class TodoController {

    private final TodoItemRepository todoItemRepository;

    public TodoController(TodoItemRepository todoItemRepository) {
        this.todoItemRepository = todoItemRepository;
    }

    // Endpoint per ottenere tutte le todo non completate (per la Home)
    @GetMapping("/incomplete")
    public List<TodoItem> getIncompleteTodos() {
        return todoItemRepository.findByCompletedFalse();
    }
    
    // (Eventuale endpoint esistente per tutte le todo)
    @GetMapping
    public List<TodoItem> getAllTodos() {
        return todoItemRepository.findAll();
    }
}
