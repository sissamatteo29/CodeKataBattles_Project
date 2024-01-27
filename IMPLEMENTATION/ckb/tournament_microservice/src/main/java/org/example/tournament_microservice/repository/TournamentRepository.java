package org.example.tournament_microservice.repository;

import org.example.tournament_microservice.model.TournamentModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface TournamentRepository extends JpaRepository<TournamentModel, Long> {

    // those are method for which JPA automatically generates the query
    Optional<TournamentModel> findByName(String name);
    boolean existsByName(@Param("name") String name);

    @Query("SELECT t.name FROM TournamentModel t WHERE t.creator = :creator")
    List<String> findNamesByCreator(String creator);

    @Query("SELECT t.name FROM TournamentModel t WHERE t.id IN :ids")
    List<String> findNamesByIds(@Param("ids") List<Long> ids);

    List<TournamentModel> findAll();

}
