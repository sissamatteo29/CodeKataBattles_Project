package org.example.battle_microservice.service;

import org.example.battle_microservice.model.BattleModel;
import org.example.battle_microservice.repository.BattleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Service
public class BattleExpirationCheckService {

    private final BattleRepository battleModelRepository;

    private static List<BattleModel> createdRepositories = new ArrayList<>();

    @Autowired
    public BattleExpirationCheckService(BattleRepository battleModelRepository) {
        this.battleModelRepository = battleModelRepository;
    }

    @Scheduled(fixedRate = 60000) // Runs every minute
    public void checkBattleExpirations() {
        System.out.println("Checking if battle is expired");
        List<BattleModel> battles = battleModelRepository.findAll();

        Date currentDate = new Date();

        for (BattleModel battle : battles) {
            if (battle.getSubDeadline() != null && battle.getSubDeadline().before(currentDate)) {
                battle.setEnded(true);
                battleModelRepository.save(battle);
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