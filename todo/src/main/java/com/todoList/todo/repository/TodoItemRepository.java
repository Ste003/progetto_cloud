package com.todoList.todo.repository;

import com.todoList.todo.entities.TodoItem;
import com.todoList.todo.entities.User;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TodoItemRepository extends JpaRepository<TodoItem, Long> {

    // Per la Home: restituisce tutte le todo non completate
    List<TodoItem> findByCompletedFalse();

    // Per il profilo: restituisce tutte le todo completate create dall'utente (proprietario)
    List<TodoItem> findByUserIdAndCompletedTrue(Long userId);

    List<TodoItem> findByUser(User user);
}
