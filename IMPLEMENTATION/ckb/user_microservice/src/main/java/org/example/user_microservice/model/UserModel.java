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
    //private String subscription; //tournaments name
    private String notification;
    public UserModel() {
    }
    public UserModel(String username, int role) {
        this.username = username;
        this.role = role;
    }

    public UserModel(String username, int role, String message) {
        this.username = username;
        this.role = role;
        this.notification = message;
    }

    //public void setTournament(String subscription) {this.subscription = subscription; }

    public String getUsername() {
        return this.username;
    }
    public void setRole(int role) {
        this.role = role;
    }

    public String getNotifications() {return this.notification; }

    public int getRole() {return this.role; }

    //private String getSubscription() { return this.subscription; }
}
