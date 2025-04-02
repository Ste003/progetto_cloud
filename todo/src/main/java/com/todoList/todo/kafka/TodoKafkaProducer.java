package com.todoList.todo.kafka;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Servizio per produrre messaggi Kafka relativi alle To-Do.
 */
@Service
public class TodoKafkaProducer {
    @Value("${spring.kafka.consumer.group-id}")
    private String groupId;
    
    @Value("${kafka.topic.todo.closed}")
    private String topic; // Nota: il nome della proprietà è letto in "topic"

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper(); // Mapper per convertire oggetti Java in JSON

    /**
     * Costruttore per iniezione delle dipendenze.
     */
    public TodoKafkaProducer(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    /**
     * Metodo per inviare una notifica di chiusura di una To-Do.
     */
    public void sendTodoClosedNotification(Long todoId, String title, String userEmail) {
        try {
            TodoClosedEvent event = new TodoClosedEvent(todoId, title, userEmail); // Crea un evento TodoClosedEvent
            String message = objectMapper.writeValueAsString(event); // Converte l'evento in una stringa JSON
            System.out.println(">>> [Producer] Invio notifica Kafka: " + message); // Log del messaggio inviato
            kafkaTemplate.send(topic, groupId, message)
                    .whenComplete((result, ex) -> {
                        if (ex != null) {
                            System.err.println(">>> [Producer] Errore nell'invio del messaggio: " + ex.getMessage()); // Log dell'errore di invio
                        } else {
                            System.out.println(">>> [Producer] Messaggio inviato con successo!"); // Log del successo dell'invio
                        }
                    });
            kafkaTemplate.flush(); // Flush del KafkaTemplate per assicurarsi che il messaggio sia inviato

        } catch (JsonProcessingException e) {
            System.err.println(">>> [Producer] Errore serializzazione evento: " + e.getMessage()); // Log dell'errore di serializzazione
        }
    }
}