package com.rmarcello.note.service;

import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;

@Service
public class ApiKeyService {

    private static final String API_KEY_FILE = "apikey-list.txt";

    private final Set<String> validApiKeys = new HashSet<>();

    public ApiKeyService() {
        loadApiKeys();
    }

    private void loadApiKeys() {
        InputStream is = getClass().getClassLoader().getResourceAsStream(API_KEY_FILE);
        if (is == null) {
            throw new RuntimeException("API key file not found: " + API_KEY_FILE);
        }
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (!line.isEmpty()) {
                    validApiKeys.add(line);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to load API keys from " + API_KEY_FILE, e);
        }
    }

    public boolean isValidApiKey(String apiKey) {
        return apiKey != null && validApiKeys.contains(apiKey);
    }
}
