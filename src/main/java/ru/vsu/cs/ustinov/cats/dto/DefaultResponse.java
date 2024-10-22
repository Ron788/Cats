package ru.vsu.cs.ustinov.cats.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Data
@AllArgsConstructor
public class DefaultResponse<T> {
    HttpStatus status;
    T result;

    public DefaultResponse<T> build(){
        return this;
    }

    public static <T> ResponseEntity<DefaultResponse<T>> ok(T result){
        return ResponseEntity.ok(new DefaultResponse<>(HttpStatus.OK, result));
    }

    public static <T> ResponseEntity<DefaultResponse<T>> badRequest(T result){
        return ResponseEntity.badRequest().body(new DefaultResponse<>(HttpStatus.BAD_REQUEST, result));
    }

    public static <T> ResponseEntity<DefaultResponse<T>> unauthorized(T result){
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new DefaultResponse<>(HttpStatus.UNAUTHORIZED, result));
    }
}
