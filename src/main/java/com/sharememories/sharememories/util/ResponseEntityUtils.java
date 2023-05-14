package com.sharememories.sharememories.util;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.LinkedHashMap;
import java.util.Map;

public class ResponseEntityUtils {

    public static ResponseEntity<?> generateResponse(HttpStatus status, String message) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("status", status.value());
        map.put("message", message);

        return ResponseEntity.status(status)
                .body(map);
    }

    public static ResponseEntity<?> generateResponse(HttpStatus status, Map<String, Object> messages) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("status", status.value());
        map.put("messages", messages);

        return ResponseEntity.status(status)
                .body(map);
    }
}
