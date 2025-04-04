package com.todoList.todo.service;

import java.io.FileInputStream;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.SSLContext;

import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactory;
import org.apache.hc.core5.http.config.Registry;
import org.apache.hc.core5.http.config.RegistryBuilder;
import org.apache.hc.core5.ssl.SSLContexts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import jakarta.annotation.PostConstruct;

/**
 * Servizio per inviare notifiche Telegram.
 */
@Service
public class TelegramNotificationService {

    @Value("${telegram.bot.token}")
    private String botToken;

    @Value("${percorso.java.cacerts}")
    private String percorsoJava;

    private RestTemplate restTemplate;

    /**
     * Metodo di inizializzazione chiamato dopo la costruzione del bean.
     */
    @PostConstruct
    public void init() {
        this.restTemplate = createSecureRestTemplate();
    }

    /**
     * Crea un RestTemplate sicuro con configurazione SSL.
     */
    private RestTemplate createSecureRestTemplate() {
        try {
            // Carica il truststore usando il percorso specificato nella proprietà
            KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
            try (FileInputStream trustStoreStream = new FileInputStream(percorsoJava)) {
                trustStore.load(trustStoreStream, "changeit".toCharArray());
            }

            // Crea un SSLContext basato sul truststore
            SSLContext sslContext = SSLContexts.custom()
                    .loadTrustMaterial(trustStore, (X509Certificate[] chain, String authType) -> true)
                    .build();

            // Costruisci un SSLConnectionSocketFactory e un HttpClient con questo SSLContext
            @SuppressWarnings("deprecation")
            SSLConnectionSocketFactory sslSocketFactory = new SSLConnectionSocketFactory(sslContext);
            @SuppressWarnings("deprecation")
            Registry<org.apache.hc.client5.http.socket.ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<org.apache.hc.client5.http.socket.ConnectionSocketFactory>create()
                    .register("https", sslSocketFactory)
                    .build();

            @SuppressWarnings("deprecation")
            PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager(
                    socketFactoryRegistry);
            CloseableHttpClient httpClient = HttpClients.custom()
                    .setConnectionManager(connectionManager)
                    .build();

            HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory(
                    httpClient);
            return new RestTemplate(requestFactory);

        } catch (IOException | KeyManagementException | KeyStoreException | NoSuchAlgorithmException
                | CertificateException e) {
            throw new RuntimeException("Errore nella configurazione SSL per RestTemplate", e);
        }
    }

    /**
     * Invia una notifica Telegram.
     */
    @SuppressWarnings("CallToPrintStackTrace")
    public void sendNotification(String chatId, String message) {
        String url = "https://api.telegram.org/bot" + botToken + "/sendMessage";

        // Crea il body della richiesta con i parametri
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("chat_id", chatId);
        requestBody.put("text", message);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, String>> requestEntity = new HttpEntity<>(requestBody, headers);

        try {
            restTemplate.postForObject(url, requestEntity, String.class);
        } catch (RestClientException e) {
            e.printStackTrace();
        }
    }
}