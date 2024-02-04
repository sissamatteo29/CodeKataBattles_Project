package org.example.battle_microservice.service;

import org.example.battle_microservice.model.BattleModel;
import org.example.battle_microservice.repository.BattleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class BattleExpirationCheckService {

    private final BattleRepository battleModelRepository;
    private final BattleService battleService;

    private final BattleRankingService battleRankingService;
    private final RestTemplate restTemplate;

    private static List<BattleModel> createdRepositories = new ArrayList<>();

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

    @Scheduled(fixedRate = 60000)
    public void checkRegDeadline() throws URISyntaxException {
        List<BattleModel> battles = battleModelRepository.findAll();
        Date currentDate = new Date();
        System.out.println("The current date is: " + currentDate);

        for (BattleModel battle : battles) {
            if (!containedBattle(createdRepositories, battle) && battle.getRegDeadline() != null && battle.getRegDeadline().before(currentDate)) {
                createdRepositories.add(battle);
                System.out.println("Added a new battle to the list of battles with created repositories: " + battle.getName());
                /* Fire request to the Github microservice */
                HttpClient client = HttpClient.newHttpClient();
                HttpRequest createNewBattleRequest = HttpRequest.newBuilder()
                        .GET()
                        .uri(new URI(String.format("http://localhost:8090/publishRepo/%s/%s", battle.getTournament(), battle.getName())))
                        .build();

                client.sendAsync(createNewBattleRequest, HttpResponse.BodyHandlers.discarding());
            }
        }
    }

    public boolean containedBattle(List<BattleModel> createdRepository, BattleModel battle){
        boolean created = false;
        for(BattleModel curr : createdRepository){
            if(curr.getName().equals(battle.getName()) && curr.getTournament().equals(battle.getTournament())){
                created = true;
            }
        }
        return created;
    }

}