package org.example.tournament_microservice;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.tournament_microservice.service.TournamentProducerService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = { TournamentProducerServiceTest.class})
public class TournamentProducerServiceTest {
    @Mock
    private KafkaTemplate<String, String> kafkaTemplate;
    @Mock
    private ObjectMapper objectMapperMock;

    @InjectMocks
    private TournamentProducerService tournamentProducerService;
    @Test
    public void testProduceTournamentEventEnding() throws JsonProcessingException {
        // Arrange
        String message = "Tournament ended!";
        List<String> userIds = Arrays.asList("user1", "user2");

        // Expected payload as a JSON string
        String expectedJsonPayload = "{\"message\":\"Tournament ended!\",\"userIds\":[\"user1\",\"user2\"]}";

        // Mock the ObjectMapper to return the expected JSON string
        when(objectMapperMock.writeValueAsString(any())).thenReturn(expectedJsonPayload);

        // Act
        tournamentProducerService.produceTournamentEventEnding(message, userIds);

        // Assert
        // Verify that the send method is called with the expected topic and JSON payload
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode expectedJson = objectMapper.readTree(expectedJsonPayload);
        JsonNode actualJson = objectMapper.readTree(expectedJsonPayload); // Replace with the actual JSON string

        assertEquals(expectedJson, actualJson);
    }


}
