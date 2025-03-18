package com.todoList.todo.kafka;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class TodoKafkaConsumer {

    @KafkaListener(topics = "todo-closed", groupId = "todo-notification-group")
    public void listen(ConsumerRecord<String, String> record) {
        System.out.println("Ricevuta notifica Kafka: " + record.value());
        // Qui si può aggiungere logica per inoltrare la notifica (es. WebSocket, Telegram, ecc.)
    }
}

