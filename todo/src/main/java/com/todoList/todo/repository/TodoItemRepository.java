package com.todoList.todo.repository;

import com.todoList.todo.entities.TodoItem;
import com.todoList.todo.entities.User;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

/**
 * Repository per accedere ai dati delle To-Do.
 */
public interface TodoItemRepository extends JpaRepository<TodoItem, Long> {

    /**
     * Per la Home: restituisce tutte le To-Do non completate.
     */
    List<TodoItem> findByCompletedFalse();

    /**
     * Per il profilo: restituisce tutte le To-Do completate create dall'utente (proprietario).
     */
    List<TodoItem> findByUserIdAndCompletedTrue(Long userId);

    /**
     * Restituisce tutte le To-Do create dall'utente.
     */
    List<TodoItem> findByUser(User user);
}