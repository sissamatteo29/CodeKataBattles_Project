package org.example.battle_microservice.model;


import javax.persistence.*;


@Entity
@Table(name="battle_model")
public class BattleModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    public BattleModel() {
    }

    public BattleModel(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

}
