package com.todoList.todo.config;

import com.todoList.todo.entities.User;
import com.todoList.todo.service.UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import java.util.List;
import java.util.Optional;

/**
 * Configurazione di sicurezza per l'applicazione.
 */
@Configuration
public class SecurityConfig {

    private final UserService userService;

    /**
     * Costruttore per iniezione delle dipendenze.
     */
    public SecurityConfig(UserService userService) {
        this.userService = userService;
    }

    /**
     * Configura la catena di filtri di sicurezza.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // Configurazione CORS
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            // Disabilita CSRF per facilitare i test (valuta se abilitarlo in produzione)
            .csrf(csrf -> csrf.disable())
            // Definizione degli endpoint pubblici e protetti
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/", "/login", "/error", "/telegram/webhook").permitAll()
                .anyRequest().authenticated()
            )
            // Configurazione OAuth2 per il login
            .oauth2Login(oauth2 -> oauth2
                .defaultSuccessUrl("http://localhost:5173/home", true)
                .successHandler(customAuthenticationSuccessHandler())
                .loginPage("/login")
            )
            // Configurazione del logout
            .logout(logout -> logout
                .logoutUrl("/logout")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .logoutSuccessHandler((HttpServletRequest request, HttpServletResponse response, Authentication authentication) -> {
                    response.setStatus(HttpServletResponse.SC_NO_CONTENT);
                })
            );
        return http.build();
    }

    /**
     * Configura le impostazioni CORS.
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        // Permetti il frontend in esecuzione su localhost:5173
        configuration.setAllowedOrigins(List.of("http://localhost:5173"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("Authorization", "Content-Type"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    /**
     * Gestore di successo dell'autenticazione personalizzato.
     */
    @Bean
    public AuthenticationSuccessHandler customAuthenticationSuccessHandler() {
        return (HttpServletRequest request, HttpServletResponse response, Authentication authentication) -> {
            OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
            String email = oAuth2User.getAttribute("email");
            Optional<User> existingUserOpt = userService.getUserByEmail(email);
            if (existingUserOpt.isEmpty()) {
                String name = oAuth2User.getAttribute("name");
                User newUser = new User(name, email);
                userService.createUser(newUser);
            }
            response.sendRedirect("http://localhost:5173/home");
        };
    }
}