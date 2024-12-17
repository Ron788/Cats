package ru.vsu.cs.ustinov.cats.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import ru.vsu.cs.ustinov.cats.dto.DefaultResponse;
import ru.vsu.cs.ustinov.cats.dto.posts.CreatePostRequest;
import ru.vsu.cs.ustinov.cats.dto.posts.DeletePostRequest;
import ru.vsu.cs.ustinov.cats.jwt.JwtUtil;
import ru.vsu.cs.ustinov.cats.model.Post;
import ru.vsu.cs.ustinov.cats.model.User;
import ru.vsu.cs.ustinov.cats.service.PostsService;
import ru.vsu.cs.ustinov.cats.service.UserService;

import java.util.Optional;

@AllArgsConstructor
@RestController
@RequestMapping("/api/post/")
public class PostsController {

    PostsService postsService;
    UserService userService;

    JwtUtil jwtUtil;

    @PostMapping("/new")
    public ResponseEntity<DefaultResponse<String>> newPost(@RequestBody CreatePostRequest postRequest, @AuthenticationPrincipal UserDetails userDetails) {
        String username = userDetails.getUsername();
        Optional<User> user = userService.findByUsername(username);
        if (user.isEmpty()){
            return DefaultResponse.badRequest("User not found");
        }
        if (!postsService.newPost(user.get(), postRequest.getTitle(), postRequest.getBody())){
            return ResponseEntity.badRequest().body(new DefaultResponse<>(HttpStatus.BAD_REQUEST, "User not found"));
        }

        return ResponseEntity.ok().body(new DefaultResponse<>(HttpStatus.OK, "Post created"));
    }

    @PostMapping("/delete")
    public ResponseEntity<DefaultResponse<String>> deletePost(@RequestBody DeletePostRequest postRequest, HttpServletRequest request) {
        String username = jwtUtil.extractUsername(request.getHeader("Authorization").substring(7));

        if (!postsService.deletePost(username, postRequest.getId())){
            return ResponseEntity.badRequest().body(new DefaultResponse<>(HttpStatus.BAD_REQUEST, "Error"));
        }

        return ResponseEntity.ok().body(new DefaultResponse<>(HttpStatus.OK, "Post deleted"));
    }

    @GetMapping("/{postId}")
    public ResponseEntity<DefaultResponse<String>> getPost(@PathVariable Long postId){
        Post post = postsService.getPost(postId);

        if (post == null){
            return ResponseEntity.badRequest().body(new DefaultResponse<>(HttpStatus.BAD_REQUEST, "Post not found"));
        }

        return ResponseEntity.ok().body(new DefaultResponse<>(HttpStatus.OK, "Post found")
                .addData("title", post.getTitle()).addData("body", post.getBody())
                .addData("creatorUsername", post.getUser().getUsername()).addData("postId", post.getPostId()));
    }
}
