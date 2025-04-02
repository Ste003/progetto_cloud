package com.todoList.todo.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.todoList.todo.entities.TodoItem;
import com.todoList.todo.repository.TodoItemRepository;

/**
 * Controller per la gestione della home.
 * - Espone un endpoint per visualizzare la lista delle To-Do in formato HTML.
 */
@RestController
@RequestMapping("/home") // Mappa il controller sull'endpoint /home
public class HomeController {

    private final TodoItemRepository repository;

    /**
     * Costruttore per iniezione delle dipendenze.
     * Spring gestisce automaticamente l'iniezione senza bisogno di `@Autowired`.
     */
    public HomeController(TodoItemRepository repository) {
        this.repository = repository;
    }

    /**
     * Recupera tutte le To-Do e le restituisce come una pagina HTML.
     */
    @GetMapping(produces = "text/html")
    public String home() {
        List<TodoItem> todos = repository.findAll(); // Recupera tutte le To-Do dal database

        // Costruisce dinamicamente una pagina HTML con la lista delle To-Do
        StringBuilder sb = new StringBuilder("<html><body>");
        sb.append("<h1>Todo List</h1>");
        sb.append("<ul>");
        for (TodoItem todo : todos) {
            sb.append("<li>")
              .append(todo.getTitle()) // Titolo della To-Do
              .append(" - Completato: ")
              .append(todo.getCompleted()) // Stato di completamento
              .append("</li>");
        }
        sb.append("</ul>");
        sb.append("</body></html>");
        
        return sb.toString(); // Restituisce la stringa HTML generata
    }
}