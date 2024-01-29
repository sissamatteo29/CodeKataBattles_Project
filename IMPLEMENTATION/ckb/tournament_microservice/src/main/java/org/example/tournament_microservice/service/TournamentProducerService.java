package org.example.tournament_microservice.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class TournamentProducerService {

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    public void produceTournamentEvent(String message) {
        kafkaTemplate.send("topic-new-tournament", message);
    }
}
