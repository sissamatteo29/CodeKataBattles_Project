package org.example.user_microservice.model;

import javax.persistence.*;

@Entity
@Table(name="user_model")
public class UserModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;
    private int role;
    private String subscription; //tournaments name

    public UserModel() {
    }
    public UserModel(String username, int role) {
        this.username = username;
        this.role = role;
    }

    public void setTournament(String subscription) {this.subscription = subscription; }

    public String getUsername() {
        return this.username;
    }
    public void setRole(int role) {
        this.role = role;
    }

    private String getSubscription() { return this.subscription; }
}
