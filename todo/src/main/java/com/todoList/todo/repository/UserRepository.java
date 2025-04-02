package com.todoList.todo.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.todoList.todo.entities.User;

/**
 * Repository per accedere ai dati degli utenti.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Trova un utente per email.
     */
    Optional<User> findByEmail(String email);
}