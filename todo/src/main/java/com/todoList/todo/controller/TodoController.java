package com.todoList.todo.controller;

import com.todoList.todo.entities.TodoItem;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import com.todoList.todo.repository.TodoItemRepository;

@RestController
@RequestMapping("/api/todos")
public class TodoController {
    
    @Autowired
    private TodoItemRepository todoItemRepository;

    @GetMapping
    public List<TodoItem> getAllTodos() {
        return todoItemRepository.findAll();
    }

    @GetMapping("/incomplete")
    public List<TodoItem> getIncompleteTodos() {
        return todoItemRepository.findByCompletedFalse();
    }
}
