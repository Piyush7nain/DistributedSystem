package org.example.distributedsystem.controllers;

import org.example.distributedsystem.data.DataStore;
import org.example.distributedsystem.registry.FollowerRegistry;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@RestController
@RequestMapping("/data")
public class LeaderController {

    private final RestTemplate restTemplate = new RestTemplate();
    private final DataStore dataStore;
    private final FollowerRegistry followerRegistry;

    public LeaderController(DataStore dataStore, FollowerRegistry followerRegistry) {
        this.dataStore = dataStore;
        this.followerRegistry = followerRegistry;
    }

    @GetMapping("/health")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("Leader is healthy");
    }

    @PostMapping
    public ResponseEntity<String> saveData(@RequestBody Map<String, String> request) {
        dataStore.saveData(request);
        System.out.println("Saved data");
        propagateToFollowers(request);
        return ResponseEntity.ok("Data saved successfully");
    }

    private void propagateToFollowers(Map<String, String> data) {
        followerRegistry.getUrls().forEach(url -> {
            try {
                restTemplate.postForEntity(url + "/replica", data, String.class);
                System.out.println("Propagated data to: " + url);
            } catch (Exception e) {
                System.err.println("Failed to propagate data to: " + url);
                e.printStackTrace();
            }
        });
    }
}