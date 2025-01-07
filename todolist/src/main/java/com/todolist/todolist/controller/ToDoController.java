package com.todolist.todolist.controller;

import com.todolist.todolist.model.ToDo;
import com.todolist.todolist.service.ToDoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/todos")
public class ToDoController {

    private final ToDoService toDoService;

    @Autowired
    public ToDoController(ToDoService toDoService) {
        this.toDoService = toDoService;
    }

    @PostMapping
    public ResponseEntity<ToDo> createToDo(@RequestBody ToDo toDo) {
        ToDo savedToDo = toDoService.saveToDo(toDo);
        return ResponseEntity.ok(savedToDo);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ToDo> getToDoById(@PathVariable Long id) {
        return toDoService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ToDo>> getToDosByUserId(@PathVariable Long userId) {
        List<ToDo> toDos = toDoService.findByUserId(userId);
        return ResponseEntity.ok(toDos);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteToDoById(@PathVariable Long id) {
        toDoService.deleteToDo(id);
        return ResponseEntity.noContent().build();
    }
}
