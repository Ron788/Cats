package ru.vsu.cs.ustinov.cats.jwt;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

/**
 * Класс для работы с Jwt.
 */
@Component
public class JwtUtil {

    private final String SECRET_KEY = "tStmllQxADE+/MuCqiXk6wExMSPNcliv2CZyoVukR+8=";
    private final Key key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes());

    /**
     * Генерируем access токен
     * @param userDetails информация о пользователе, будет зашита в ключ
     * @return Непосредственно токен
     */
    public String generateAccessToken(UserDetails userDetails) {
        return generateAccessToken(userDetails.getUsername());
    }

    /**
     * Генерируем access токен
     * @param username юзернейм пользователя
     * @return Непосредственно токен
     */
    public String generateAccessToken(String username) {
        int KEY_DURATION = 1000 * 60 * 60; // 1 час

        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + KEY_DURATION))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }


    /**
     * Получаем из Jwt токена юзернейм зашитого в него пользователя
     * @param token Jwt токен
     * @return Юзернейм пользователя
     */
    public String extractUsername(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
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
        return validateToken(token, userDetails.getUsername());
    }

    /**
     * Проверяем валидность токена на соответствие пользователю
     * @param token Jwt токен
     * @param username юзернейм пользователя
     * @return Булево значение валиден ли токен
     */
    public boolean validateToken(String token, String username) {
        if (!extractUsername(token).equals(username)) {
            return false;
        }
        return !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        Date expiration = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getExpiration();
        return expiration.before(new Date());
    }
}

