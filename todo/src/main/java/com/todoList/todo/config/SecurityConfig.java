package com.todoList.todo.config;

import java.io.IOException;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import com.todoList.todo.entities.User;
import com.todoList.todo.service.UserService;

@Configuration
public class SecurityConfig {

    private final UserService userService;

    public SecurityConfig(UserService userService) {
        this.userService = userService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(authorize -> authorize
                .requestMatchers("/", "/login", "/error").permitAll()
                .anyRequest().authenticated()
            )
            .oauth2Login(oauth2 -> oauth2
                .loginPage("/login")
                .successHandler(customAuthenticationSuccessHandler())
            )
            .logout(logout -> logout
                .logoutSuccessUrl("/")
                .permitAll()
            );
        return http.build();
    }

    @Bean
    @SuppressWarnings("CallToPrintStackTrace")
    public AuthenticationSuccessHandler customAuthenticationSuccessHandler() {
        return (var request, var response, var authentication) -> {
            try {
                OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
                // Debug: stampa tutti gli attributi ricevuti
                System.out.println("OAuth2User attributes: " + oAuth2User.getAttributes());

                String email = oAuth2User.getAttribute("email");
                if (email == null) {
                    throw new IllegalStateException("L'attributo 'email' è mancante nei dati di OAuth2User");
                }

                if (userService.getUserByEmail(email) == null) {
                    String name = oAuth2User.getAttribute("name");
                    User newUser = new User(name, email);
                    userService.createUser(newUser);
                    System.out.println("Created new user: " + email);
                } else {
                    System.out.println("User already exists: " + email);
                }
                
                // Redirect verso il frontend (usa URL assoluto se necessario)
                response.sendRedirect("http://localhost:5173/home");
            } catch (IOException | IllegalStateException e) {
                e.printStackTrace();
                // Rilancia l'eccezione per mostrare la pagina di errore
                throw e;
            }
        };
    }
}
