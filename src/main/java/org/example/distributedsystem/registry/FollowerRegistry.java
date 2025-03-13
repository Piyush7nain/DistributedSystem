package org.example.distributedsystem.registry;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

@EnableScheduling
@Configuration
public class FollowerRegistry implements Registry {

    @Value("${follower.service.name}")
    private String followerServiceName;

    private final String followerServicePort = "8080";

    @Value("${node.role}")
    private String nodeRole;

    List<String> followerUrls = new CopyOnWriteArrayList<>();
    @Override
    @Scheduled(fixedRateString = "${discovery.rate:10000}")
    public void updateUrls() {
        if(nodeRole!=null && nodeRole.equalsIgnoreCase("follower")){
            System.out.println("Skipping follower registry update");
            return;
        }
        int retries = 5;
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
    }

    @Override
    public List<String> getUrls() {
        return new ArrayList<>(followerUrls);
    }
}
