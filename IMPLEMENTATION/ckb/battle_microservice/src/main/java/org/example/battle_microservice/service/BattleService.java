package org.example.battle_microservice.service;

import org.example.battle_microservice.model.BattleModel;
import org.example.battle_microservice.repository.BattleRepository;
import org.springframework.stereotype.Service;

import java.util.Date;
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

    public void markBattleAsEnded(Long battleId) {
        battleRepository.markBattleAsEnded(battleId);
    }

    public byte[] getTests(String tour, String battle){
        return battleRepository.findCodeTestByTournamentAndBattle(tour, battle).get(0);
    }

    public byte[] getBuildAutomationScripts(String tour, String battle){
        return battleRepository.findBuildAutomationScriptsByTournamentAndBattle(tour, battle).get(0);
    }

    public Date getRegDeadline(String tour, String battle){
        return battleRepository.findRegDeadlineByTournamentAndName(tour, battle);
    }

    public Date getSubDeadline(String tour, String battle){
        return battleRepository.findSubDeadlineByTournamentAndName(tour, battle);
    }

}
