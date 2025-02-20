package com.todoList.todo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.todoList.todo.entities.TodoItem;

public interface TodoItemRepository extends JpaRepository<TodoItem, Long> {
    // eventuali metodi, se necessari
}
