package org.example.tournament_microservice.controller;

import org.example.tournament_microservice.model.TournamentModel;
import org.example.tournament_microservice.service.TournamentRankingService;
import org.example.tournament_microservice.service.TournamentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;

@RestController
public class TournamentController {
    @Autowired
    private TournamentService tournamentService;
    @Autowired
    private TournamentRankingService tournamentRankingService;
    @PostMapping("/createNewTournament")
    public ResponseEntity<String> createNewTournament(@RequestParam String name, @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date subscriptionDate, @RequestParam String creator, Model model) {
            tournamentService.saveTournament(new TournamentModel(name, subscriptionDate, creator));
            return ResponseEntity.ok("Tournament created successfully");
    }
    @GetMapping("/getAllTournaments")
    public ResponseEntity<List<String>> getAllTournaments(@RequestParam String name) {
        System.out.println("Getting the tournaments");
        System.out.println(name);
        List<String> tournamentNames = tournamentService.getTournamentNamesByCreator(name);
        if (tournamentNames != null && !tournamentNames.isEmpty()) {
            return ResponseEntity.ok(tournamentNames);
        } else {
            return ResponseEntity.noContent().build();
        }
    }

    @GetMapping("/getAllTournamentsAbs")
    public ResponseEntity<List<String>> getAllTournamentsAbs() {
        System.out.println("Getting all the tournaments for students");
        List<String> tournamentNames = tournamentService.getTournaments();
        if (tournamentNames != null && !tournamentNames.isEmpty()) {
            return ResponseEntity.ok(tournamentNames);
        } else {
            return ResponseEntity.noContent().build();
        }
    }

    @PostMapping("/addStudent")
    public ResponseEntity<String> addStudent(@RequestParam String tourId, @RequestParam String studId) {
        return tournamentRankingService.addStudent(tourId, studId);
    }

}
