package org.example.distributedsystem.registry;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

@EnableScheduling
@Configuration
public class LeaderRegistry implements Registry {

    @Value("${leader.service.name}")
    private String leaderServiceName;

    @Value("${node.role}")
    private String nodeRole;

    private final String leaderServicePort = "8080";

    List<String> leaderUrls = new CopyOnWriteArrayList<>();

    @Override
    @Scheduled(fixedRateString = "${discovery.rate:10000}")
    public void updateUrls() {
        if(nodeRole!=null && nodeRole.equalsIgnoreCase("leader")){
            System.out.println("Skipping leader registry update");
            return;
        }
        int retries = 5;
        while (retries-- > 0) {
            try {
                InetAddress[] addresses = InetAddress.getAllByName(leaderServiceName);
                List<String> newFollowerUrls = Arrays.stream(addresses)
                        .map(addr -> "http://" + addr.getHostAddress() + ":" + leaderServicePort)
                        .collect(Collectors.toList());
                leaderUrls.clear();
                leaderUrls.addAll(newFollowerUrls);
                System.out.println("Discovered Leaders: " + leaderUrls);
                if (!leaderUrls.isEmpty()) break;
                Thread.sleep(2000); // Wait 2 seconds before retrying
            } catch (UnknownHostException | InterruptedException e) {
                System.err.println("Retrying follower discovery: " + e.getMessage());
            }
        }
    }

    @Override
    public List<String> getUrls() {
        return new ArrayList<>(leaderUrls);
    }
}
