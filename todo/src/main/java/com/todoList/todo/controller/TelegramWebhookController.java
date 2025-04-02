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
@RequestMapping("/telegram") // Mappa il controller sull'endpoint /telegram
public class TelegramWebhookController {

    private static final Logger logger = LoggerFactory.getLogger(ProfileController.class); // Logger per registrare le informazioni di debug
    
    @Autowired
    private UserRepository userRepository; // Repository per accedere ai dati degli utenti

    /**
     * Endpoint per ricevere gli aggiornamenti da Telegram. Telegram invia un
     * JSON con il messaggio. Ci aspettiamo che l'utente invii un comando
     * "/start <email>".
     */
    @PostMapping("/webhook")
    public ResponseEntity<String> receiveUpdate(@RequestBody Map<String, Object> update) {
        logger.info("Update ricevuto: {}", update); // Log dell'aggiornamento ricevuto
        if (update.containsKey("message")) {
            @SuppressWarnings("unchecked")
            Map<String, Object> message = (Map<String, Object>) update.get("message");
            String text = (String) message.get("text");
            logger.info("Testo del messaggio: {}", text); // Log del testo del messaggio
            if (text != null && text.startsWith("/start")) {
                // Non rimuovere le occorrenze di "@" per non alterare l'email
                String[] parts = text.split(" ");
                if (parts.length > 1) {
                    String email = parts[1].trim();
                    logger.info("Email estratta: {}", email); // Log dell'email estratta
                    @SuppressWarnings("unchecked")
                    Map<String, Object> chat = (Map<String, Object>) message.get("chat");
                    String chatId = String.valueOf(chat.get("id"));
                    logger.info("Chat ID estratto: {}", chatId); // Log del chat ID estratto

                    Optional<User> userOpt = userRepository.findByEmail(email);
                    if (userOpt.isPresent()) {
                        User user = userOpt.get();
                        user.setTelegramChatId(chatId); // Imposta il chat ID di Telegram per l'utente
                        userRepository.save(user); // Salva l'utente aggiornato nel repository
                        return ResponseEntity.ok("Chat id registrato per l'utente: " + email); // Risposta di successo
                    } else {
                        return ResponseEntity.ok("Utente non trovato per email: " + email); // Risposta se l'utente non è trovato
                    }
                } else {
                    return ResponseEntity.ok("Comando non nel formato atteso. Usa: /start <email>"); // Risposta se il comando non è nel formato atteso
                }
            }
        }
        return ResponseEntity.ok("Update ricevuto"); // Risposta generica per gli aggiornamenti ricevuti
    }

}