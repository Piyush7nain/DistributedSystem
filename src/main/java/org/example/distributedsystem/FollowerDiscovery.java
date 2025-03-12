package org.example.distributedsystem;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
@EnableScheduling
public class FollowerDiscovery {
//    @Value("dicovery.scheduler.rate")
//    private long rate;

    private final LeaderController leaderController;

    public FollowerDiscovery(LeaderController leaderController) {
        this.leaderController = leaderController;
    }

    @Scheduled(fixedRateString = "${follower.discovery.rate:10000}")
    public void refreshFollowerIPs() {
        System.out.println("Updating follower IPs");
        leaderController.updateFollowerUrls();
    }
}
