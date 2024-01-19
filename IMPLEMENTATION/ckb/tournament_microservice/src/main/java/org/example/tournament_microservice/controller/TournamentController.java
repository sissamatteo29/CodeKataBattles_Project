package org.example.tournament_microservice.controller;

import org.example.tournament_microservice.service.TournamentService;
import org.example.tournament_microservice.model.TournamentModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;
@RestController
public class TournamentController {
    @Autowired
    private TournamentService tournamentService;
    @PostMapping("/createNewTournament")
    public ResponseEntity<String> createNewTournament(@RequestParam String name, @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date subscriptionDate, @RequestParam String creator, Model model) {
            tournamentService.saveTournament(new TournamentModel(name, subscriptionDate, creator));
            return ResponseEntity.ok("Tournament created successfully");
    }

}
