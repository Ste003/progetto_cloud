package com.todoList.todo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Classe principale per avviare l'applicazione To-Do.
 */
@SpringBootApplication
public class TodoApplication {

    /**
     * Metodo main per avviare l'applicazione Spring Boot.
     */
    public static void main(String[] args) {
        SpringApplication.run(TodoApplication.class, args);
    }

}