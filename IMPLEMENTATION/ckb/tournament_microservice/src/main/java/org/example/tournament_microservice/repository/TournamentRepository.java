package org.example.tournament_microservice.repository;

import org.example.tournament_microservice.model.TournamentModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface TournamentRepository extends JpaRepository<TournamentModel, Long> {
    Optional<TournamentModel> findByName(String name);
    boolean existsByName(@Param("name") String name);
}
