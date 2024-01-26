package org.example.battle_microservice.service;

import org.example.battle_microservice.model.BattleModel;
import org.example.battle_microservice.repository.BattleRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BattleService {
    private final BattleRepository battleRepository;
    public BattleService(BattleRepository userRepository) {
        this.battleRepository = userRepository;
    }

    public void saveBattle(BattleModel battle) {
        System.out.println("Saving the battle");
        battleRepository.save(battle);
    }
    public List<String> getBattlesByTournament(String tournamentName) {
        List<BattleModel> battles = battleRepository.findByTournament(tournamentName);
        return battles.stream().map(BattleModel::getName).collect(Collectors.toList());
    }

}
