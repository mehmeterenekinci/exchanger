package com.exchanger.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.ResponseEntity;

@Data
@AllArgsConstructor
public class StandardResponse<T> {
    private int code;
    private String message;
    private T data;

    public static <T> ResponseEntity<StandardResponse<T>> success(T data) {
        return ResponseEntity.ok(new StandardResponse<>(200, "Success", data));
    }

    public static <T> ResponseEntity<StandardResponse<T>> success(String message, T data) {
        return ResponseEntity.ok(new StandardResponse<>(200, message, data));
    }

    public static <T> ResponseEntity<StandardResponse<T>> error(int code, String message) {
        return ResponseEntity.status(code).body(new StandardResponse<>(code, message, null));
    }

    public static <T> ResponseEntity<StandardResponse<T>> badRequest(String message) {
        return error(400, message);
    }
}