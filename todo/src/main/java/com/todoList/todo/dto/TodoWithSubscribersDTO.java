package com.todoList.todo.dto;

import java.util.List;

public class TodoWithSubscribersDTO {
    private Long id;
    private String title;
    private Boolean completed;
    private List<UserDTO> subscribers;

    public TodoWithSubscribersDTO() {}

    public TodoWithSubscribersDTO(Long id, String title, Boolean completed, List<UserDTO> subscribers) {
        this.id = id;
        this.title = title;
        this.completed = completed;
        this.subscribers = subscribers;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public Boolean getCompleted() { return completed; }
    public void setCompleted(Boolean completed) { this.completed = completed; }

    public List<UserDTO> getSubscribers() { return subscribers; }
    public void setSubscribers(List<UserDTO> subscribers) { this.subscribers = subscribers; }
}
