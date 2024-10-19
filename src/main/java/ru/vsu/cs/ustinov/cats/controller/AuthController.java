package ru.vsu.cs.ustinov.cats.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
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


/**
 *  Непосредственно контроллер авторизации
 */
@AllArgsConstructor
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private AuthenticationManager authenticationManager;

    private UserService userService;

    private RefreshTokenService refreshTokenService;

    private JwtUtil jwtUtil;

    /**
     * Регистрируем пользователя.
     * Так же проверка чтоб не зарегистрировать один юзернейм дважды
     * @param registrationRequest данные для регистрации
     * @return ответ пользователю
     */
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

    /**
     * Логиним пользователя. Возвращаем ему access токен,
     * который надо будет держать в заголовке каждого запроса для идентификации.
     * И refresh токен, по которому можно будет генерировать новые access токены
     * @param authRequest данные для логина
     * @return ответ пользователю
     */
    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody AuthRequest authRequest) {
        // Непосредственно авторизация пользователя
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword()));
        } catch (BadCredentialsException e) {
            return ResponseEntity.badRequest().body("Incorrect username or password.");
        } catch (AuthenticationException e) {
            return ResponseEntity.badRequest().body("Login failed.");
        }

        // Получаем юзера по юзернейму
        User user = userService.findByUsername(authRequest.getUsername());
        // Генерируем refresh токен
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);
        // В куках прописываем refresh токен
        ResponseCookie cookie = ResponseCookie.from("refreshToken", refreshToken.getToken())
                .httpOnly(true)
                .path("/")
                .maxAge(60 * 60 * 24 * 7)  // 7 дней
                .build();


        UserDetails userDetails = userService.loadUserByUsername(user);
        String accessToken = jwtUtil.generateAccessToken(userDetails);

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(new AuthResponse(accessToken)); // Возвращаем access токен с куками
        // Возможно следует возвращать только refresh, а access получать следующим запросом, надо будет как-нибудь подумать над этим
    }

    /**
     * Генерируем новый access токен получив refresh токен в куках
     * @param refreshToken токен в куках
     * @return ответ пользователю
     */
    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshAccessToken(@CookieValue("refreshToken") String refreshToken) {
        // Проверяем валидность refresh токена
        Optional<RefreshToken> token = refreshTokenService.validateRefreshToken(refreshToken);

        if (token.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or expired refresh token");
        }

        // Генерируем access и возвращаем
        UserDetails userDetails = userService.loadUserByUsername(token.get().getUser().getUsername());
        String newAccessToken = jwtUtil.generateAccessToken(userDetails);
        return ResponseEntity.ok(new AuthResponse(newAccessToken));

    }
}

