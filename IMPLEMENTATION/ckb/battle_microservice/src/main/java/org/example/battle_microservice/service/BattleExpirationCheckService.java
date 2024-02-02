package org.example.battle_microservice.service;

import org.example.battle_microservice.model.BattleModel;
import org.example.battle_microservice.repository.BattleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Date;
import java.util.List;

@Service
public class BattleExpirationCheckService {

    private final BattleRepository battleModelRepository;

    private final BattleService battleService;

    private final BattleRankingService battleRankingService;
    private final RestTemplate restTemplate;


    @Autowired
    public BattleExpirationCheckService(BattleRepository battleModelRepository,
                                        BattleService battleService, BattleRankingService battleRankingService) {
        this.battleModelRepository = battleModelRepository;
        this.battleService = battleService;
        this.battleRankingService = battleRankingService;
        this.restTemplate = new RestTemplate();
    }


    @Scheduled(fixedRate = 60000) // Runs every minute
    public void checkBattleExpirations() {
        System.out.println("Checking if battle is expired");
        List<BattleModel> battles = battleModelRepository.findAll();

        Date currentDate = new Date();

        for (BattleModel battle : battles) {
            // check if battle has not already been ended and if battle submission deadline has passed
            if (!battle.getEnded() && battle.getSubDeadline() != null && battle.getSubDeadline().before(currentDate)) {

                //retrieves list of students and their score in the battle
                List<Object[]> stud_score = battleRankingService.getStudAndScoreByTourAndBattle(battle.getTournament(), battle.getName());
                // I think I can just send the entire list to the tournament
                String tournamentUrl = "http://localhost:8085/updateResultsBattle"
                                        + "?tour=" + battle.getTournament();
                restTemplate.postForObject(tournamentUrl,stud_score, String.class );
                // update database that battle ended
                battleService.markBattleAsEnded(battle.getId());
            }
        }
    }
}