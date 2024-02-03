package org.example.battle_microservice;

import org.example.battle_microservice.model.BattleModel;
import org.example.battle_microservice.model.BattleRankingModel;
import org.example.battle_microservice.repository.BattleRankingRepository;
import org.example.battle_microservice.repository.BattleRepository;
import org.example.battle_microservice.service.BattleRankingService;
import org.example.battle_microservice.service.BattleService;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = BattleServiceTest.class)
public class BattleServiceTest {
    @Mock
    BattleRepository battleRepository;
    @Mock
    private BattleRankingRepository battleRankingRepository;
    @InjectMocks
    BattleService battleService;
    @InjectMocks
    BattleRankingService battleRankingService;
    @Test
    @Order(1)
    public void testSaveBattle() {
        BattleModel battle = new BattleModel("Battle1", "Tournament1", null, null, null,
                5, 3, "http://repository.com", true, new Date(), new Date(), "Creator1");
        battleService.saveBattle(battle);
        // Assert
        // Verify that the save method of the repository is called with the correct argument
        verify(battleRepository).save(eq(battle));
    }
    @Test
    @Order(2)
    public void testGetBattlesByTournament() {
        String tournamentName = "Tournament1";
        List<BattleModel> mockBattles = Arrays.asList(
                new BattleModel("Battle1", tournamentName, null, null, null, 5, 3, "http://repository.com", true, null, null, "Creator1"),
                new BattleModel("Battle2", tournamentName, null, null, null, 4, 2, "http://repository.com", false, null, null, "Creator2")
        );

        when(battleRepository.findByTournament(tournamentName)).thenReturn(mockBattles);

        List<String> result = battleService.getBattlesByTournament(tournamentName);

        // Assert
        assertEquals(Arrays.asList("Battle1", "Battle2"), result);
    }
    @Test
    @Order(3)
    void testGetBattlesByTourAndStud() {
        String tour = "Tournament1";
        String stud = "Student1";
        List<String> expectedBattles = Arrays.asList("Battle1", "Battle2");

        when(battleRankingRepository.findBattlesByTourAndStud(anyString(), anyString()))
                .thenReturn(expectedBattles);

        List<String> actualBattles = battleRankingService.getBattlesByTourAndStud(tour, stud);

        // Assert
        assertEquals(expectedBattles, actualBattles);
    }
    @Test
    @Order(4)
    void testGetScoreByTourBattleStud() {
        String tour = "Tournament1";
        String battle = "Battle1";
        String stud = "Student1";
        int expectedScore = 100;

        when(battleRankingRepository.findScoreByTourBattleStud(anyString(), anyString(), anyString()))
                .thenReturn(expectedScore);

        int actualScore = battleRankingService.getScoreByTourBattleStud(tour, battle, stud);

        // Assert
        assertEquals(expectedScore, actualScore);
    }
    @Test
    @Order(5)
    void testAddStudent_EntryDoesNotExist() {
        String tour = "Tournament1";
        String battle = "Battle1";
        String stud = "Student1";
        String team = "Team1";

        when(battleRankingRepository.findById(any())).thenReturn(Optional.empty());

        ResponseEntity<String> responseEntity = battleRankingService.addStudent(tour, battle, stud, team);

        // Assert
        assertEquals(ResponseEntity.ok("Entry added successfully for tour: " + tour +
                ", battle: " + battle + ", stud: " + stud + ", and team: " + team), responseEntity);
    }

    @Test
    @Order(6)
    void testAddStudent_EntryExists() {
        String tour = "Tournament1";
        String battle = "Battle1";
        String stud = "Student1";
        String team = "Team1";

        when(battleRankingRepository.findById(any())).thenReturn(Optional.of(new BattleRankingModel()));

        ResponseEntity<String> responseEntity = battleRankingService.addStudent(tour, battle, stud, team);

        // Assert
        assertEquals(ResponseEntity.badRequest().body("Entry already exists for tour: " + tour +
                ", battle: " + battle + ", and stud: " + stud), responseEntity);
    }
    @Test
    @Order(7)
    void testGetDistinctTeamNameAndScoreByTourAndBattle() {
        String tour = "Tournament1";
        String battle = "Battle1";

        when(battleRankingRepository.findDistinctTeamNameAndScoreByTourAndBattle(anyString(), anyString()))
                .thenReturn(Arrays.asList(new Object[]{"Team1", 100}, new Object[]{"Team2", 90}));

        List<Object[]> result = battleRankingService.getDistinctTeamNameAndScoreByTourAndBattle(tour, battle);

        // Assert
        assertEquals(2, result.size());
        assertEquals("Team1", result.get(0)[0]);
        assertEquals(100, result.get(0)[1]);
        assertEquals("Team2", result.get(1)[0]);
        assertEquals(90, result.get(1)[1]);
    }




}
