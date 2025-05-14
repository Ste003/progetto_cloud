package com.todoList.todo.config;

import com.todoList.todo.entities.User;
import com.todoList.todo.service.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.cors.CorsConfiguration;

import java.io.IOException;
import java.util.*;

@Configuration
public class SecurityConfig {

    private final UserService userService;

    public SecurityConfig(UserService userService) {
        this.userService = userService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/login", "/error", "/telegram/webhook").permitAll()
                        .anyRequest().authenticated())
                .oauth2Login(oauth2 -> oauth2
                        .defaultSuccessUrl("http://localhost:5173/home", true)
                        .successHandler(customAuthenticationSuccessHandler())
                        .loginPage("/login"))
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                        .logoutSuccessHandler((request, response, authentication) -> response
                                .setStatus(HttpServletResponse.SC_NO_CONTENT)));

        // Aggiunge il filtro dopo l'autenticazione username/password
        http.addFilterAfter(sameSiteCookieFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public OncePerRequestFilter sameSiteCookieFilter() {
        return new OncePerRequestFilter() {
            @Override
            protected void doFilterInternal(HttpServletRequest request,
                    HttpServletResponse response,
                    FilterChain filterChain)
                    throws ServletException, IOException {

                filterChain.doFilter(request, response);

                Collection<String> headers = response.getHeaders("Set-Cookie");
                if (headers.isEmpty()) {
                    return;
                }

                // boolean isSecure = request.isSecure()
                //         || "https".equalsIgnoreCase(request.getHeader("X-Forwarded-Proto"));
                boolean isProduction = !request.getServerName().contains("localhost");
                boolean isSecure = isProduction
                    || request.isSecure()
                    || "https".equalsIgnoreCase(request.getHeader("X-Forwarded-Proto"));


                List<String> modified = new ArrayList<>();
                for (String header : headers) {
                    if (header.toLowerCase().startsWith("jsessionid")) {
                        modified.add(header + (isSecure
                                ? "; SameSite=None; Secure"
                                : "; SameSite=Lax"));
                    } else {
                        modified.add(header);
                    }
                }

                response.setHeader("Set-Cookie", String.join(",", modified));
            }

        };
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of(
                "http://localhost:5173",
                "https://tuo-dominio.com" // sostituisci con l'URL del tuo cloud
        ));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        configuration.setAllowedHeaders(List.of("Authorization", "Content-Type", "X-Requested-With"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public AuthenticationSuccessHandler customAuthenticationSuccessHandler() {
        return (request, response, authentication) -> {
            OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
            String email = oAuth2User.getAttribute("email");

            userService.getUserByEmail(email).orElseGet(() -> {
                String name = oAuth2User.getAttribute("name");
                return userService.createUser(new User(name, email));
            });

            // redirect post-login
            response.sendRedirect("http://localhost:5173/home");
        };
    }
}
