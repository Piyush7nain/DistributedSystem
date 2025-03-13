package org.example.distributedsystem.data;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class DataStore {
    private final Map<String, String> data = new ConcurrentHashMap<>();

    public void saveData(Map<String, String> newData) {
        data.putAll(newData);
    }

    public Map<String, String> getData() {
        return data;
    }
}
