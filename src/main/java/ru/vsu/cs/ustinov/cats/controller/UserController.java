package ru.vsu.cs.ustinov.cats.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.vsu.cs.ustinov.cats.dto.DefaultResponse;
import ru.vsu.cs.ustinov.cats.model.User;
import ru.vsu.cs.ustinov.cats.service.UserService;

@AllArgsConstructor
@RestController
@RequestMapping("/api/user")
public class UserController {
    private final UserService userService;

    @GetMapping("/{userId}")
    public ResponseEntity<DefaultResponse<String>> getUser(@PathVariable("userId") Long userId) {
        User user = userService.getUser(userId);

        if (user == null) {
            return ResponseEntity.badRequest().body(new DefaultResponse<>(HttpStatus.BAD_REQUEST, "User not found"));
        }

        return ResponseEntity.ok().body(new DefaultResponse<>(HttpStatus.OK, "User found")
                .addData("userId", userId)
                .addData("username", user.getUsername()));
    }
}
