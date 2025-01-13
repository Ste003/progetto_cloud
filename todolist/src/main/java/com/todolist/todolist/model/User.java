package com.todolist.todolist.model;

import jakarta.persistence.*;
import java.util.Set;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "username", nullable = false, unique = true)
    private String username;

    //@Column(name = "chat_id", nullable = false, unique = true)
    //private String chatId; // ID del bot Telegram per comunicare con l'utente

    @Column(name = "password", nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private Role role;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private Set<ToDo> todos; // Attività create dall'utente

    @ManyToMany(mappedBy = "subscribers")
    private Set<ToDo> subscribedToDos; // Attività a cui l'utente è sottoscritto

    public User() {
    }

    // Getter e Setter
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    //public String getChatId() {
    //    return chatId;
    //}

    //public void setChatId(String chatId) {
    //    this.chatId = chatId;
    //}

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public Set<ToDo> getTodos() {
        return todos;
    }

    public void setTodos(Set<ToDo> todos) {
        this.todos = todos;
    }

    public Set<ToDo> getSubscribedToDos() {
        return subscribedToDos;
    }

    public void setSubscribedToDos(Set<ToDo> subscribedToDos) {
        this.subscribedToDos = subscribedToDos;
    }

    // Enum per il ruolo dell'utente
    public enum Role {
        USER,
        ADMIN
    }
}