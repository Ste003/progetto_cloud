package com.todoList.todo.kafka;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class TodoKafkaConsumer {

    private static final Logger log = LoggerFactory.getLogger(TodoKafkaConsumer.class);
    private final ObjectMapper objectMapper = new ObjectMapper();

    @KafkaListener(topics = "todo-closed", groupId = "todo-notification-group")
    public void listen(ConsumerRecord<String, String> record) {
        String json = record.value();
        log.debug(">>> [Consumer] Messaggio grezzo ricevuto: {}", json);
        try {
            TodoClosedEvent event = objectMapper.readValue(json, TodoClosedEvent.class);
            log.info("====== Ricevuto Evento Kafka ======");
            log.info("To-Do ID: {}", event.getTodoId());
            log.info("Titolo: {}", event.getTitle());
            log.info("Utente: {}", event.getUserEmail());
            log.info("===================================");
        } catch (JsonProcessingException e) {
            log.error(">>> [Consumer] Errore parsing messaggio Kafka: {}", e.getMessage());
        }
    }
}
