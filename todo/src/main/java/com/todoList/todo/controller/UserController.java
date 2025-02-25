package com.todoList.todo.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.todoList.todo.dto.TodoItemDTO;
import com.todoList.todo.dto.UserDTO;
import com.todoList.todo.entities.User;
import com.todoList.todo.repository.UserRepository;
import com.todoList.todo.service.UserService;

@RestController
public class UserController {

    private final UserService userService;

    @Autowired
    private UserRepository userRepository;

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

    @GetMapping("/subscriptions")
    public ResponseEntity<List<TodoItemDTO>> getSubscriptions(@AuthenticationPrincipal OAuth2User principal) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        String email = principal.getAttribute("email");
        User user = userRepository.findByEmail(email);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        List<TodoItemDTO> subscriptions = user.getSubscribedTodos().stream()
            .map(todo -> new TodoItemDTO(
                    todo.getId(), 
                    todo.getTitle(), 
                    todo.getCompleted(), 
                    todo.getUser() != null ? todo.getUser().getEmail() : null
            ))
            .collect(Collectors.toList());
        return ResponseEntity.ok(subscriptions);
    }

}
