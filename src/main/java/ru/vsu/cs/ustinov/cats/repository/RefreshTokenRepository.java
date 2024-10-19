package ru.vsu.cs.ustinov.cats.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.vsu.cs.ustinov.cats.model.RefreshToken;
import ru.vsu.cs.ustinov.cats.model.User;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByToken(String token);
    Optional<RefreshToken> findByUser(User user);
    void deleteByUser(User user);
}