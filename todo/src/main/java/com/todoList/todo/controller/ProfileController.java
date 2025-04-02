package com.todoList.todo.controller;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.todoList.todo.dto.TodoItemDTO;
import com.todoList.todo.dto.UserDTO;
import com.todoList.todo.entities.TodoItem;
import com.todoList.todo.entities.User;
import com.todoList.todo.repository.TodoItemRepository;
import com.todoList.todo.repository.UserRepository;
import com.todoList.todo.service.TelegramNotificationService;
import com.todoList.todo.service.TodoService;

import jakarta.annotation.PostConstruct;

/**
* Controller per la gestione del profilo utente e delle relative To-Do.
* Espone vari endpoint per:
* - Ottenere il profilo dell'utente
* - Ottenere le To-Do create o a cui l'utente è iscritto
* - Gestire l'iscrizione alle To-Do
* - Completare le To-Do (e inviare notifiche via Telegram e Kafka)
*/
@RestController
@RequestMapping("/profile")
public class ProfileController {

    // Iniezione del servizio TodoService per gestire le logiche relative alle To-Do
    @Autowired
    private TodoService todoService;

    // Iniezione del repository degli utenti per recuperare i dati dell'utente
    @Autowired
    private UserRepository userRepository;

    // Iniezione del repository delle To-Do per operazioni dirette sul database (se necessario)
    @Autowired
    private TodoItemRepository todoItemRepository;

    // Lettura della proprietà admin.email dal file di configurazione
    @Value("${admin.email}")
    private String adminEmail;

    // Iniezione del servizio per l'invio delle notifiche via Telegram
    @Autowired
    private TelegramNotificationService telegramNotificationService;

    /**
    * Metodo eseguito dopo l'inizializzazione del bean (PostConstruct).
    * Attualmente commentato: si potrebbe usare per eseguire operazioni di log di inizializzazione.
    */
    @PostConstruct
    public void init() {
        // Esempio: logger.info("Admin email (valore letto da properties): {}", adminEmail);
    }

    /**
    * Endpoint GET per ottenere il profilo utente.
    * Restituisce un oggetto UserDTO contenente le informazioni dell'utente, le To-Do create e le To-Do a cui l'utente è iscritto.
    */
    @GetMapping
    public ResponseEntity<UserDTO> getUserProfile(@AuthenticationPrincipal OAuth2User principal) {
        // Se l'utente non è autenticato, restituisce UNAUTHORIZED
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
        String email = principal.getAttribute("email");
        // Recupera l'utente dal database in base all'email
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utente non trovato"));

        // Mappa le To-Do create dall'utente in DTO
        List<TodoItemDTO> createdTodos = user.getTodoItems().stream()
                .map(todo -> new TodoItemDTO(
                        todo.getId(),
                        todo.getTitle(),
                        todo.getCompleted(),
                        (todo.getUser() != null ? todo.getUser().getEmail() : null),
                        false,
                        (todo.getCompletedBy() != null ? todo.getCompletedBy().getEmail() : null),
                        (todo.getCompletedBy() != null ? todo.getCompletedBy().getName() : null)))
                .collect(Collectors.toList());

        // Mappa le To-Do a cui l'utente è iscritto in DTO
        List<TodoItemDTO> subscribedTodos = user.getSubscribedTodos().stream()
                .map(todo -> new TodoItemDTO(
                        todo.getId(),
                        todo.getTitle(),
                        todo.getCompleted(),
                        (todo.getUser() != null ? todo.getUser().getEmail() : null),
                        false,
                        (todo.getCompletedBy() != null ? todo.getCompletedBy().getEmail() : null),
                        (todo.getCompletedBy() != null ? todo.getCompletedBy().getName() : null)))
                .collect(Collectors.toList());

        UserDTO userDTO = new UserDTO(user.getId(), user.getName(), user.getEmail(), createdTodos, subscribedTodos);
        return ResponseEntity.ok(userDTO);
    }

    /**
    * Endpoint GET per ottenere l'email dell'amministratore.
    */
    @GetMapping("/admin-email")
    public ResponseEntity<String> getAdminEmail() {
        return ResponseEntity.ok(adminEmail);
    }

    /**
    * Endpoint GET per ottenere le To-Do create dall'utente autenticato.
    */
    @GetMapping("/created-todos")
    public ResponseEntity<List<TodoItemDTO>> getCreatedTodos(@AuthenticationPrincipal OAuth2User principal) {
        String email = principal.getAttribute("email");
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utente non trovato"));
        List<TodoItemDTO> createdTodos = user.getTodoItems().stream()
                .map(todo -> new TodoItemDTO(
                        todo.getId(),
                        todo.getTitle(),
                        todo.getCompleted(),
                        (todo.getUser() != null ? todo.getUser().getEmail() : null),
                        false,
                        (todo.getCompletedBy() != null ? todo.getCompletedBy().getEmail() : null),
                        (todo.getCompletedBy() != null ? todo.getCompletedBy().getName() : null)))
                .collect(Collectors.toList());
        return ResponseEntity.ok(createdTodos);
    }

    /**
    * Endpoint GET per ottenere le To-Do a cui l'utente è iscritto.
    */
    @GetMapping("/subscribed-todos")
    public ResponseEntity<List<TodoItemDTO>> getSubscribedTodos(@AuthenticationPrincipal OAuth2User principal) {
        String email = principal.getAttribute("email");
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utente non trovato"));
        List<TodoItemDTO> subscribedTodos = user.getSubscribedTodos().stream()
                .map(todo -> new TodoItemDTO(
                        todo.getId(),
                        todo.getTitle(),
                        todo.getCompleted(),
                        (todo.getUser() != null ? todo.getUser().getEmail() : null),
                        false,
                        (todo.getCompletedBy() != null ? todo.getCompletedBy().getEmail() : null),
                        (todo.getCompletedBy() != null ? todo.getCompletedBy().getName() : null)))
                .collect(Collectors.toList());
        return ResponseEntity.ok(subscribedTodos);
    }

    /**
    * Endpoint POST per iscrivere l'utente a una To-Do.
    */
    @PostMapping("/todos/{todoId}/subscribe")
    public ResponseEntity<String> subscribeToTodo(@PathVariable Long todoId,
            @AuthenticationPrincipal OAuth2User principal) {
        String email = principal.getAttribute("email");
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utente non trovato"));
        TodoItem todo = todoItemRepository.findById(todoId)
                .orElseThrow(() -> new RuntimeException("Todo non trovata"));

        // Aggiunge l'utente alla lista dei sottoscrittori se non è già presente
        if (!todo.getSubscribers().contains(user)) {
            todo.getSubscribers().add(user);
            todoItemRepository.save(todo);
            return ResponseEntity.ok("Iscrizione avvenuta con successo");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Già iscritto");
        }
    }

    /**
    * Endpoint PUT per completare una To-Do.
    * Verifica che l'utente sia autorizzato (creatore, iscritto o admin) a completare la To-Do,
    * quindi chiama il metodo di servizio per completarla e invia notifiche via Telegram.
    */
    @PutMapping("/todos/{todoId}/complete")
    public ResponseEntity<String> completeTodo(@PathVariable Long todoId,
            @AuthenticationPrincipal OAuth2User principal) {
        String email = principal.getAttribute("email");
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utente non trovato"));
        TodoItem todo = todoItemRepository.findById(todoId)
                .orElseThrow(() -> new RuntimeException("Todo non trovata"));

        boolean isAdmin = user.getEmail().equals(adminEmail);
        // Verifica che l'utente sia il creatore, uno degli iscritti o un admin
        if (!isAdmin && !todo.getUser().getEmail().equalsIgnoreCase(user.getEmail())
                && !todo.getSubscribers().contains(user)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Non puoi modificare questa to-do.");
        }

        // Chiama il metodo del service per completare la To-Do e inviare la notifica Kafka
        todoService.closeTodo(todoId, email);

        // Notifica il creatore della To-Do se è stato registrato un telegram_chat_id
        if (todo.getUser() != null && todo.getUser().getTelegramChatId() != null) {
            String message = "La to-do '" + todo.getTitle() + "' è stata completata da " + user.getName();
            telegramNotificationService.sendNotification(todo.getUser().getTelegramChatId(), message);
        }

        // Notifica tutti gli iscritti che hanno un telegram_chat_id
        if (todo.getSubscribers() != null && !todo.getSubscribers().isEmpty()) {
            for (User subscriber : todo.getSubscribers()) {
                // Evita di notificare il creatore se risulta anche iscritto
                if (subscriber.getTelegramChatId() != null
                        && (todo.getUser() == null || !subscriber.getEmail().equalsIgnoreCase(todo.getUser().getEmail()))) {
                    String subscriberMessage = "La to-do '" + todo.getTitle() + "' a cui eri iscritto è stata completata da " + user.getName();
                    telegramNotificationService.sendNotification(subscriber.getTelegramChatId(), subscriberMessage);
                }
            }
        }

        return ResponseEntity.ok("Todo completata con successo");
    }

    /**
    * Endpoint PATCH per completare tutte le To-Do (solo per l'admin).
    */
    @PatchMapping("/todos/complete")
    public ResponseEntity<Void> completeAllTodos(@AuthenticationPrincipal OAuth2User principal) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        String email = principal.getAttribute("email");
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (!userOpt.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        User user = userOpt.get();
        boolean isAdmin = user.getEmail().equals(adminEmail);
        if (!isAdmin) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        List<TodoItem> allTodos = todoItemRepository.findAll();
        for (TodoItem todo : allTodos) {
            if (!todo.getCompleted()) {
                todo.setCompleted(true);
                // Per completeAll, puoi decidere se settare completedBy o usare l'admin
                todo.setCompletedBy(user);
                todoItemRepository.save(todo);
            }
        }
        return ResponseEntity.ok().build();
    }

    /**
    * Endpoint GET per recuperare tutte le To-Do (solo per l'admin).
    */
    @GetMapping("/todos")
    public ResponseEntity<List<TodoItemDTO>> getAllTodos(@AuthenticationPrincipal OAuth2User principal) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        String email = principal.getAttribute("email");
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (!userOpt.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        User user = userOpt.get();
        boolean isAdmin = user.getEmail().equalsIgnoreCase(adminEmail);
        if (!isAdmin) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        List<TodoItemDTO> todos = todoItemRepository.findAll().stream()
                .map(todo -> new TodoItemDTO(
                        todo.getId(),
                        todo.getTitle(),
                        todo.getCompleted(),
                        (todo.getUser() != null ? todo.getUser().getEmail() : null),
                        false,
                        (todo.getCompletedBy() != null ? todo.getCompletedBy().getEmail() : null),
                        (todo.getCompletedBy() != null ? todo.getCompletedBy().getName() : null)))
                .collect(Collectors.toList());
        return ResponseEntity.ok(todos);
    }
}
