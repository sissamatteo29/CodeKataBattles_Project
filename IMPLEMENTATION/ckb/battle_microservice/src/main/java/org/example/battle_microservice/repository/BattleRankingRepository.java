package org.example.battle_microservice.repository;

import org.example.battle_microservice.model.BattleRankingModel;
import org.example.battle_microservice.model.TourBattleStud;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BattleRankingRepository extends JpaRepository<BattleRankingModel, TourBattleStud> {

    @Query("SELECT DISTINCT brm.id.battle FROM BattleRankingModel brm " +
            "WHERE brm.id.tour = :tour AND brm.id.stud = :stud")
    List<String> findBattlesByTourAndStud(@Param("tour") String tour, @Param("stud") String stud);

    @Query("SELECT brm.score FROM BattleRankingModel brm " +
            "WHERE brm.id.tour = :tour AND brm.id.battle = :battle AND brm.id.stud = :stud")
    Integer findScoreByTourBattleStud(@Param("tour") String tour, @Param("battle") String battle, @Param("stud") String stud);

    @Query("SELECT DISTINCT brm.teamName, brm.score FROM BattleRankingModel brm " +
            "WHERE brm.id.tour = :tour AND brm.id.battle = :battle")
    List<Object[]> findDistinctTeamNameAndScoreByTourAndBattle(@Param("tour") String tour, @Param("battle") String battle);

    @Query("SELECT br.teamName FROM BattleRankingModel br WHERE br.id = :tourBattleStud")
    String findTeamNameByTourBattleStud(@Param("tourBattleStud") TourBattleStud tourBattleStud);

}
