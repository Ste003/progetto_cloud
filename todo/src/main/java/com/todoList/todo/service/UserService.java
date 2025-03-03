package com.todoList.todo.service;

import java.util.List;
import java.util.Optional;
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

    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }   

    public User createUser(User user) {
        return userRepository.save(user);
    }
    
    public UserDTO getUserProfile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<TodoItemDTO> createdTodos = user.getCreatedTodos().stream()
                .map(todo -> new TodoItemDTO(
                        todo.getId(),
                        todo.getTitle(),
                        todo.getCompleted(),
                        null,
                        false,
                        (todo.getCompletedBy() != null ? todo.getCompletedBy().getEmail() : null),
                        (todo.getCompletedBy() != null ? todo.getCompletedBy().getName() : null)
                ))
                .collect(Collectors.toList());

        List<TodoItemDTO> subscribedTodos = user.getSubscribedTodos().stream()
                .map(todo -> new TodoItemDTO(
                        todo.getId(),
                        todo.getTitle(),
                        todo.getCompleted(),
                        null,
                        false,
                        (todo.getCompletedBy() != null ? todo.getCompletedBy().getEmail() : null),
                        (todo.getCompletedBy() != null ? todo.getCompletedBy().getName() : null)
                ))
                .collect(Collectors.toList());
                
        return new UserDTO(user.getId(), user.getName(), user.getEmail(), createdTodos, subscribedTodos);
    }
}
