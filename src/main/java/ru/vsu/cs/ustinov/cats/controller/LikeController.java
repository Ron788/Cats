package ru.vsu.cs.ustinov.cats.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.vsu.cs.ustinov.cats.service.LikeService;

import java.security.Principal;

@RestController
@RequestMapping("/api/posts")
@AllArgsConstructor
public class LikeController {

    LikeService likeService;

    @PostMapping("likePost/{postId}")
    public ResponseEntity<?> likePost(@PathVariable Long postId, Principal principal) {
        likeService.likePost(postId, principal.getName());

        return ResponseEntity.ok("Like add success");
    }

    @PostMapping("removeLike/{postId}")
    public ResponseEntity<?> removeLike(@PathVariable Long postId, Principal principal) {
        likeService.removeLike(postId, principal.getName());

        return ResponseEntity.ok("Remove like success");
    }
}
