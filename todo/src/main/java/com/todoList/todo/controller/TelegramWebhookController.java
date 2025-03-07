package com.todoList.todo.controller;

import com.todoList.todo.entities.User;
import com.todoList.todo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/telegram")
public class TelegramWebhookController {

    private static final Logger logger = LoggerFactory.getLogger(ProfileController.class);
    
    @Autowired
    private UserRepository userRepository;

    /**
     * Endpoint per ricevere gli aggiornamenti da Telegram. Telegram invia un
     * JSON con il messaggio. Ci aspettiamo che l'utente invii un comando
     * "/start <email>".
     */
    @PostMapping("/webhook")
    public ResponseEntity<String> receiveUpdate(@RequestBody Map<String, Object> update) {
        logger.info("Update ricevuto: {}", update);
        if (update.containsKey("message")) {
            @SuppressWarnings("unchecked")
            Map<String, Object> message = (Map<String, Object>) update.get("message");
            String text = (String) message.get("text");
            logger.info("Testo del messaggio: {}", text);
            if (text != null && text.startsWith("/start")) {
                // Non rimuovere le occorrenze di "@" per non alterare l'email
                String[] parts = text.split(" ");
                if (parts.length > 1) {
                    String email = parts[1].trim();
                    logger.info("Email estratta: {}", email);
                    @SuppressWarnings("unchecked")
                    Map<String, Object> chat = (Map<String, Object>) message.get("chat");
                    String chatId = String.valueOf(chat.get("id"));
                    logger.info("Chat ID estratto: {}", chatId);

                    Optional<User> userOpt = userRepository.findByEmail(email);
                    if (userOpt.isPresent()) {
                        User user = userOpt.get();
                        user.setTelegramChatId(chatId);
                        userRepository.save(user);
                        return ResponseEntity.ok("Chat id registrato per l'utente: " + email);
                    } else {
                        return ResponseEntity.ok("Utente non trovato per email: " + email);
                    }
                } else {
                    return ResponseEntity.ok("Comando non nel formato atteso. Usa: /start <email>");
                }
            }
        }
        return ResponseEntity.ok("Update ricevuto");
    }

}
