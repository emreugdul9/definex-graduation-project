package com.definexjavaspringbootbootcamp.definexgraduationproject.service;

import com.definexjavaspringbootbootcamp.definexgraduationproject.utils.JwtUtil;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.crypto.SecretKey;
import java.lang.reflect.Field;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class JwtUtilTest {

    @InjectMocks
    private JwtUtil jwtUtil;

    private SecretKey testSecretKey;
    private String validToken;
    private String username;
    private String userType;

    @BeforeEach
    void setUp() throws Exception {
        testSecretKey = Keys.secretKeyFor(SignatureAlgorithm.HS256);

        Field secretKeyField = JwtUtil.class.getDeclaredField("secretKey");
        secretKeyField.setAccessible(true);
        secretKeyField.set(jwtUtil, testSecretKey);

        username = "testUser";
        userType = "TEAM_LEADER";

        validToken = Jwts.builder()
                .setSubject(username)
                .claim("userType", userType)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 60 * 60 * 1000))
                .signWith(testSecretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    @Test
    void generateToken_ShouldCreateValidToken() {
        String token = jwtUtil.generateToken(username, userType);

        assertNotNull(token);
        assertTrue(jwtUtil.validateToken(token));
        assertEquals(username, jwtUtil.extractUsername(token));
    }

    @Test
    void extractUsername_ShouldReturnCorrectUsername() {
        String extractedUsername = jwtUtil.extractUsername(validToken);

        assertEquals(username, extractedUsername);
    }

    @Test
    void validateToken_WithValidToken_ShouldReturnTrue() {
        boolean isValid = jwtUtil.validateToken(validToken);

        assertTrue(isValid);
    }

    @Test
    void validateToken_WithExpiredToken_ShouldReturnFalse() {
        String expiredToken = Jwts.builder()
                .setSubject(username)
                .claim("userType", userType)
                .setIssuedAt(new Date(System.currentTimeMillis() - 2 * 60 * 60 * 1000)) // 2 hours ago
                .setExpiration(new Date(System.currentTimeMillis() - 60 * 60 * 1000))   // 1 hour ago (expired)
                .signWith(testSecretKey, SignatureAlgorithm.HS256)
                .compact();

        boolean isValid = jwtUtil.validateToken(expiredToken);

        assertFalse(isValid);
    }

    @Test
    void validateToken_WithInvalidSignature_ShouldReturnFalse() {
        SecretKey differentKey = Keys.secretKeyFor(SignatureAlgorithm.HS256);
        String tokenWithDifferentSignature = Jwts.builder()
                .setSubject(username)
                .claim("userType", userType)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 60 * 60 * 1000))
                .signWith(differentKey, SignatureAlgorithm.HS256)
                .compact();

        boolean isValid = jwtUtil.validateToken(tokenWithDifferentSignature);

        assertFalse(isValid);
    }

    @Test
    void validateToken_WithMalformedToken_ShouldReturnFalse() {
        String malformedToken = "malformed.jwt.token";

        boolean isValid = jwtUtil.validateToken(malformedToken);

        assertFalse(isValid);
    }

    @Test
    void extractUsername_WithInvalidToken_ShouldThrowException() {
        String invalidToken = "invalid.token";

        assertThrows(Exception.class, () -> jwtUtil.extractUsername(invalidToken));
    }
}