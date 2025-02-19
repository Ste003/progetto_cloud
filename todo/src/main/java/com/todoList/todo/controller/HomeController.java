package com.todoList.todo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.todoList.todo.entities.TodoItem;
import com.todoList.todo.repository.TodoItemRepository;

@RestController
@RequestMapping("/home") // Cambiato per evitare conflitti
public class HomeController {

    private final TodoItemRepository repository;

    @Autowired
    public HomeController(TodoItemRepository repository) {
        this.repository = repository;
    }

    @GetMapping(produces = "text/html")  // Ora risponde su "/home"
    public String home() {
        List<TodoItem> todos = repository.findAll();
        StringBuilder sb = new StringBuilder("<html><body>");
        sb.append("<h1>Todo List</h1>");
        sb.append("<ul>");
        for (TodoItem todo : todos) {
            sb.append("<li>")
              .append(todo.getTitle())
              .append(" - Completato: ")
              .append(todo.getCompleted())
              .append("</li>");
        }
        sb.append("</ul>");
        sb.append("</body></html>");
        return sb.toString();
    }
}
