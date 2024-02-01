package org.example.tournament_microservice;

import org.example.tournament_microservice.controller.TournamentController;
import org.example.tournament_microservice.service.TournamentRankingService;
import org.example.tournament_microservice.service.TournamentService;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

@SpringBootTest(classes= {TournamentControllerTests.class})
public class TournamentControllerTests {
    @Mock
    private TournamentRankingService tournamentRankingService;
    @Mock
    private TournamentService tournamentService;
    @InjectMocks
    private TournamentController tournamentController;
    @Test
    @Order(1)
    public void testCreateNewTournament() {
        String name = "Tournament1";
        Date subscriptionDate = new Date();
        String creator = "user123";
        // Mock the tournamentService behavior
        doNothing().when(tournamentService).saveTournament(any());
        // Act
        ResponseEntity<String> responseEntity = tournamentController.createNewTournament(name, subscriptionDate, creator);
        // Assert
        assertEquals(ResponseEntity.ok("Tournament created successfully"), responseEntity);
        // Verify that the saveTournament method of tournamentService is called with the correct arguments
        verify(tournamentService).saveTournament(argThat(tournamentModel ->
                tournamentModel.getName().equals(name) &&
                        tournamentModel.getSubscriptionDeadline().equals(subscriptionDate) &&
                        tournamentModel.getCreator().equals(creator)
        ));
    }

    @Test
    @Order(2)
    public void testGetAllTournaments() {
        // Arrange
        String creatorName = "someCreator";
        List<String> tournamentNames = Arrays.asList("Tournament1", "Tournament2");

        // Mock the tournamentService behavior
        when(tournamentService.getTournamentNamesByCreator(creatorName)).thenReturn(tournamentNames);

        // Act
        ResponseEntity<List<String>> responseEntity = tournamentController.getAllTournaments(creatorName);

        // Assert
        assertEquals(ResponseEntity.ok(tournamentNames), responseEntity);

        // Verify that the getTournamentNamesByCreator method of tournamentService is called with the correct argument
        verify(tournamentService).getTournamentNamesByCreator(eq(creatorName));

        // You can add more verifications as needed based on the behavior of your controller
    }

    @Test
    @Order(3)
    public void testGetAllTournaments_NoContent() {
        // Arrange
        String creatorName = "someCreator";

        // Mock the tournamentService behavior
        when(tournamentService.getTournamentNamesByCreator(creatorName)).thenReturn(null);

        // Act
        ResponseEntity<List<String>> responseEntity = tournamentController.getAllTournaments(creatorName);

        // Assert
        assertEquals(ResponseEntity.noContent().build(), responseEntity);

        // Verify that the getTournamentNamesByCreator method of tournamentService is called with the correct argument
        verify(tournamentService).getTournamentNamesByCreator(eq(creatorName));

        // You can add more verifications as needed based on the behavior of your controller
    }
    @Test
    @Order(4)
    public void testGetAllTournamentsAbs() {
        // Arrange
        List<String> tournamentNames = Arrays.asList("Tournament1", "Tournament2");

        // Mock the tournamentService behavior
        when(tournamentService.getTournaments()).thenReturn(tournamentNames);

        // Act
        ResponseEntity<List<String>> responseEntity = tournamentController.getAllTournamentsAbs();

        // Assert
        assertEquals(ResponseEntity.ok(tournamentNames), responseEntity);

        // Verify that the getTournaments method of tournamentService is called
        verify(tournamentService).getTournaments();

        // You can add more verifications as needed based on the behavior of your controller
    }

    @Test
    @Order(5)
    public void testGetAllTournamentsAbs_NoContent() {
        // Arrange
        // Mock the tournamentService behavior to return an empty list
        when(tournamentService.getTournaments()).thenReturn(null);

        // Act
        ResponseEntity<List<String>> responseEntity = tournamentController.getAllTournamentsAbs();

        // Assert
        assertEquals(ResponseEntity.noContent().build(), responseEntity);

        // Verify that the getTournaments method of tournamentService is called
        verify(tournamentService).getTournaments();

        // You can add more verifications as needed based on the behavior of your controller
    }
    @Test
    @Order(6)
    public void testAddStudent_EntryDoesNotExist() {
        // Arrange
        String tourId = "someTourId";
        String studId = "someStudId";

        // Mock the tournamentRankingService behavior
        when(tournamentRankingService.addStudent(tourId, studId)).thenReturn(ResponseEntity.ok("Entry added successfully"));

        // Act
        ResponseEntity<String> responseEntity = tournamentController.addStudent(tourId, studId);

        // Assert
        assertEquals(ResponseEntity.ok("Entry added successfully"), responseEntity);

        // Verify that the addStudent method of tournamentRankingService is called with the correct arguments
        verify(tournamentRankingService).addStudent(eq(tourId), eq(studId));

    }

    @Test
    @Order(7)
    public void testAddStudent_EntryAlreadyExists() {
        // Arrange
        String tourId = "existingTourId";
        String studId = "existingStudId";

        // Mock the tournamentRankingService behavior
        when(tournamentRankingService.addStudent(tourId, studId)).thenReturn(ResponseEntity.badRequest().body("Entry already exists"));

        // Act
        ResponseEntity<String> responseEntity = tournamentController.addStudent(tourId, studId);

        // Assert
        assertEquals(ResponseEntity.badRequest().body("Entry already exists"), responseEntity);

        // Verify that the addStudent method of tournamentRankingService is called with the correct arguments
        verify(tournamentRankingService).addStudent(eq(tourId), eq(studId));
    }
    @Test
    @Order(8)
    public void testGetTourIdsByStudId() {
        // Arrange
        String studId = "someStudId";
        List<String> tourIds = Arrays.asList("Tour1", "Tour2");

        // Mock the tournamentRankingService behavior
        when(tournamentRankingService.findTourIdsByStudId(studId)).thenReturn(tourIds);

        // Act
        ResponseEntity<List<String>> responseEntity = tournamentController.getTourIdsByStudId(studId);

        // Assert
        assertEquals(ResponseEntity.ok(tourIds), responseEntity);

        // Verify that the findTourIdsByStudId method of tournamentRankingService is called with the correct argument
        verify(tournamentRankingService).findTourIdsByStudId(eq(studId));

        // You can add more verifications as needed based on the behavior of your controller
    }

    @Test
    @Order(9)
    public void testGetStudAndScoreByTour() {
        // Arrange
        String tour = "someTour";
        List<Object[]> studAndScoreList = Arrays.asList(
                new Object[]{"Stud1", 100},
                new Object[]{"Stud2", 75}
        );

        // Mock the tournamentRankingService behavior
        when(tournamentRankingService.getStudAndScoreByTour(tour)).thenReturn(studAndScoreList);

        // Act
        List<Object[]> result = tournamentController.getStudAndScoreByTour(tour);

        // Assert
        assertEquals(studAndScoreList, result);

        // Verify that the getStudAndScoreByTour method of tournamentRankingService is called with the correct argument
        verify(tournamentRankingService).getStudAndScoreByTour(eq(tour));

        // You can add more verifications as needed based on the behavior of your controller
    }

    @Test
    @Order(10)
    public void testEndTournament() {
        // Arrange
        String tournamentName = "someTournament";

        // Mock the tournamentService behavior
        doNothing().when(tournamentService).endTournament(tournamentName);

        // Act
        ResponseEntity<String> responseEntity = tournamentController.endTournament(tournamentName);

        // Assert
        assertEquals(ResponseEntity.ok(tournamentName), responseEntity);

        // Verify that the endTournament method of tournamentService is called with the correct argument
        verify(tournamentService).endTournament(eq(tournamentName));

        // You can add more verifications as needed based on the behavior of your controller
    }


}




