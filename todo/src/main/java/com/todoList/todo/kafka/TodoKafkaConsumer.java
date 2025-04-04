package com.todoList.todo.kafka;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Servizio per consumare messaggi Kafka relativi alle To-Do.
 */
@Service
public class TodoKafkaConsumer {

    private static final Logger log = LoggerFactory.getLogger(TodoKafkaConsumer.class); // Logger per registrare le informazioni di debug
    private final ObjectMapper objectMapper = new ObjectMapper(); // Mapper per convertire JSON in oggetti Java

    /**
     * Listener per il topic "todo-closed" nel gruppo "todo-notification-group".
     * Viene chiamato quando un messaggio è pubblicato su questo topic.
     */
    @KafkaListener(topics = "todo-closed", groupId = "todo-notification-group")
    public void listen(ConsumerRecord<String, String> record) {
        String json = record.value();
        log.debug(">>> [Consumer] Messaggio grezzo ricevuto: {}", json); // Log del messaggio grezzo ricevuto
        try {
            TodoClosedEvent event = objectMapper.readValue(json, TodoClosedEvent.class); // Converte il JSON in un oggetto TodoClosedEvent
            log.info("====== Ricevuto Evento Kafka ======");
            log.info("To-Do ID: {}", event.getTodoId());
            log.info("Titolo: {}", event.getTitle());
            log.info("Utente: {}", event.getUserEmail());
            log.info("==================================="); // Log dei dettagli dell'evento ricevuto
        } catch (JsonProcessingException e) {
            log.error(">>> [Consumer] Errore parsing messaggio Kafka: {}", e.getMessage()); // Log dell'errore di parsing
        }
    }
}