package com.todoList.todo.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.todoList.todo.dto.TodoItemDTO;
import com.todoList.todo.dto.UserDTO;
import com.todoList.todo.entities.User;
import com.todoList.todo.repository.UserRepository;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // Recupera un utente tramite email
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }   

    // Crea un nuovo utente nel database
    public User createUser(User user) {
        return userRepository.save(user);
    }
    public UserDTO getUserProfile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<TodoItemDTO> createdTodos = user.getCreatedTodos().stream()
                .map(todo -> new TodoItemDTO(todo.getId(), todo.getTitle(), todo.getCompleted()))
                .collect(Collectors.toList());

        List<TodoItemDTO> subscribedTodos = user.getSubscribedTodos().stream()
                .map(todo -> new TodoItemDTO(todo.getId(), todo.getTitle(), todo.getCompleted()))
                .collect(Collectors.toList());
                
        return new UserDTO(user.getId(), user.getName(), user.getEmail(), createdTodos, subscribedTodos);

    }
}
