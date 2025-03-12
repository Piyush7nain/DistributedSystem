package org.example.distributedsystem;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/data")
public class LeaderController {
    private final Map<String, String> dataStore = new ConcurrentHashMap<>();
    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${follower.service.name}")
    private String followerServiceName;

    @Value("${follower.service.port}")
    private String followerServicePort;

    @Value("${node.role}")
    private String nodeRole;

    private final List<String> followerUrls = new CopyOnWriteArrayList<>();

    @PostConstruct
    public void discoverFollowers() {
        updateFollowerUrls();
    }

    public void updateFollowerUrls() {
        int retries = 5;
        if("leader".equalsIgnoreCase(nodeRole)){
            while (retries-- > 0) {
                try {
                    InetAddress[] addresses = InetAddress.getAllByName(followerServiceName);
                    List<String> newFollowerUrls = Arrays.stream(addresses)
                            .map(addr -> "http://" + addr.getHostAddress() + ":" + followerServicePort)
                            .collect(Collectors.toList());
                    followerUrls.clear();
                    followerUrls.addAll(newFollowerUrls);
                    System.out.println("Discovered followers: " + followerUrls);
                    if (!followerUrls.isEmpty()) break;
                    Thread.sleep(2000); // Wait 2 seconds before retrying
                } catch (UnknownHostException | InterruptedException e) {
                    System.err.println("Retrying follower discovery: " + e.getMessage());
                }
            }
        } else{
            System.out.println("Skipping follower discovery on follower node ");
        }
    }

    @GetMapping("/health")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("Leader is healthy");
    }

    @PostMapping
    public ResponseEntity<String> saveData(@RequestBody Map<String, String> request) {
        dataStore.putAll(request);
        System.out.println("Saved data");
        propagateToFollowers(request);
        return ResponseEntity.ok("Data saved successfully");
    }

    private void propagateToFollowers(Map<String, String> data) {
        followerUrls.forEach(url -> {
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