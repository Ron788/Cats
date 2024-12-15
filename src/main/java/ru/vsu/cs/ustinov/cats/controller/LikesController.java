package ru.vsu.cs.ustinov.cats.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.vsu.cs.ustinov.cats.dto.DefaultResponse;
import ru.vsu.cs.ustinov.cats.jwt.JwtUtil;
import ru.vsu.cs.ustinov.cats.service.LikesService;

@AllArgsConstructor
@RestController
@RequestMapping("/api/post")
public class LikesController {
    private final LikesService likesService;
    private final JwtUtil jwtUtil;

    @PostMapping("/like/{postId}")
    public ResponseEntity<DefaultResponse<String>> like(@PathVariable Long postId, HttpServletRequest request) {
        String username = jwtUtil.extractUsername(request.getHeader("Authorization").substring(7));

        if (!likesService.like(username, postId)){
            return ResponseEntity.ok().body(new DefaultResponse<>(HttpStatus.BAD_REQUEST, "Error"));
        }
        return ResponseEntity.ok().body(new DefaultResponse<>(HttpStatus.OK, "Success"));
    }

    @PostMapping("/unlike/{postId}")
    public ResponseEntity<DefaultResponse<String>> unlike(@PathVariable Long postId, HttpServletRequest request) {
        String username = jwtUtil.extractUsername(request.getHeader("Authorization").substring(7));

        if (!likesService.unlike(username, postId)){
            return ResponseEntity.ok().body(new DefaultResponse<>(HttpStatus.BAD_REQUEST, "Error"));
        }

        return ResponseEntity.ok().body(new DefaultResponse<>(HttpStatus.OK, "Success"));
    }
}
