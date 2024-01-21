package org.example.battle_microservice.model;


import javax.persistence.*;


@Entity
@Table(name="battle_model")
public class BattleModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String tournament;

    public BattleModel() {
    }

    public BattleModel(String name) {
        this.name = name;
    }

    public BattleModel(String name, String tournament) {
        this.name= name;
        this.tournament = tournament;
    }

    public String getName() {
        return this.name;
    }

    public String getTournament() { return this.tournament; }

}
