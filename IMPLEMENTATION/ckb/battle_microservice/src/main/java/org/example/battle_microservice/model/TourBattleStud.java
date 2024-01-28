package org.example.battle_microservice.model;

import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class TourBattleStud implements Serializable {
    private String tour;

    private String battle;
    private String stud;

    public TourBattleStud(String tour, String battle, String stud) {
        this.tour = tour;
        this.battle = battle;
        this.stud = stud;
    }

    public TourBattleStud() {

    }

    public String getTour() {
        return tour;
    }

    public void setTour(String tour) {
        this.tour = tour;
    }

    public String getBattle() {
        return battle;
    }

    public void setBattle(String battle) {
        this.battle = battle;
    }

    public String getStud() {
        return stud;
    }

    public void setStud(String stud) {
        this.stud = stud;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TourBattleStud that = (TourBattleStud) o;
        return Objects.equals(tour, that.tour) &&
                Objects.equals(battle, that.battle) &&
                Objects.equals(stud, that.stud);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tour, battle, stud);
    }
}
