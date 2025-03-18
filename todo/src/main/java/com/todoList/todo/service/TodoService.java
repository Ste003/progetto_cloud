package com.todoList.todo.service;

import com.todoList.todo.entities.TodoItem;
import com.todoList.todo.kafka.TodoKafkaProducer;
import com.todoList.todo.repository.TodoItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TodoService {

    private final TodoItemRepository todoItemRepository;
    
    private TodoKafkaProducer todoKafkaProducer; // Non `final`, perché potrebbe non essere iniettato

    public TodoService(TodoItemRepository todoItemRepository) {
        this.todoItemRepository = todoItemRepository;
    }

    @Autowired(required = false) // Se Kafka è disponibile, lo iniettiamo
    public void setTodoKafkaProducer(TodoKafkaProducer todoKafkaProducer) {
        this.todoKafkaProducer = todoKafkaProducer;
    }

    public List<TodoItem> getAllTodos() {
        return todoItemRepository.findAll();
    }

    public TodoItem createTodo(TodoItem todoItem) {
        return todoItemRepository.save(todoItem);
    }

    @Transactional
    public void closeTodo(Long todoId, String userEmail) {
        TodoItem todo = todoItemRepository.findById(todoId)
                .orElseThrow(() -> new RuntimeException("To-Do non trovata"));

        if (!Boolean.TRUE.equals(todo.getCompleted())) {
            todo.setCompleted(true);
            todoItemRepository.save(todo);

            if (todoKafkaProducer != null) { // Evitiamo NullPointerException
                todoKafkaProducer.sendTodoClosedNotification(todoId, todo.getTitle(), userEmail);
            }
        }
    }
}
