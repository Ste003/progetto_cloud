package com.todoList.todo.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
// import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.todoList.todo.dto.UserDTO;
import com.todoList.todo.service.UserService;

@RestController
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/users/{userId}/profile")
    public UserDTO getUserProfile(@PathVariable Long userId) {
        return userService.getUserProfile(userId);
    }

    // @GetMapping("/user")
    // public ResponseEntity<Map<String, Object>> getUser(Authentication authentication) {
    //     OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
    //     if (oAuth2User != null) {
    //         Map<String, Object> user = new HashMap<>();
    //         user.put("name", oAuth2User.getAttribute("name"));
    //         user.put("email", oAuth2User.getAttribute("email"));
    //         return ResponseEntity.ok(user);
    //     }
    //     return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
    // }

    // Se non serve restituire il profilo qui, rimuovi o rinomina questo metodo.
    @GetMapping("/user")
    public ResponseEntity<Map<String, Object>> getBasicUserInfo(@AuthenticationPrincipal OAuth2User principal) {
        if (principal != null) {
            Map<String, Object> user = new HashMap<>();
            user.put("name", principal.getAttribute("name"));
            user.put("email", principal.getAttribute("email"));
            return ResponseEntity.ok(user);
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
    }

}
