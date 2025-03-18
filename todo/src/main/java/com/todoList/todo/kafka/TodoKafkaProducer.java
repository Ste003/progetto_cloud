package com.todoList.todo.kafka;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class TodoKafkaProducer {
    private final KafkaTemplate<String, String> kafkaTemplate;
    private static final String TOPIC = "todo-closed";

    public TodoKafkaProducer(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendTodoClosedNotification(Long todoId, String title, String userEmail) {
        String message = "To-Do chiusa: " + title + " (ID: " + todoId + ") per utente: " + userEmail;
        kafkaTemplate.send(TOPIC, message);
    }
}

