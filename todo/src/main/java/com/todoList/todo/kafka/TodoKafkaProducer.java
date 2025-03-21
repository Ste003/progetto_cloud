package com.todoList.todo.kafka;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class TodoKafkaProducer {
    @Value("${kafka.topic.todo-closed}")
    private String TOPIC;

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public TodoKafkaProducer(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendTodoClosedNotification(Long todoId, String title, String userEmail) {
        try {
            TodoClosedEvent event = new TodoClosedEvent(todoId, title, userEmail);
            String message = objectMapper.writeValueAsString(event);
            System.out.println(">>> [Producer] Invio notifica Kafka: " + message);
            kafkaTemplate.send(TOPIC, message);
            // Per forzare l'invio, puoi anche chiamare kafkaTemplate.flush();
        } catch (JsonProcessingException e) {
            System.err.println(">>> [Producer] Errore serializzazione evento: " + e.getMessage());
        }
    }
}
