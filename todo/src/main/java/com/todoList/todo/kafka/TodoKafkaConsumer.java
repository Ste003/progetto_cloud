package com.todoList.todo.kafka;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class TodoKafkaConsumer {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @KafkaListener(topics = { "todo-closed" }, groupId = "todo-notification-group")
    public void listen(ConsumerRecord<String, String> record) {
        try {
            String json = record.value();
            TodoClosedEvent event = objectMapper.readValue(json, TodoClosedEvent.class);

            System.out.println("====== Ricevuto Evento Kafka ======");
            System.out.println("To-Do ID: " + event.getTodoId());
            System.out.println("Titolo: " + event.getTitle());
            System.out.println("Utente: " + event.getUserEmail());
            System.out.println("===================================");
        } catch (JsonProcessingException e) {
            System.err.println(">>> [Consumer] Errore parsing messaggio Kafka: " + e.getMessage());
        }
    }
}
