package org.example.tournament_microservice.service;

import org.example.tournament_microservice.model.TourIdStudId;
import org.example.tournament_microservice.model.TournamentRankingModel;
import org.example.tournament_microservice.repository.TournamentRankingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Service
public class TournamentRankingService {
    private final TournamentRankingRepository tournamentRankingRepository;

    @Autowired
    public TournamentRankingService(TournamentRankingRepository tournamentRankingRepository) {
        this.tournamentRankingRepository = tournamentRankingRepository;
    }

    public Integer findScoreByTourIdAndStudId(String tourId, String studId) {
        return tournamentRankingRepository.findScoreByTourIdAndStudId(tourId, studId);
    }

    public List<String> findStudIdsByTourId(String tourId) {
        return tournamentRankingRepository.findStudIdsByTourId(tourId);
    }

    public List<String> findTourIdsByStudId(String studId) {
        return tournamentRankingRepository.findTourIdsByStudId(studId);
    }

    public void updateScoreByTourIdAndStudId(String tourId, String studId, int newScore) {
        tournamentRankingRepository.updateScoreByTourIdAndStudId(tourId, studId, newScore);
    }

    public List<Object[]> getStudAndScoreByTour(String tour) {
        return tournamentRankingRepository.findStudAndScoreByTour(tour);
    }

    public ResponseEntity<String> addStudent(String tourId, String studId) {
        // Check if the entry already exists
        TourIdStudId id = new TourIdStudId(tourId, studId);
        Optional<TournamentRankingModel> existingEntry = tournamentRankingRepository.findById(id);

        if (existingEntry.isPresent()) {
            return ResponseEntity.badRequest().body("Entry already exists for tourId: " + tourId + " and studId: " + studId);
        }

        // Create a new entry with a default score of 0
        TournamentRankingModel newEntry = new TournamentRankingModel(id, 0);
        tournamentRankingRepository.save(newEntry);

        return ResponseEntity.ok("Entry added successfully for tourId: " + tourId + " and studId: " + studId);
    }


    @Transactional
    public void updateScoreByTourAndStudent( List<Object[]> stud_scores, String tour) {
        for (Object[] stud_score : stud_scores) {
            if (stud_score.length == 2 && stud_score[0] instanceof String && stud_score[1] instanceof Integer) {
                String stud = (String) stud_score[0];
                int battleScore = (int) stud_score[1];

                tournamentRankingRepository.updateScoreByTourAndStudent(tour, stud, battleScore);


            }
        }

    }



}
