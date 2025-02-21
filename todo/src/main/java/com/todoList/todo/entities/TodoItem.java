package com.todoList.todo.entities;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "todo_item")
public class TodoItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private Boolean completed;

    // Proprietario della todo
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    // Gli utenti sottoscrittori
    @ManyToMany
    @JoinTable(
        name = "todo_item_subscribers",
        joinColumns = @JoinColumn(name = "todo_item_id"),
        inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private List<User> subscribers = new ArrayList<>();

    // Getters e setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public Boolean getCompleted() { return completed; }
    public void setCompleted(Boolean completed) { this.completed = completed; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public List<User> getSubscribers() { return subscribers; }
    public void setSubscribers(List<User> subscribers) { this.subscribers = subscribers; }
}
