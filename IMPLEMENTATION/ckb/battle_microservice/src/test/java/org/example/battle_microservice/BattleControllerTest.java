package org.example.battle_microservice;

import org.example.battle_microservice.controller.BattleController;
import org.example.battle_microservice.model.BattleModel;
import org.example.battle_microservice.service.BattleRankingService;
import org.example.battle_microservice.service.BattleService;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.Base64;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = {BattleServiceTest.class})
public class BattleControllerTest {
    @Mock
    private BattleRankingService battleRankingService;
    @Mock
    private BattleService battleService;
    @InjectMocks
    private BattleController battleController;


    @Test
    @Order(1)
    void testCreateNewBattle() {
        String name = "Battle1";
        String tournament = "Tournament1";
        String automationBuildScript = Base64.getUrlEncoder().encodeToString("AutomationScript".getBytes());
        String codeTest = Base64.getUrlEncoder().encodeToString("CodeTest".getBytes());
        String code = Base64.getUrlEncoder().encodeToString("Code".getBytes());
        int maxTeamSize = 5;
        int minTeamSize = 3;
        String repositoryLink = "https://github.com/battle";
        boolean manualEvaluation = true;
        String regDeadline = "2022-03-01";
        String subDeadline = "2022-03-15";
        String creator = "Admin";

        doNothing().when(battleService).saveBattle(any(BattleModel.class));

        ResponseEntity<String> responseEntity = battleController.createNewBattle(
                name, tournament, automationBuildScript, codeTest, code, maxTeamSize, minTeamSize,
                repositoryLink, manualEvaluation, regDeadline, subDeadline, creator);

        // Assert
        assertEquals("Battle created successfully", responseEntity.getBody());
    }

    @Test
    @Order(2)
    void testGetBattlesByTournament() {
        String tournamentName = "Tournament1";
        List<String> expectedBattles = Arrays.asList("Battle1", "Battle2");

        when(battleService.getBattlesByTournament(tournamentName)).thenReturn(expectedBattles);

        List<String> actualBattles = battleController.getBattlesByTournament(tournamentName);

        // Assert
        assertEquals(expectedBattles, actualBattles);
    }
    @Test
    @Order(3)
    void testAddStudent() {
        String tour = "Tour1";
        String battle = "Battle1";
        String stud = "Student1";
        String team = "Team1";

        when(battleRankingService.addStudent(tour, battle, stud, team))
                .thenReturn(ResponseEntity.ok("Entry added successfully"));

        ResponseEntity<String> responseEntity = battleController.addStudent(tour, battle, stud, team);

        // Assert
        assertEquals(ResponseEntity.ok("Entry added successfully"), responseEntity);
    }

    @Test
    @Order(4)
    void testGetBattlesByTourAndStud() {
        String tour = "Tour1";
        String stud = "Student1";
        List<String> expectedBattles = Arrays.asList("Battle1", "Battle2");

        when(battleRankingService.getBattlesByTourAndStud(tour, stud))
                .thenReturn(expectedBattles);

        ResponseEntity<List<String>> responseEntity = battleController.getBattlesByTourAndStud(tour, stud);

        // Assert
        assertEquals(ResponseEntity.ok(expectedBattles), responseEntity);
    }

    @Test
    @Order(5)
    void testGetScoreByTourBattleStud() {
        String tour = "Tour1";
        String battle = "Battle1";
        String stud = "Student1";
        int expectedScore = 50;

        when(battleRankingService.getScoreByTourBattleStud(tour, battle, stud))
                .thenReturn(expectedScore);

        Integer resultScore = battleController.getScoreByTourBattleStud(tour, battle, stud);

        // Assert
        assertEquals(expectedScore, resultScore);
    }

    @Test
    @Order(6)
    void testGetDistinctTeamNameAndScoreByTourAndBattle() {
        String tour = "Tour1";
        String battle = "Battle1";
        Object[] expectedData1 = {"TeamA", 100};
        Object[] expectedData2 = {"TeamB", 80};
        List<Object[]> expectedResults = Arrays.asList(expectedData1, expectedData2);

        when(battleRankingService.getDistinctTeamNameAndScoreByTourAndBattle(tour, battle))
                .thenReturn(expectedResults);

        List<Object[]> actualResults = battleController
                .getDistinctTeamNameAndScoreByTourAndBattle(tour, battle);

        // Assert
        assertEquals(expectedResults, actualResults);
    }



}
