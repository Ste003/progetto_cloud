package com.todoList.todo.entities;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

/**
 * Entità JPA che rappresenta un utente.
 */
@Entity
@Table(name = "users")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
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
    @ManyToMany
    @JoinTable(
            name = "todo_item_subscribers",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "todo_item_id")
    )
    private List<TodoItem> subscribedTodos = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    @JsonManagedReference
    private List<TodoItem> todoItems = new ArrayList<>();

    @Column(name = "telegram_chat_id")
    private String telegramChatId;

    public User() {
    }

    /**
     * Costruttore per inizializzare nome ed email.
     */
    public User(String name, String email) {
        this.name = name;
        this.email = email;
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

    public List<TodoItem> getCreatedTodos() { 
        return createdTodos; 
    }

    public void setCreatedTodos(List<TodoItem> createdTodos) { 
        this.createdTodos = createdTodos; 
    }

    public List<TodoItem> getSubscribedTodos() { 
        return subscribedTodos; 
    }

    public void setSubscribedTodos(List<TodoItem> subscribedTodos) { 
        this.subscribedTodos = subscribedTodos; 
    }

    public List<TodoItem> getTodoItems() { 
        return this.todoItems; 
    }

    public void setTodoItems(List<TodoItem> todoItems) { 
        this.todoItems = todoItems; 
    }

    public String getTelegramChatId() { 
        return telegramChatId; 
    }

    public void setTelegramChatId(String telegramChatId) { 
        this.telegramChatId = telegramChatId; 
    }

}