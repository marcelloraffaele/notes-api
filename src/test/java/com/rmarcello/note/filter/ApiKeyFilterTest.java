package com.rmarcello.note.filter;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        classes = com.rmarcello.note.SpringBootDemoApplication.class)
class ApiKeyFilterTest {

    private static final String VALID_API_KEY = "apikey-1234567890";
    private static final String INVALID_API_KEY = "invalid-key";

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void testRequestWithoutApiKey_returns401() {
        ResponseEntity<String> response = restTemplate.getForEntity("/notes", String.class);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    void testRequestWithInvalidApiKey_returns401() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-API-KEY", INVALID_API_KEY);
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange("/notes", HttpMethod.GET, entity, String.class);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    void testRequestWithValidApiKey_returns200() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-API-KEY", VALID_API_KEY);
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange("/notes", HttpMethod.GET, entity, String.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }
}
