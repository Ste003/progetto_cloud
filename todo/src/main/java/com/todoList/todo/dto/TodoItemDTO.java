package com.todoList.todo.dto;

public class TodoItemDTO {
    private Long id;
    private String title;
    private Boolean completed;
    private String creatorEmail; // Email del creatore della todo
    private Boolean subscribed;  // Indica se l'utente corrente è già iscritto
    private String completedByEmail; // Email dell'utente che ha completato la todo

    public TodoItemDTO() {}

    // Costruttore a 4 parametri (senza subscribed; imposta default false)
    public TodoItemDTO(Long id, String title, Boolean completed, String creatorEmail) {
        this.id = id;
        this.title = title;
        this.completed = completed;
        this.creatorEmail = creatorEmail;
        this.subscribed = false;
        this.completedByEmail = null;
    }

    // Costruttore a 3 parametri (senza email; non usato per la home)
    public TodoItemDTO(Long id, String title, Boolean completed) {
        this.id = id;
        this.title = title;
        this.completed = completed;
        this.creatorEmail = null;
        this.subscribed = false;
        this.completedByEmail = null;
    }

    // Costruttore a 5 parametri, per settare esplicitamente anche subscribed
    public TodoItemDTO(Long id, String title, Boolean completed, String creatorEmail, Boolean subscribed) {
        this.id = id;
        this.title = title;
        this.completed = completed;
        this.creatorEmail = creatorEmail;
        this.subscribed = subscribed;
        this.completedByEmail = null;
    }

    // Costruttore a 6 parametri, per settare anche completedByEmail
    public TodoItemDTO(Long id, String title, Boolean completed, String creatorEmail, Boolean subscribed, String completedByEmail) {
        this.id = id;
        this.title = title;
        this.completed = completed;
        this.creatorEmail = creatorEmail;
        this.subscribed = subscribed;
        this.completedByEmail = completedByEmail;
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

    public String getCreatorEmail() { 
        return creatorEmail; 
    }
    public void setCreatorEmail(String creatorEmail) { 
        this.creatorEmail = creatorEmail; 
    }

    public Boolean getSubscribed() { 
        return subscribed; 
    }
    public void setSubscribed(Boolean subscribed) { 
        this.subscribed = subscribed; 
    }

    public String getCompletedByEmail() { 
        return completedByEmail; 
    }
    public void setCompletedByEmail(String completedByEmail) { 
        this.completedByEmail = completedByEmail; 
    }
}
