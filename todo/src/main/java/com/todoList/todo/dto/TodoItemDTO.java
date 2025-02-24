package com.todoList.todo.dto;

public class TodoItemDTO {
    
    private Long id;
    private String title;
    private Boolean completed;

    public TodoItemDTO() {}  // Costruttore vuoto

    public TodoItemDTO(Long id, String title, Boolean completed) {
        this.id = id;
        this.title = title;
        this.completed = completed;
    }

    // Getters e setters
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public Boolean getCompleted() {
        return completed;
    }
    public void setCompleted(Boolean completed) {
        this.completed = completed;
    }
}
