package com.todoList.todo.dto;

public class TodoItemDTO {
    private Long id;
    private String title;
    private Boolean completed;
    private String creatorEmail; // Nuovo campo per l'email del creatore

    public TodoItemDTO() {}

    public TodoItemDTO(Long id, String title, Boolean completed, String creatorEmail) {
        this.id = id;
        this.title = title;
        this.completed = completed;
        this.creatorEmail = creatorEmail;
    }

    // Aggiungi un costruttore a 3 parametri per quando un utente si sottoscrive ad una todo --> non serve l'email
    public TodoItemDTO(Long id, String title, Boolean completed) {
        this.id = id;
        this.title = title;
        this.completed = completed;
        this.creatorEmail = null;
    }

    // Getters e Setters
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
    public String getCreatorEmail() {
        return creatorEmail;
    }
    public void setCreatorEmail(String creatorEmail) {
        this.creatorEmail = creatorEmail;
    }
}
