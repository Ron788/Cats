package ru.vsu.cs.ustinov.cats.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {
    /*
    Просто контроллер, для доступа к которому обязательна авторизация
     */
    @GetMapping("/test")
    public ResponseEntity<?> test() {
        return ResponseEntity.ok().body("test");
    }
}
