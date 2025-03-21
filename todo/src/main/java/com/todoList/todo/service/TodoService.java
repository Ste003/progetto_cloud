package com.todoList.todo.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.todoList.todo.entities.TodoItem;
import com.todoList.todo.kafka.TodoKafkaProducer;
import com.todoList.todo.repository.TodoItemRepository;

@Service
public class TodoService {

    private static final Logger log = LoggerFactory.getLogger(TodoService.class);

    private final TodoItemRepository todoItemRepository;
    private final TodoKafkaProducer todoKafkaProducer;

    // Iniezione tramite costruttore per garantire che entrambi i bean siano disponibili
    public TodoService(TodoItemRepository todoItemRepository, TodoKafkaProducer todoKafkaProducer) {
        this.todoItemRepository = todoItemRepository;
        this.todoKafkaProducer = todoKafkaProducer;
        log.info(">>> Iniezione KafkaProducer completata: {}", todoKafkaProducer);
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

        log.info(">>> [closeTodo] Chiamato per todoId {}. Stato 'completed' PRIMA: {}", todoId, todo.getCompleted());

        // Per test, forziamo l'invio della notifica (rimuovi il controllo se funziona)
        todo.setCompleted(true);
        todoItemRepository.save(todo);
        log.info(">>> [closeTodo] To-Do segnata come completata.");

        log.info(">>> [closeTodo] Invio messaggio a Kafka...");
        todoKafkaProducer.sendTodoClosedNotification(todoId, todo.getTitle(), userEmail);
    }
}
