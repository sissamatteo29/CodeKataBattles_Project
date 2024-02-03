package org.example.tournament_microservice;

import org.example.tournament_microservice.model.TourIdStudId;
import org.example.tournament_microservice.model.TournamentModel;
import org.example.tournament_microservice.model.TournamentRankingModel;
import org.example.tournament_microservice.repository.TournamentRankingRepository;
import org.example.tournament_microservice.repository.TournamentRepository;
import org.example.tournament_microservice.service.TournamentProducerService;
import org.example.tournament_microservice.service.TournamentRankingService;
import org.example.tournament_microservice.service.TournamentService;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = {tournamentServiceTests.class})
public class tournamentServiceTests {
    @Mock
    TournamentRepository tournamentRepository;
    @Mock
    private TournamentProducerService tournamentProducerService;
    @Mock
    private TournamentRankingRepository tournamentRankingRepository;

    @InjectMocks
    TournamentService tournamentService;

    @InjectMocks
    TournamentRankingService tournamentRankingService;


    @Test
    @Order(1)
    public void test_getTournaments(){
        List<TournamentModel> mytournaments = new ArrayList<>();
        Date date = new Date(2024, Calendar.FEBRUARY, 1);
        mytournaments.add(new TournamentModel("TOURNAMENT1", date, "sara"));
        mytournaments.add(new TournamentModel("TOURNAMENT2", date, "ale"));

        when(tournamentRepository.findAll()).thenReturn(mytournaments);

        assertEquals(2, tournamentService.getTournaments().size());
    }

    @Test
    @Order(2)
    public void test_getTournamentsNameByCreator(){
        List<String> names = new ArrayList<>();
        names.add("TOURNAMENT1");
        String creator = "sara";
        when(tournamentRepository.findNamesByCreator(creator)).thenReturn(names);
        assertEquals(names, tournamentService.getTournamentNamesByCreator(creator));

    }

    @Test
    @Order(3)
    public void test_saveTournament() {
        TournamentModel tournament = new TournamentModel("TOURNAMENT3", new Date(), "john");
        tournamentService.saveTournament(tournament);
        verify(tournamentRepository).save(tournament);
        verify(tournamentProducerService).produceTournamentEvent("New tournament created! Check it: TOURNAMENT3");
    }

    @Test
    @Order(4)
    public void test_endTournament() {
        String tournamentName = "TOURNAMENT5";
        TournamentModel tournament = new TournamentModel(tournamentName, new Date(), "user123");
        when(tournamentRepository.findByName(tournamentName)).thenReturn(Optional.of(tournament));
        when(tournamentRankingRepository.findStudIdsByTourId(tournamentName)).thenReturn(Arrays.asList("user1", "user2"));

        tournamentService.endTournament(tournamentName);

        assertTrue(tournament.getEnded());
        assertNotNull(tournament.getEndDate());

        // Verify that the produceTournamentEventEnding method of the producer service is called with the correct message and user list
        verify(tournamentProducerService).produceTournamentEventEnding(eq("Tournament TOURNAMENT5 is ended! "), eq(Arrays.asList("user1", "user2")));
        // Verify that the save method of the repository is called with the updated tournament
        verify(tournamentRepository).save(tournament);
    }

    @Test
    @Order(5)
    void testFindTourIdsByStudId() {
        String studId = "user123";
        List<String> expectedTourIds = Arrays.asList("TOUR1", "TOUR2");
        when(tournamentRankingRepository.findTourIdsByStudId(studId)).thenReturn(expectedTourIds);

        List<String> actualTourIds = tournamentRankingService.findTourIdsByStudId(studId);

        // Verify that the repository method is called with the correct argument
        verify(tournamentRankingRepository).findTourIdsByStudId(studId);

        // Verify that the result matches the expected values
        assertEquals(expectedTourIds, actualTourIds);
    }

    @Test
    @Order(6)
    void testGetStudAndScoreByTour() {
        String tour = "TOUR1";
        List<Object[]> expectedResults = Arrays.asList(
                new Object[]{"user1", 100},
                new Object[]{"user2", 80}
        );

        when(tournamentRankingRepository.findStudAndScoreByTour(tour)).thenReturn(expectedResults);

        List<Object[]> actualResults = tournamentRankingService.getStudAndScoreByTour(tour);

        // Verify that the repository method is called with the correct argument
        verify(tournamentRankingRepository).findStudAndScoreByTour(tour);
        // Verify that the result matches the expected values
        assertEquals(expectedResults, actualResults);
    }

    @Test
    @Order(7)
    void testAddStudent_EntryDoesNotExist() {
        String tourId = "TOUR1";
        String studId = "user123";
        TourIdStudId expectedId = new TourIdStudId(tourId, studId);

        when(tournamentRankingRepository.findById(expectedId)).thenReturn(Optional.empty());

        ResponseEntity<String> responseEntity = tournamentRankingService.addStudent(tourId, studId);

        // Verify that the repository method is called with the correct argument
        verify(tournamentRankingRepository).findById(expectedId);

        // Verify that the repository's save method is called with the correct argument
        verify(tournamentRankingRepository).save(new TournamentRankingModel(expectedId, 0));

        // Verify the response entity is as expected
        assertEquals(ResponseEntity.ok("Entry added successfully for tourId: " + tourId + " and studId: " + studId), responseEntity);
    }

    @Test
    @Order(8)
    void testAddStudent_EntryAlreadyExists() {
        String tourId = "TOUR2";
        String studId = "user456";

        when(tournamentRankingRepository.findById(any())).thenReturn(Optional.of(new TournamentRankingModel()));

        ResponseEntity<String> responseEntity = tournamentRankingService.addStudent(tourId, studId);

        // Verify that the repository method is called with the correct argument
        verify(tournamentRankingRepository).findById(new TourIdStudId(tourId, studId));

        // Verify that the repository's save method is not called (entry already exists)
        verify(tournamentRankingRepository, never()).save(any());

        // Verify the response entity is as expected
        assertEquals(ResponseEntity.badRequest().body("Entry already exists for tourId: " + tourId + " and studId: " + studId), responseEntity);
    }




}
