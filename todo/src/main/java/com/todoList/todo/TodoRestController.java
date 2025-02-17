package com.todoList.todo;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.todoList.todo.entities.TodoItem;
import com.todoList.todo.repository.TodoItemRepository;

@RestController
@RequestMapping("/api")
public class TodoRestController {

    private final TodoItemRepository repository;

    @Autowired
    public TodoRestController(TodoItemRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/todos")
    public List<TodoItem> getTodos() {
        return repository.findAll();
    }
}

