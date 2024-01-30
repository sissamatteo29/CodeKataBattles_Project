package org.example.tournament_microservice.model;

import javax.persistence.*;
import java.util.Date;


@Entity
@Table(name="tournament_model")
public class TournamentModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    @Temporal(TemporalType.DATE)
    private Date subscription_deadline;
    private String creator;
    private boolean ended;
    @Temporal(TemporalType.DATE)
    private Date end_date;

    public TournamentModel() {
    }

    public TournamentModel(String name) {
        this.name = name;
    }

    public TournamentModel(String name, Date subscriptionDate, String creator) {
        this.name = name;
        this.subscription_deadline = subscriptionDate;
        this.creator = creator;
    }

    public String getName() {
        return this.name;
    }
    public Long getId() {return this.id;}
    public void setEndDate(Date date) {this.end_date = date; }
    public Date getSubscriptionDeadline() {return this.subscription_deadline; }
    public String getCreator() {return this.creator; }
    public boolean getEnded() {return this.ended; }
    public void setEnded(Boolean ended) {this.ended=ended; }
    public Date getEndDate() {return this.end_date; }
}
