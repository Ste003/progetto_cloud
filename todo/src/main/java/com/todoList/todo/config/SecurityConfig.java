package com.todoList.todo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(authorize -> authorize
                // Permetti l’accesso a /login, /error e a risorse statiche (se presenti)
                .requestMatchers("/", "/login", "/error").permitAll()
                // Tutte le altre richieste richiedono autenticazione
                .anyRequest().authenticated()
            )
            .oauth2Login(oauth2 -> oauth2
                // Puoi usare la pagina di login di default oppure personalizzata
                .loginPage("/login")
                .defaultSuccessUrl("/home", true)  // Redirect a /home dopo il login
            )
            .logout(logout -> logout
                .logoutSuccessUrl("/")
                .permitAll()
            );
        return http.build();
    }
}
