package ru.vsu.cs.ustinov.cats.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import ru.vsu.cs.ustinov.cats.dto.auth.AuthRequest;
import ru.vsu.cs.ustinov.cats.dto.auth.AuthResponse;
import ru.vsu.cs.ustinov.cats.dto.registration.RegistrationRequest;
import ru.vsu.cs.ustinov.cats.model.RefreshToken;
import ru.vsu.cs.ustinov.cats.model.User;
import ru.vsu.cs.ustinov.cats.service.RefreshTokenService;
import ru.vsu.cs.ustinov.cats.service.UserService;
import ru.vsu.cs.ustinov.cats.jwt.JwtUtil;

import java.util.Optional;

@AllArgsConstructor
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private AuthenticationManager authenticationManager;

    private UserService userService;

    private RefreshTokenService refreshTokenService;

    private JwtUtil jwtUtil;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody RegistrationRequest registrationRequest) {
        // Если пользователь с таким юзернеймом уже есть в БД
        if (userService.existsByUsername(registrationRequest.getUsername())) {
            return ResponseEntity.badRequest().body("User with this username already exists.");
        }
        // Регистрируем
        userService.registerNewUser(registrationRequest.getUsername(), registrationRequest.getPassword());
        return ResponseEntity.ok("User registered successfully");
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody AuthRequest authRequest, HttpServletResponse response) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword())
            );
        } catch (BadCredentialsException e) {
            return ResponseEntity.badRequest().body("Incorrect username or password.");
        }

        // TODO: наспагетил я тут кнш обязательно исправить!

        UserDetails userDetails = userService.loadUserByUsername(authRequest.getUsername());
        String accessToken = jwtUtil.generateAccessToken(userDetails);

        Optional<User> userOptional = userService.findByUsername(userDetails.getUsername());

        RefreshToken refreshToken = refreshTokenService.createRefreshToken(userOptional.get());

        Cookie refreshTokenCookie = new Cookie("refreshToken", refreshToken.getToken());
        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setSecure(true);
        refreshTokenCookie.setMaxAge(7 * 24 * 60 * 60); // 1 неделя

        response.addCookie(refreshTokenCookie);

        return ResponseEntity.ok(new AuthResponse(accessToken));
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshAccessToken(@CookieValue("refreshToken") String refreshToken) {
        Optional<RefreshToken> token = refreshTokenService.validateRefreshToken(refreshToken);
        if (token.isPresent()) {
            UserDetails userDetails = userService.loadUserByUsername(token.get().getUser().getUsername());
            String newAccessToken = jwtUtil.generateAccessToken(userDetails);
            return ResponseEntity.ok(new AuthResponse(newAccessToken));
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or expired refresh token");
        }
    }
}

