package com.todoList.todo.dto;

import java.util.List;

public class UserDTO {
    private Long id;
    private String name;
    private String email;
    private List<TodoItemDTO> todoItems;
    private List<TodoItemDTO> subscribedTodos;

    // Costruttore vuoto
    public UserDTO() {}

    // Costruttore con tutti i parametri
    public UserDTO(Long id, String name, String email, List<TodoItemDTO> todoItems, List<TodoItemDTO> subscribedTodos) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.todoItems = todoItems;
        this.subscribedTodos = subscribedTodos;
    }

    // Getters e setters
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public List<TodoItemDTO> getTodoItems() {
        return todoItems;
    }
    public void setTodoItems(List<TodoItemDTO> todoItems) {
        this.todoItems = todoItems;
    }
    public List<TodoItemDTO> getSubscribedTodos() {
        return subscribedTodos;
    }
    public void setSubscribedTodos(List<TodoItemDTO> subscribedTodos) {
        this.subscribedTodos = subscribedTodos;
    }
}
