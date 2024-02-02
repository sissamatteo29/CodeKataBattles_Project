package org.example.tournament_microservice.model;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name="tournament_student_score")
public class TournamentRankingModel {


    @EmbeddedId
    private TourIdStudId id;

    private int score;

    // Default constructor (needed by JPA)
    public TournamentRankingModel() {
        this.id = new TourIdStudId();
    }

    // Constructor with parameters
    public TournamentRankingModel(TourIdStudId id, int score) {
        this.id = id;
        this.score = score;
    }


    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public TourIdStudId getId() {
        return id;
    }

    public void setId(TourIdStudId id) {
        this.id = id;
    }
}
