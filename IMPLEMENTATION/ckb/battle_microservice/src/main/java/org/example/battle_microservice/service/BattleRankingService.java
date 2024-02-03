package org.example.battle_microservice.service;

import org.example.battle_microservice.model.BattleRankingModel;
import org.example.battle_microservice.model.TourBattleStud;
import org.example.battle_microservice.repository.BattleRankingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BattleRankingService {

    private final BattleRankingRepository rankingRepository;

    @Autowired
    public BattleRankingService(BattleRankingRepository rankingRepository) {
        this.rankingRepository = rankingRepository;
    }

    public List<String> getBattlesByTourAndStud(String tour, String stud) {
        return rankingRepository.findBattlesByTourAndStud(tour, stud);
    }

    public Integer getScoreByTourBattleStud(String tour, String battle, String stud) {
        return rankingRepository.findScoreByTourBattleStud(tour, battle, stud);
    }

    public ResponseEntity<String> addStudent(String tour, String battle, String stud, String team) {
        // Check if the entry already exists
        TourBattleStud id = new TourBattleStud(tour, battle, stud);
        Optional<BattleRankingModel> existingEntry = rankingRepository.findById(id);

        if (existingEntry.isPresent()) {
            return ResponseEntity.badRequest().body("Entry already exists for tour: " + tour +
                    ", battle: " + battle + ", and stud: " + stud);
        }

        // Create a new entry with a default score of 0
        BattleRankingModel newEntry = new BattleRankingModel(id, team, 0);
        rankingRepository.save(newEntry);

        return ResponseEntity.ok("Entry added successfully for tour: " + tour +
                ", battle: " + battle + ", stud: " + stud + ", and team: " + team);
    }

    public Integer getScore(String tour,String battle,String stud){
        return rankingRepository.findScoreByTourBattleStud(tour, battle, stud);
    }
    public List<Object[]> getDistinctTeamNameAndScoreByTourAndBattle(String tour, String battle) {
        return rankingRepository.findDistinctTeamNameAndScoreByTourAndBattle(tour, battle);
    }

    public String findTeamNameByTourBattleStud(String tour, String battle, String stud) {
        TourBattleStud tourBattleStud = new TourBattleStud(tour, battle, stud);
        return rankingRepository.findTeamNameByTourBattleStud(tourBattleStud);
    }

    public void updateScoreForANewSolution(String tour, String battle, String teamName, int newScore){

        rankingRepository.updateScoreByTourAndBattleAndTeamName(tour, battle, teamName, newScore);
    }
}
