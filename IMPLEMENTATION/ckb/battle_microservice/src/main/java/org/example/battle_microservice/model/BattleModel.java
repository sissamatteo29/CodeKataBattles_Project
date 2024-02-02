package org.example.battle_microservice.model;


import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.Date;


@Entity
@Table(name="battle_model")
public class BattleModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String tournament;
    @Basic(fetch = FetchType.LAZY)
    @Column(name = "automation_build_script", columnDefinition = "bytea")
    @Type(type="org.hibernate.type.BinaryType")
    private byte[] automation_build_script;

    @Basic(fetch = FetchType.LAZY)
    @Column(name = "code_test", columnDefinition = "bytea")
    @Type(type="org.hibernate.type.BinaryType")
    private byte[] code_test;

    @Basic(fetch = FetchType.LAZY)
    @Column(name = "code", columnDefinition = "bytea")
    @Type(type="org.hibernate.type.BinaryType")
    private byte[] code;

    private int max_team_size;

    private int min_team_size;
    private String repository_link;
    private Boolean manual_evaluation;
    @Temporal(TemporalType.DATE)
    private Date reg_deadline;
    @Temporal(TemporalType.DATE)
    private Date sub_deadline;
    private Boolean ended;

    private String creator;
    public BattleModel() {
    }

    public BattleModel(String name, String tournament, byte[] automation_build_script, byte[] code_test, byte[] code, int max_team_size,
                       int min_team_size, String repository_link, Boolean manual_evaluation, Date reg_deadline, Date sub_deadline, String creator
                       ) {
        this.name= name;
        this.tournament = tournament;
        this.automation_build_script = automation_build_script;
        this.code_test = code_test;
        this.code = code;
        this.max_team_size = max_team_size;
        this.min_team_size = min_team_size;
        this.repository_link = repository_link;
        this.manual_evaluation = manual_evaluation;
        this.reg_deadline = reg_deadline;
        this.sub_deadline = sub_deadline;
        this.creator = creator;
    }

    public String getName() {
        return this.name;
    }

    public String getTournament() { return this.tournament; }
    public String getRepository() {return this.repository_link; }
    public boolean getManualEvaluation() {return this.manual_evaluation; }
    public Date getRegDeadline() { return this.reg_deadline; }
    public Date getSubDeadline() { return this.sub_deadline; }
    public String getCreator() { return this.creator; }
    public Boolean getEnded() { return this.ended; }

    public void setAutomation_build_script(byte[] automation_build_script) {
        this.automation_build_script = automation_build_script;
    }

    public void setCode_test(byte[] code_test) {
        this.code_test = code_test;
    }

    public void setCode(byte[] code) {
        this.code = code;
    }

    public void setEnded(Boolean ended) {this.ended = ended; }


}
