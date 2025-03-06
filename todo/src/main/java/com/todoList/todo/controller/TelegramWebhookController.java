package com.todoList.todo.controller;

import com.todoList.todo.entities.User;
import com.todoList.todo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/telegram")
public class TelegramWebhookController {

    @Autowired
    private UserRepository userRepository;

    /**
     * Endpoint per ricevere gli aggiornamenti da Telegram.
     * Telegram invia un JSON con il messaggio.
     * Ci aspettiamo che l'utente invii un comando "/start <email>".
     */
    @PostMapping("/webhook")
    public ResponseEntity<String> receiveUpdate(@RequestBody Map<String, Object> update) {
        // Controlla se l'update contiene un campo "message"
        if (update.containsKey("message")) {
            @SuppressWarnings("unchecked")
            Map<String, Object> message = (Map<String, Object>) update.get("message");
            String text = (String) message.get("text");
            if (text != null && text.startsWith("/start")) {
                // Aspettiamo il comando nel formato: "/start user@example.com"
                String[] parts = text.split(" ");
                if (parts.length > 1) {
                    String email = parts[1].trim();
                    // Estrai il chat id dal messaggio
                    @SuppressWarnings("unchecked")
                    Map<String, Object> chat = (Map<String, Object>) message.get("chat");
                    String chatId = String.valueOf(chat.get("id"));
                    
                    // Cerca l'utente per email
                    Optional<User> userOpt = userRepository.findByEmail(email);
                    if (userOpt.isPresent()) {
                        User user = userOpt.get();
                        // Aggiorna il campo telegramChatId
                        user.setTelegramChatId(chatId);
                        userRepository.save(user);
                        return ResponseEntity.ok("Chat id registrato per l'utente: " + email);
                    } else {
                        return ResponseEntity.ok("Utente non trovato per email: " + email);
                    }
                }
            }
        }
        return ResponseEntity.ok("Update ricevuto");
    }
}
