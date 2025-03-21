package com.todoList.todo.kafka;

public class TodoClosedEvent {
    private Long todoId;
    private String title;
    private String userEmail;

    // Costruttore di default (necessario per Jackson)
    public TodoClosedEvent() {}

    public TodoClosedEvent(Long todoId, String title, String userEmail) {
        this.todoId = todoId;
        this.title = title;
        this.userEmail = userEmail;
    }

    // Getters e Setters
    public Long getTodoId() {
        return todoId;
    }

    public void setTodoId(Long todoId) {
        this.todoId = todoId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }
}
