package ru.vsu.cs.ustinov.cats.jwt;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class JwtUtilTest {
    @Test
    public void testGenerateAndValidateToken() {
        JwtUtil jwtUtil = new JwtUtil();

        String username = "test username";

        String jwtToken = jwtUtil.generateAccessToken(username);

        assertNotNull(jwtToken);

        assertEquals(jwtUtil.extractUsername(jwtToken), username);

        assertTrue(jwtUtil.validateToken(jwtToken, username));
    }
}