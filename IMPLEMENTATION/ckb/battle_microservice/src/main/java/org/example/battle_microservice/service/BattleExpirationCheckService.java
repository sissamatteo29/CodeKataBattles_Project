package org.example.battle_microservice.service;

import org.example.battle_microservice.model.BattleModel;
import org.example.battle_microservice.repository.BattleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import java.util.Date;
import java.util.List;

@Service
public class BattleExpirationCheckService {

    private final BattleRepository battleModelRepository;

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
}