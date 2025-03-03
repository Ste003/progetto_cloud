package com.todoList.todo.controller;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.todoList.todo.dto.TodoItemDTO;
import com.todoList.todo.dto.TodoWithSubscribersDTO;
import com.todoList.todo.dto.UserDTO;
import com.todoList.todo.entities.User;
import com.todoList.todo.repository.UserRepository;

@RestController
@RequestMapping("/api/subscriptions")
public class SubscriptionController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping
    public ResponseEntity<List<TodoItemDTO>> getUserSubscriptions(@AuthenticationPrincipal OAuth2User principal) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        String email = principal.getAttribute("email");
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (!userOpt.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        User user = userOpt.get();
        List<TodoItemDTO> subscriptions = user.getSubscribedTodos().stream()
                .map(todo -> new TodoItemDTO(
                        todo.getId(), 
                        todo.getTitle(), 
                        todo.getCompleted(), 
                        (todo.getUser() != null ? todo.getUser().getEmail() : null),
                        false,
                        (todo.getCompletedBy() != null ? todo.getCompletedBy().getEmail() : null),
                        (todo.getCompletedBy() != null ? todo.getCompletedBy().getName() : null)
                ))
                .collect(Collectors.toList());
        return ResponseEntity.ok(subscriptions);
    }

    // Gli endpoint per TodoWithSubscribersDTO possono rimanere invariati se non necessitano il nuovo campo
    @GetMapping("/created-todos-subscribers")
    public ResponseEntity<List<TodoWithSubscribersDTO>> getCreatedTodosWithSubscribers(@AuthenticationPrincipal OAuth2User principal) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        String email = principal.getAttribute("email");
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (!userOpt.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        User user = userOpt.get();
        List<TodoWithSubscribersDTO> dtos = user.getTodoItems().stream().map(todo -> {
            List<UserDTO> subscribers = todo.getSubscribers().stream()
                    .map(sub -> new UserDTO(sub.getId(), sub.getName(), sub.getEmail(), null, null))
                    .collect(Collectors.toList());
            return new TodoWithSubscribersDTO(todo.getId(), todo.getTitle(), todo.getCompleted(), subscribers);
        }).collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }
    
    @GetMapping("/subscribed-todos-subscribers")
    public ResponseEntity<List<TodoWithSubscribersDTO>> getSubscribedTodosWithSubscribers(@AuthenticationPrincipal OAuth2User principal) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        String email = principal.getAttribute("email");
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (!userOpt.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        User user = userOpt.get();
        List<TodoWithSubscribersDTO> dtos = user.getSubscribedTodos().stream().map(todo -> {
            List<UserDTO> subscribers = todo.getSubscribers().stream()
                    .map(sub -> new UserDTO(sub.getId(), sub.getName(), sub.getEmail(), null, null))
                    .collect(Collectors.toList());
            return new TodoWithSubscribersDTO(todo.getId(), todo.getTitle(), todo.getCompleted(), subscribers);
        }).collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }
}
