package ru.vsu.cs.ustinov.cats.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ru.vsu.cs.ustinov.cats.dto.auth.AuthRequest;
import ru.vsu.cs.ustinov.cats.dto.auth.AuthResponse;
import ru.vsu.cs.ustinov.cats.dto.registration.RegistrationRequest;
import ru.vsu.cs.ustinov.cats.service.UserService;
import ru.vsu.cs.ustinov.cats.jwt.JwtUtil;

@RestController
@AllArgsConstructor
public class AuthController {

    private AuthenticationManager authenticationManager;

    private UserService userService;

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
    public ResponseEntity<?> loginUser(@RequestBody AuthRequest authRequest) throws Exception {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword())
            );
        } catch (BadCredentialsException e) {
            return ResponseEntity.badRequest().body("Incorrect username or password.");
        }

        final UserDetails userDetails = userService.loadUserByUsername(authRequest.getUsername());
        final String jwt = jwtUtil.generateToken(userDetails);

        return ResponseEntity.ok(new AuthResponse(jwt));
    }
}

