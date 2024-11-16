package ru.vsu.cs.ustinov.cats.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.vsu.cs.ustinov.cats.model.User;

import java.util.Optional;

/**
 * На самом деле чудо -_-
 */
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    boolean existsByUsername(String username);
}
