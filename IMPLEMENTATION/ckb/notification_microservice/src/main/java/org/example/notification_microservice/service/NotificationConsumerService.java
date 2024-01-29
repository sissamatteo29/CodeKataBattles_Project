package org.example.notification_microservice.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class NotificationConsumerService {
    private final RestTemplate restTemplate = new RestTemplate();
    @KafkaListener(topics = "topic-new-tournament", groupId = "notification_group")
    public void receiveMessage(String message) {
        String url = "http://localhost:8086/saveMessage";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> request = new HttpEntity<>(message, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);

        if (response.getStatusCode().is2xxSuccessful()) {
            System.out.println("Success sending message (new tournament)!");
        } else {
            System.out.println("Error sending message (new tournament)");
        }
    }
}
