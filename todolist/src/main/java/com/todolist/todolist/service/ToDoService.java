package com.todolist.todolist.service;

import com.todolist.todolist.model.ToDo;
import com.todolist.todolist.repository.ToDoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ToDoService {

    private final ToDoRepository toDoRepository;
    private final TelegramBotService telegramBotService; // Iniezione del TelegramBotService

    @Autowired
    public ToDoService(ToDoRepository toDoRepository, TelegramBotService telegramBotService) {
        this.toDoRepository = toDoRepository;
        this.telegramBotService = telegramBotService;
    }

    public ToDo saveToDo(ToDo toDo) {
        return toDoRepository.save(toDo);
    }

    public Optional<ToDo> findById(Long id) {
        return toDoRepository.findById(id);
    }

    public List<ToDo> findByUserId(Long userId) {
        return toDoRepository.findByUser_Id(userId);
    }

    public void deleteToDo(Long id) {
        toDoRepository.deleteById(id);
    }

    public void markAsCompleted(Long todoId) {
        Optional<ToDo> optionalToDo = toDoRepository.findById(todoId);
        if (optionalToDo.isPresent()) {
            ToDo toDo = optionalToDo.get();
            toDo.setCompleted(true);
            toDoRepository.save(toDo);

            // Invia una notifica agli utenti sottoscritti
            toDo.getSubscribers().forEach(user -> {
                telegramBotService.sendMessage(
                    Long.valueOf(user.getChatId()),
                    "L'attività \"" + toDo.getTitle() + "\" è stata completata!"
                );
            });
        }
    }
}
