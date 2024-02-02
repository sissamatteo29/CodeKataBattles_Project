package org.example.tournament_microservice.repository;

import org.example.tournament_microservice.model.TourIdStudId;
import org.example.tournament_microservice.model.TournamentRankingModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
public interface TournamentRankingRepository extends JpaRepository<TournamentRankingModel, TourIdStudId> {
    @Query("SELECT tr.score FROM TournamentRankingModel tr WHERE tr.id.tourId = :tourId AND tr.id.studId = :studId")
    Integer findScoreByTourIdAndStudId(@Param("tourId") String tourId, @Param("studId") String studId);

    // Custom query to get the List of studId given a tourId
    @Query("SELECT tr.id.studId FROM TournamentRankingModel tr WHERE tr.id.tourId = :tourId")
    List<String> findStudIdsByTourId(@Param("tourId") String tourId);

    // Custom query to get a list of tourId given a studId
    @Query("SELECT tr.id.tourId FROM TournamentRankingModel tr WHERE tr.id.studId = :studId")
    List<String> findTourIdsByStudId(@Param("studId") String studId);

    @Query("SELECT tr.id.studId, tr.score FROM TournamentRankingModel tr " +
            "WHERE tr.id.tourId = :tour ")
    List<Object[]> findStudAndScoreByTour(@Param("tour") String tour);


    @Modifying
    @Transactional
    @Query("UPDATE TournamentRankingModel tr SET tr.score = tr.score + :battleScore " +
            "WHERE tr.id.tourId = :tourId AND tr.id.studId = :studId")
    void updateScoreByTourAndStudent(@Param("tourId") String tourId,
                                     @Param("studId") String studId,
                                     @Param("battleScore") int battleScore);
    // Custom query to update the score given a tourId and studId
    @Modifying
    @Transactional
    @Query("UPDATE TournamentRankingModel tr SET tr.score = :newScore WHERE tr.id.tourId = :tourId AND tr.id.studId = :studId")
    void updateScoreByTourIdAndStudId(@Param("tourId") String tourId, @Param("studId") String studId, @Param("newScore") int newScore);
}

