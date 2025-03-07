package com.todoList.todo.service;

import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.config.Registry;
import org.apache.hc.core5.http.config.RegistryBuilder;
import org.apache.hc.core5.ssl.SSLContexts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import jakarta.annotation.PostConstruct;

import javax.net.ssl.SSLContext;

import java.security.KeyStore;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.client5.http.socket.ConnectionSocketFactory;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactory;

@Service
public class TelegramNotificationService {

    @Value("${telegram.bot.token}")
    private String botToken;

    private RestTemplate restTemplate;

    @PostConstruct
    public void init() {
        this.restTemplate = createSecureRestTemplate();
    }

    private RestTemplate createSecureRestTemplate() {
        try {
            // Carica il truststore contenente il certificato importato
            KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
            try (FileInputStream trustStoreStream = new FileInputStream("C:/Program Files/Java/jdk-23/lib/security/cacerts")) {
                trustStore.load(trustStoreStream, "changeit".toCharArray());
            }

            // Crea un SSLContext basato sul truststore
            SSLContext sslContext = SSLContexts.custom()
                    .loadTrustMaterial(trustStore, (X509Certificate[] chain, String authType) -> true)
                    .build();

            // Crea un SSLConnectionSocketFactory con l'SSLContext
            @SuppressWarnings("deprecation")
            SSLConnectionSocketFactory sslSocketFactory = new SSLConnectionSocketFactory(sslContext);

            // Crea un registry e registra lo SSLConnectionSocketFactory
            @SuppressWarnings("deprecation")
            Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
                    .register("https", sslSocketFactory)
                    .build();

            // Crea un HttpClient con SSL configurato
            @SuppressWarnings("deprecation")
            CloseableHttpClient httpClient = HttpClients.custom()
                    .setConnectionManager(new PoolingHttpClientConnectionManager(socketFactoryRegistry))
                    .build();

            HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory(httpClient);
            return new RestTemplate(requestFactory);

        } catch (IOException | KeyManagementException | KeyStoreException | NoSuchAlgorithmException | CertificateException e) {
            throw new RuntimeException("Errore nella configurazione SSL per RestTemplate", e);
        }
    }

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
            e.printStackTrace();
        }
    }
}
