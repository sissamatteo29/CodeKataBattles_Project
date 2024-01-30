package org.example.tournament_microservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class TournamentProducerService {

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    public void produceTournamentEvent(String message) {
        kafkaTemplate.send("topic-new-tournament", message);
    }

    public void produceTournamentEventEnding(String message, List<String> userIds) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("message", message);
        payload.put("userIds", userIds);

        try {
            // Convert the payload to a JSON string
            String jsonString = new ObjectMapper().writeValueAsString(payload);
            System.out.println("Sending kafka ended tournament message");
            // Send the JSON string as the message to the Kafka topic
            kafkaTemplate.send("topic-ended-tournament", jsonString);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }
}
