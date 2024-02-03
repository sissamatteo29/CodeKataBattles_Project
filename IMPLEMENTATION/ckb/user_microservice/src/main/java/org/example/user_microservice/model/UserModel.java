package org.example.user_microservice.model;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name="user_model")
public class UserModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;
    private int role;

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


    public void setRole(int role) {
        this.role = role;
    }



    public int getRole() {return this.role; }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getNotification() {
        return notification;
    }

    public void setNotification(String notification) {
        this.notification = notification;
    }

    @Override
    public int hashCode() {
        // XOR (^) can be used to combine hash values of different attributes
        return Objects.hash(username, role);
    }

    @Override
    public boolean equals(Object obj) {
        // Check for object equality based on username and role
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        UserModel otherUser = (UserModel) obj;
        return Objects.equals(username, otherUser.username) &&
                Objects.equals(role, otherUser.role);
    }


    //private String getSubscription() { return this.subscription; }
}
