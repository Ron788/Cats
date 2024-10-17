package ru.vsu.cs.ustinov.cats.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.antlr.v4.runtime.misc.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
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
 */
// TODO: разобраться как это работает
@Component
@AllArgsConstructor
public class JwtRequestFilter extends OncePerRequestFilter {

    private UserService userService;

    private JwtUtil jwtUtil;


    // TODO: разораться почему подчеркивает идея
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {


        // Получаем заголовок
        final String authorizationHeader = request.getHeader("Authorization");

        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // Вытаскиваем токен запроса
        String jwt = authorizationHeader.substring(7);
        String username = jwtUtil.extractUsername(jwt);

        if (SecurityContextHolder.getContext().getAuthentication() != null) {
            filterChain.doFilter(request, response);
            return;
        }

        // Получаем из БД информацию о пользователе
        UserDetails userDetails = userService.loadUserByUsername(username);

        if (!jwtUtil.validateToken(jwt, userDetails)){
            filterChain.doFilter(request, response);
            return;
        }

        // Авторизуем
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities());
        authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);

        filterChain.doFilter(request, response);
    }
}

