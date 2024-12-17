package ru.vsu.cs.ustinov.cats.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;

/**
 * Дабы уменьшить число дублируемого кода в контроллерах  и стандартизировать вид ответов
 * написал эту обертку небольшую
 * @param <T>
 */
@Data
@AllArgsConstructor
public class DefaultResponse<T> {
    HttpStatus status;
    T result;
    Map<String, Object> additionalData;

    public DefaultResponse(HttpStatus status, T result) {
        this(status, result, new HashMap<>());
    }

    public DefaultResponse<T> addData(String key, Object value) {
        additionalData.put(key, value);
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
