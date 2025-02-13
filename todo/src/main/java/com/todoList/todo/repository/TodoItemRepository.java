package com.todoList.todo.repository;

import com.todoList.todo.entities.TodoItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TodoItemRepository extends JpaRepository<TodoItem, Long> {
    // eventuali metodi personalizzati se necessari
}