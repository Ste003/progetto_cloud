package com.todoList.todo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(authorize -> authorize
                // Permetti l'accesso a queste URL (modifica se necessario)
                .requestMatchers("/", "/login", "/error").permitAll()
                // Tutte le altre richieste richiedono autenticazione
                .anyRequest().authenticated()
            )
            .oauth2Login(oauth2 -> oauth2
                // Puoi usare la pagina di login di default (o crearne una personalizzata)
                .loginPage("/login")
                // Dopo un login riuscito, reindirizza l'utente al frontend home page
                .successHandler(customAuthenticationSuccessHandler())
            )
            .logout(logout -> logout
                .logoutSuccessUrl("/")
                .permitAll()
            );
        return http.build();
    }

    @Bean
    public AuthenticationSuccessHandler customAuthenticationSuccessHandler() {
        return (HttpServletRequest request, HttpServletResponse response, org.springframework.security.core.Authentication authentication) -> {
            // Dopo il successo, reindirizza al frontend (pagina home)
            response.sendRedirect("http://localhost:5173/home");
        };
    }
}
