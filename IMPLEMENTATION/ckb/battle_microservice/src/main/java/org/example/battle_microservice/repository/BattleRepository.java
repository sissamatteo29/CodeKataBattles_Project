package org.example.battle_microservice.repository;

import org.example.battle_microservice.model.BattleModel;
import org.hibernate.annotations.Type;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface BattleRepository extends JpaRepository<BattleModel, Long> {
    Optional<BattleModel> findByName(String name);
    boolean existsByName(@Param("name") String name);

    @Override
    @Type(type = "org.hibernate.type.BinaryType")  // Specify the correct Hibernate type for bytea
    <S extends BattleModel> S save(S entity);

    List<BattleModel> findByTournament(String tournamentName);

    @Transactional
    @Modifying
    @Query("UPDATE BattleModel b SET b.ended = true WHERE b.id = :battleId")
    void markBattleAsEnded(Long battleId);

    @Query("SELECT b.code_test FROM BattleModel b " +
            "WHERE b.tournament = :tournamentName AND b.name = :battleName")
    List<byte[]> findCodeTestByTournamentAndBattle(
            @Param("tournamentName") String tournamentName,
            @Param("battleName") String battleName
    );

    @Query("SELECT b.automation_build_script FROM BattleModel b " +
            "WHERE b.tournament = :tournamentName AND b.name = :battleName")
    List<byte[]> findBuildAutomationScriptsByTournamentAndBattle(
            @Param("tournamentName") String tournamentName,
            @Param("battleName") String battleName
    );

    @Query("SELECT b.reg_deadline FROM BattleModel b WHERE b.tournament = :tournament AND b.name = :name")
    Date findRegDeadlineByTournamentAndName(@Param("tournament") String tournament, @Param("name") String name);

    @Query("SELECT b.sub_deadline FROM BattleModel b WHERE b.tournament = :tournament AND b.name = :name")
    Date findSubDeadlineByTournamentAndName(@Param("tournament") String tournament, @Param("name") String name);
}
