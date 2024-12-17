package ru.vsu.cs.ustinov.cats.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import ru.vsu.cs.ustinov.cats.dto.DefaultResponse;
import ru.vsu.cs.ustinov.cats.dto.auth.AuthRequest;
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
    public ResponseEntity<DefaultResponse<String>> registerUser(@RequestBody RegistrationRequest registrationRequest) {
        // Если пользователь с таким юзернеймом уже есть в БД
        if (userService.existsByUsername(registrationRequest.getUsername())) {
            return DefaultResponse.badRequest("This username is already in use");
        }
        // Регистрируем
        userService.registerNewUser(registrationRequest.getUsername(), registrationRequest.getPassword());
        return DefaultResponse.ok("User registered successfully");
    }

    /**
     * Логиним пользователя. Записываем в куки refresh токен, access возвращаем ответом.
     * Второй используется для авторизации, первый для генерации новых access токенов
     * (срок жизни у них сильно различается)
     * @param authRequest данные для логина
     * @return ответ пользователю
     */
    @PostMapping("/login")
    public ResponseEntity<DefaultResponse<String>> loginUser(@RequestBody AuthRequest authRequest) {
        // Непосредственно авторизация пользователя
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword()));
        } catch (BadCredentialsException e) {
            return DefaultResponse.badRequest("Incorrect username or password.");
        } catch (AuthenticationException e) {
            return DefaultResponse.badRequest("Login failed.");
        }

        // Получаем юзера по юзернейму
        Optional<User> user = userService.findByUsername(authRequest.getUsername());
        if (user.isEmpty()){
            return DefaultResponse.badRequest("User not found");
        }
        // Генерируем refresh токен
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user.get());
        // В куках прописываем refresh токен
        ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", refreshToken.getToken())
                .httpOnly(true)
                .path("/")
                .maxAge(1000 * 60 * 60 * 24 * 7)  // 7 дней
                .build();


        UserDetails userDetails = userService.loadUserByUsername(user.get());
        String accessToken = jwtUtil.generateAccessToken(userDetails);

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
                .body(new DefaultResponse<>(HttpStatus.OK, "Login completed successfully").addData("accessToken", accessToken));
        // Возможно следует возвращать только refresh, а access получать следующим запросом, надо будет как-нибудь подумать над этим
    }

    /**
     * Генерируем новый access токен получив refresh токен в куках
     * @param refreshToken токен в куках
     * @return ответ пользователю
     */
    @PostMapping("/refresh-token")
    public ResponseEntity<DefaultResponse<String>> refreshAccessToken(@CookieValue("refreshToken") String refreshToken) {
        // Проверяем валидность refresh токена
        Optional<RefreshToken> token = refreshTokenService.validateRefreshToken(refreshToken);

        if (token.isEmpty()) {
            return DefaultResponse.badRequest("Invalid or expired refresh token");
        }

        // Генерируем access и возвращаем
        UserDetails userDetails = userService.loadUserByUsername(token.get().getUser().getUsername());
        String newAccessToken = jwtUtil.generateAccessToken(userDetails);

        ResponseCookie accessCookie = ResponseCookie.from("accessToken", newAccessToken)
                .httpOnly(true)
                .path("/")
                .maxAge(1000 * 60 * 60)  // 1 час
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, accessCookie.toString())
                .body(new DefaultResponse<>(HttpStatus.OK, "New access token refreshed successfully"));
    }

    /**
     * Делаем refresh токен недействительным
     * @param refreshToken токен
     * @return ответ пользователю
     */
    @SuppressWarnings("DataFlowIssue") // for nulls
    @PostMapping("/logout")
    public ResponseEntity<DefaultResponse<String>> logoutUser(@CookieValue("refreshToken") String refreshToken) {
        Optional<RefreshToken> token = refreshTokenService.validateRefreshToken(refreshToken);

        if (token.isEmpty()) {
            return DefaultResponse.unauthorized("Invalid or expired refresh token");
        }

        refreshTokenService.revokeToken(token.get());

        ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", null)
                .httpOnly(true)
                .path("/")
                .maxAge(0) // Задаем время жизни = 0, чтобы удалить
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
                .body(new DefaultResponse<>(HttpStatus.OK, "Ok!"));
    }
}

