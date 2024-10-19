package ru.vsu.cs.ustinov.cats.jwt;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * Класс для работы с Jwt.
 */
@Component
public class JwtUtil {

    private final String SECRET_KEY = "tStmllQxADE+/MuCqiXk6wExMSPNcliv2CZyoVukR+8=";

    /**
     * Генерируем access токен
     * @param userDetails информация о пользователе, будет зашита в ключ
     * @return Непосредственно токен
     */
    public String generateAccessToken(UserDetails userDetails) {
        int KEY_DURATION = 1000 * 60 * 60; // 1 час

        return Jwts.builder()
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + KEY_DURATION)) // 1 час
                // TODO: разобраться почему подчеркивает идея строку
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                .compact();
    }


    /**
     * Получаем из Jwt токена юзернейм зашитого в него пользователя
     * @param token Jwt токен
     * @return Юзернейм пользователя
     */
    public String extractUsername(String token) {
        // TODO: разобраться почему подчеркивает идея строки
        return Jwts.parser()
                .setSigningKey(SECRET_KEY)
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    /**
     * Проверяем валидность токена на соответствие пользователю
     * @param token Jwt токен
     * @param userDetails Информация о пользователе вытянутая из БД (наверное лишняя проверка)
     * @return Булево значение валиден ли токен
     */
    public boolean validateToken(String token, UserDetails userDetails) {
        String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    private boolean isTokenExpired(String token) {
        // TODO: разобраться почему подчеркивает идея строки
        Date expiration = Jwts.parser()
                .setSigningKey(SECRET_KEY)
                .parseClaimsJws(token)
                .getBody()
                .getExpiration();
        return expiration.before(new Date());
    }
}

