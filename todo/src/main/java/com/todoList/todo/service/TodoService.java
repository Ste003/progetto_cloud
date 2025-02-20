package com.todoList.todo.service;

import com.todoList.todo.entities.TodoItem;
import com.todoList.todo.repository.TodoItemRepository;
//import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class TodoService {

    private final TodoItemRepository todoItemRepository;

    //@Autowired Unnecessary `@Autowired` annotation ---> warning dato da vscode
    public TodoService(TodoItemRepository todoItemRepository) {
        this.todoItemRepository = todoItemRepository;
    }

    public List<TodoItem> getAllTodos() {
        return todoItemRepository.findAll();
    }

    public TodoItem createTodo(TodoItem todoItem) {
        return todoItemRepository.save(todoItem);
    }
}
