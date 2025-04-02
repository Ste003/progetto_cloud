package com.todoList.todo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Configurazione CORS per l'applicazione.
 */
@Configuration
public class CorsConfig {

    /**
     * Configura le impostazioni CORS.
     */
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(@SuppressWarnings("null") CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedOrigins("http://localhost:5173") // URL del frontend
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH") // Metodi permessi
                        .allowCredentials(true) // Permette di inviare i cookie (ad esempio il token di sessione)
                        .allowedHeaders("*");  // Permetti tutti gli header
            }
        };
    }
}