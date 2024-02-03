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

        doNothing().when(tournamentService).saveTournament(any());

        ResponseEntity<String> responseEntity = tournamentController.createNewTournament(name, subscriptionDate, creator);

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
        String creatorName = "someCreator";
        List<String> tournamentNames = Arrays.asList("Tournament1", "Tournament2");

        when(tournamentService.getTournamentNamesByCreator(creatorName)).thenReturn(tournamentNames);

        ResponseEntity<List<String>> responseEntity = tournamentController.getAllTournaments(creatorName);

        // Assert
        assertEquals(ResponseEntity.ok(tournamentNames), responseEntity);

        // Verify that the getTournamentNamesByCreator method of tournamentService is called with the correct argument
        verify(tournamentService).getTournamentNamesByCreator(eq(creatorName));

    }

    @Test
    @Order(3)
    public void testGetAllTournaments_NoContent() {
        String creatorName = "someCreator";

        when(tournamentService.getTournamentNamesByCreator(creatorName)).thenReturn(null);

        ResponseEntity<List<String>> responseEntity = tournamentController.getAllTournaments(creatorName);

        // Assert
        assertEquals(ResponseEntity.noContent().build(), responseEntity);

        // Verify that the getTournamentNamesByCreator method of tournamentService is called with the correct argument
        verify(tournamentService).getTournamentNamesByCreator(eq(creatorName));
    }
    @Test
    @Order(4)
    public void testGetAllTournamentsAbs() {
        List<String> tournamentNames = Arrays.asList("Tournament1", "Tournament2");

        when(tournamentService.getTournaments()).thenReturn(tournamentNames);

        ResponseEntity<List<String>> responseEntity = tournamentController.getAllTournamentsAbs();

        // Assert
        assertEquals(ResponseEntity.ok(tournamentNames), responseEntity);

        // Verify that the getTournaments method of tournamentService is called
        verify(tournamentService).getTournaments();
    }

    @Test
    @Order(5)
    public void testGetAllTournamentsAbs_NoContent() {

        when(tournamentService.getTournaments()).thenReturn(null);

        ResponseEntity<List<String>> responseEntity = tournamentController.getAllTournamentsAbs();

        // Assert
        assertEquals(ResponseEntity.noContent().build(), responseEntity);

        // Verify that the getTournaments method of tournamentService is called
        verify(tournamentService).getTournaments();
    }
    @Test
    @Order(6)
    public void testAddStudent_EntryDoesNotExist() {
        String tourId = "someTourId";
        String studId = "someStudId";

        when(tournamentRankingService.addStudent(tourId, studId)).thenReturn(ResponseEntity.ok("Entry added successfully"));

        ResponseEntity<String> responseEntity = tournamentController.addStudent(tourId, studId);
        // Assert
        assertEquals(ResponseEntity.ok("Entry added successfully"), responseEntity);

        // Verify that the addStudent method of tournamentRankingService is called with the correct arguments
        verify(tournamentRankingService).addStudent(eq(tourId), eq(studId));

    }

    @Test
    @Order(7)
    public void testAddStudent_EntryAlreadyExists() {
        String tourId = "existingTourId";
        String studId = "existingStudId";

        when(tournamentRankingService.addStudent(tourId, studId)).thenReturn(ResponseEntity.badRequest().body("Entry already exists"));

        ResponseEntity<String> responseEntity = tournamentController.addStudent(tourId, studId);

        // Assert
        assertEquals(ResponseEntity.badRequest().body("Entry already exists"), responseEntity);

        // Verify that the addStudent method of tournamentRankingService is called with the correct arguments
        verify(tournamentRankingService).addStudent(eq(tourId), eq(studId));
    }
    @Test
    @Order(8)
    public void testGetTourIdsByStudId() {
        String studId = "someStudId";
        List<String> tourIds = Arrays.asList("Tour1", "Tour2");

        when(tournamentRankingService.findTourIdsByStudId(studId)).thenReturn(tourIds);

        ResponseEntity<List<String>> responseEntity = tournamentController.getTourIdsByStudId(studId);

        // Assert
        assertEquals(ResponseEntity.ok(tourIds), responseEntity);

        // Verify that the findTourIdsByStudId method of tournamentRankingService is called with the correct argument
        verify(tournamentRankingService).findTourIdsByStudId(eq(studId));

    }

    @Test
    @Order(9)
    public void testGetStudAndScoreByTour() {
        String tour = "someTour";
        List<Object[]> studAndScoreList = Arrays.asList(
                new Object[]{"Stud1", 100},
                new Object[]{"Stud2", 75}
        );

        when(tournamentRankingService.getStudAndScoreByTour(tour)).thenReturn(studAndScoreList);

        List<Object[]> result = tournamentController.getStudAndScoreByTour(tour);
        // Assert
        assertEquals(studAndScoreList, result);
        // Verify that the getStudAndScoreByTour method of tournamentRankingService is called with the correct argument
        verify(tournamentRankingService).getStudAndScoreByTour(eq(tour));
    }

    @Test
    @Order(10)
    public void testEndTournament() {
        String tournamentName = "someTournament";
        doNothing().when(tournamentService).endTournament(tournamentName);
        ResponseEntity<String> responseEntity = tournamentController.endTournament(tournamentName);
        // Assert
        assertEquals(ResponseEntity.ok(tournamentName), responseEntity);
        // Verify that the endTournament method of tournamentService is called with the correct argument
        verify(tournamentService).endTournament(eq(tournamentName));
    }


}




