package org.example.distributedsystem.registry;

import java.util.List;

public interface Registry {
    void updateUrls();
    List<String> getUrls();
}
