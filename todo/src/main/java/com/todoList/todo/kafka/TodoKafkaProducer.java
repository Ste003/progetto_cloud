package com.todoList.todo.kafka;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class TodoKafkaProducer {
    @Value("${spring.kafka.consumer.group-id}")
    private String groupId;
    
    @Value("${kafka.topic.todo.closed}")
    private String topic; // Nota: il nome della proprietà è letto in "topic"

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
            kafkaTemplate.send(topic, groupId, message)
                    .whenComplete((result, ex) -> {
                        if (ex != null) {
                            System.err.println(">>> [Producer] Errore nell'invio del messaggio: " + ex.getMessage());
                        } else {
                            System.out.println(">>> [Producer] Messaggio inviato con successo!");
                        }
                    });
            kafkaTemplate.flush();

            kafkaTemplate.flush();
        } catch (JsonProcessingException e) {
            System.err.println(">>> [Producer] Errore serializzazione evento: " + e.getMessage());
        }
    }
}
