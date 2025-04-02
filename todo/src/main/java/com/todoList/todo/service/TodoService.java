package com.todoList.todo.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.todoList.todo.entities.TodoItem;
import com.todoList.todo.kafka.TodoKafkaProducer;
import com.todoList.todo.repository.TodoItemRepository;

/**
 * Servizio per la gestione delle To-Do.
 */
@Service
public class TodoService {

    private static final Logger log = LoggerFactory.getLogger(TodoService.class); // Logger per registrare le informazioni di debug

    private final TodoItemRepository todoItemRepository;
    private final TodoKafkaProducer todoKafkaProducer;

    /**
     * Iniezione tramite costruttore per garantire che entrambi i bean siano disponibili.
     */
    public TodoService(TodoItemRepository todoItemRepository, TodoKafkaProducer todoKafkaProducer) {
        this.todoItemRepository = todoItemRepository;
        this.todoKafkaProducer = todoKafkaProducer;
        log.info(">>> Iniezione KafkaProducer completata: {}", todoKafkaProducer); // Log dell'iniezione completata
    }

    /**
     * Recupera tutte le To-Do.
     */
    public List<TodoItem> getAllTodos() {
        return todoItemRepository.findAll();
    }

    /**
     * Crea una nuova To-Do.
     */
    public TodoItem createTodo(TodoItem todoItem) {
        return todoItemRepository.save(todoItem);
    }

    /**
     * Chiude una To-Do e invia una notifica Kafka.
     */
    @Transactional
    public void closeTodo(Long todoId, String userEmail) {
        TodoItem todo = todoItemRepository.findById(todoId)
                .orElseThrow(() -> new RuntimeException("To-Do non trovata")); // Eccezione se la To-Do non è trovata

        log.info(">>> [closeTodo] Chiamato per todoId {}. Stato 'completed' PRIMA: {}", todoId, todo.getCompleted()); // Log dello stato prima della chiusura

        log.info(">>> [closeTodo] Invio messaggio a Kafka...");
        todoKafkaProducer.sendTodoClosedNotification(todoId, todo.getTitle(), userEmail); // Invio della notifica Kafka
        log.info(">>> [closeTodo] Messaggio Kafka inviato correttamente."); // Log del successo dell'invio

        // Aggiorna lo stato della To-Do
        todo.setCompleted(true);
        todoItemRepository.save(todo); // Salva la To-Do aggiornata nel repository
        log.info(">>> [closeTodo] To-Do segnata come completata."); // Log della chiusura della To-Do
    }

}