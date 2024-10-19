package ru.vsu.cs.ustinov.cats.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.vsu.cs.ustinov.cats.model.RefreshToken;
import ru.vsu.cs.ustinov.cats.model.User;
import ru.vsu.cs.ustinov.cats.repository.RefreshTokenRepository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    // Создание нового refresh токена
    public RefreshToken createRefreshToken(User user) {
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUser(user);
        refreshToken.setToken(generateToken()); // Метод для генерации токена
        refreshToken.setExpiryDate(LocalDateTime.now().plusDays(7)); // Пример: токен действует 7 дней
        refreshToken.setCreatedAt(LocalDateTime.now());

        return refreshTokenRepository.save(refreshToken);
    }

    // Проверка валидности токена
    public Optional<RefreshToken> validateRefreshToken(String token) {
        Optional<RefreshToken> refreshToken = refreshTokenRepository.findByToken(token);
        if (refreshToken.isPresent() && !refreshToken.get().isRevoked() && refreshToken.get().getExpiryDate().isAfter(LocalDateTime.now())) {
            return refreshToken;
        }
        return Optional.empty(); // Токен недействителен
    }

    // Отзыв токена
    public void revokeToken(RefreshToken token) {
        token.setRevoked(true);
        refreshTokenRepository.save(token);
    }

    // Удаление всех токенов пользователя (при выходе из системы)
    public void deleteTokensForUser(User user) {
        refreshTokenRepository.deleteByUser(user);
    }

    private String generateToken() {
        // Логика генерации уникального токена
        return UUID.randomUUID().toString();
    }
}
