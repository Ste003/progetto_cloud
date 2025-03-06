package com.todoList.todo.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class TelegramNotificationService {

    @Value("${telegram.bot.token}")
    private String botToken;

    private final RestTemplate restTemplate = new RestTemplate();

    @SuppressWarnings("CallToPrintStackTrace")
    public void sendNotification(String chatId, String message) {
        String url = "https://api.telegram.org/bot" + botToken + "/sendMessage";
        @SuppressWarnings("deprecation")
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url)
                .queryParam("chat_id", chatId)
                .queryParam("text", message);
        try {
            restTemplate.getForObject(builder.toUriString(), String.class);
        } catch (RestClientException e) {
            // Log dell'errore
            e.printStackTrace();
        }
    }
}
