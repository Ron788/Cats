package ru.vsu.cs.ustinov.cats.jwt;

import java.security.SecureRandom;
import java.util.Base64;


/**
 * Класс, который я использовал для генерации ключа для {@link ru.vsu.cs.ustinov.cats.jwt.JwtUtil}.
 */
public class KeyGenerator {

    public static String generateSecretKey() {
        byte[] key = new byte[32]; // 256 bits
        new SecureRandom().nextBytes(key);
        return Base64.getEncoder().encodeToString(key);
    }

    public static void main(String[] args) {
        String secretKey = generateSecretKey();
        System.out.println("Generated Secret Key: " + secretKey);
    }
}
