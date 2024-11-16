package ru.vsu.cs.ustinov.cats.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.lang.NonNull;
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
 * Для авторизации в куках должен лежать accessToken
 */
@Component
@AllArgsConstructor
public class JwtRequestFilter extends OncePerRequestFilter {

    private UserService userService;

    private JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain)
            throws ServletException, IOException {
        String requestUri = request.getRequestURI();

        if (requestUri.contains("/api/auth/")) {
            filterChain.doFilter(request, response);
            return;
        }

        if (SecurityContextHolder.getContext().getAuthentication() != null){
            handleException(response, "Invalid auth");
            return;
        }

        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            handleException(response, "Cookies are missing");
            return;
        }
        String jwt = null;
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals("accessToken")) {
                jwt = cookie.getValue();
            }
        }
        if (jwt == null) {
            handleException(response, "Access token is missing");
            return;
        }

        String username;
        try {
            username = jwtUtil.extractUsername(jwt);
        } catch (Exception e){
            handleException(response, "Invalid token");
            return;
        }

        UserDetails userDetails = userService.loadUserByUsername(username);

        if (!jwtUtil.validateToken(jwt, userDetails)){
            handleException(response, "Token expired");
            return;
        }

        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities());
        authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);

        filterChain.doFilter(request, response);
    }

    private void handleException(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.getWriter().write("{\"error\": \"" + message + "\"}");
    }
}

