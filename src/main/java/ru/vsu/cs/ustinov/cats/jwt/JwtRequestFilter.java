package ru.vsu.cs.ustinov.cats.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import ru.vsu.cs.ustinov.cats.service.UserService;

import java.io.IOException;

/**
 * Непосредственно фильтр, который проверяет авторизацию
 * Для авторизации в заголовке запроса должно быть "Authorization: Bearer <token>"
 */
@Component
@AllArgsConstructor
public class JwtRequestFilter extends OncePerRequestFilter {

    private UserService userService;

    private JwtUtil jwtUtil;


    // TODO: разораться почему подчеркивает идея
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String requestUri = request.getRequestURI();

        if (requestUri.contains("/api/auth/")) {
            filterChain.doFilter(request, response);
            return;
        }

        // Получаем заголовок
        final String authorizationHeader = request.getHeader("Authorization");

        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            handleException(response, "Token undefined", HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        if (SecurityContextHolder.getContext().getAuthentication() != null){
            handleException(response, "Invalid auth", HttpServletResponse.SC_UNAUTHORIZED);
        }
        // Вытаскиваем токен запроса
        String jwt = authorizationHeader.substring(7);

        String username;
        try {
            username = jwtUtil.extractUsername(jwt);
        } catch (Exception e){
            handleException(response, "Invalid token", HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }


        // Получаем из БД информацию о пользователе
        UserDetails userDetails = userService.loadUserByUsername(username);

        if (!jwtUtil.validateToken(jwt, userDetails)){
            handleException(response, "Token expired", HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        // Авторизуем
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities());
        authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);

        filterChain.doFilter(request, response);
    }

    private void handleException(HttpServletResponse response, String message, int statusCode) throws IOException {
        response.setStatus(statusCode);
        response.setContentType("application/json");
        response.getWriter().write("{\"error\": \"" + message + "\"}");
    }
}

