package org.example.battle_microservice.repository;

import org.example.battle_microservice.model.BattleModel;
import org.hibernate.annotations.Type;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

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

}
