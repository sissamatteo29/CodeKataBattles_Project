package org.example.notification_microservice.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    @KafkaListener(topics = "topic-ended-tournament", groupId = "notification_group")
    public void receiveMessageEnding(String jsonString) {
        try {
            // Deserialize the JSON string into a Map
            Map<String, Object> payload = new ObjectMapper().readValue(jsonString, Map.class);

            // Extract the message and userIds from the payload
            String message = (String) payload.get("message");
            List<String> userIds = (List<String>) payload.get("userIds");

            // Handle the extracted information as needed
            sendNotificationToUsers(message, userIds);
        } catch (Exception e) {
            // Handle the exception appropriately (e.g., log an error)
            e.printStackTrace();
        }
    }

    private void sendNotificationToUsers(String message, List<String> userIds) {
        String url = "http://localhost:8086/saveMessageToUser";
        System.out.println("Users to reach: "+userIds);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        for (String userId : userIds) {
            // For each userId, send a separate request to save the message
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("message", message);
            requestBody.put("userId", userId);

            ResponseEntity<String> response = restTemplate.postForEntity(url, requestBody, String.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                System.out.println("Success sending message to user " + userId + "ending of tournament");
            } else {
                System.out.println("Error sending message to user " + userId);
            }
        }
    }

}
