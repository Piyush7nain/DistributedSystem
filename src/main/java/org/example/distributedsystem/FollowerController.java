package org.example.distributedsystem;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/replica")
public class FollowerController {
    private final Map<String, String> dataStore = new ConcurrentHashMap<>();

    @GetMapping("/health")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("Follower is healthy");
    }

    @PostMapping
    public ResponseEntity<String> replicateData(@RequestBody Map<String, String> request) {
        dataStore.putAll(request);
        System.out.println("Data Received Successfully");
        return ResponseEntity.ok("Data replicated successfully");
    }
}