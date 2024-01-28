package org.example.battle_microservice.model;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name="tournament_battle_student_team_score")
public class BattleRankingModel {
    @EmbeddedId
    private TourBattleStud id;
    private String teamName;
    private int score;



    public BattleRankingModel(TourBattleStud id, String teamName, int score) {
        this.id = id;
        this.teamName = teamName;
        this.score = score;
    }

    public BattleRankingModel() {
        this.id = new TourBattleStud();
    }

    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public TourBattleStud getId() {
        return id;
    }

    public void setId(TourBattleStud id) {
        this.id = id;
    }
}
