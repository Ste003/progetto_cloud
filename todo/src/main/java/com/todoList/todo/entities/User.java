package com.todoList.todo.entities;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String name;
    private String email;

    // To‑do create dall’utente
    @OneToMany(mappedBy = "user")
    private List<TodoItem> createdTodos = new ArrayList<>();

    // To‑do a cui l’utente è iscritto
    @ManyToMany(mappedBy = "subscribers")
    private List<TodoItem> subscribedTodos = new ArrayList<>();

    public User() {}

    public User(String name, String email) {
        this.name = name;
        this.email = email;
    }

    // Getters e setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public List<TodoItem> getCreatedTodos() { return createdTodos; }
    public void setCreatedTodos(List<TodoItem> createdTodos) { this.createdTodos = createdTodos; }

    public List<TodoItem> getSubscribedTodos() { return subscribedTodos; }
    public void setSubscribedTodos(List<TodoItem> subscribedTodos) { this.subscribedTodos = subscribedTodos; }
}
