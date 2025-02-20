package com.todoList.todo.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

    @GetMapping("/user")
    public String getUserInfo(Authentication authentication) {
        if (authentication != null) {
            OAuth2User user = (OAuth2User) authentication.getPrincipal();
            return "Ciao, " + user.getAttribute("name") + " (" + user.getAttribute("email") + ")";
        }
        return "Utente non autenticato";
    }
}
