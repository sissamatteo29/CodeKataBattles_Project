package org.example.battle_microservice.controller;

import org.example.battle_microservice.model.BattleModel;
import org.example.battle_microservice.service.BattleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
public class BattleController {
    @Autowired
    private BattleService battleService;
    @PostMapping("/createNewBattle")
    public ResponseEntity<String> createNewBattle(@RequestParam String name, @RequestParam String tournament, Model model) {
        battleService.saveBattle(new BattleModel(name, tournament));
        return ResponseEntity.ok("Tournament created successfully");
    }
    @GetMapping("/getAllBattles")
    public List<String> getBattlesByTournament(@RequestParam String tournamentName, Model model) {
        System.out.println("Getting all battles by tournament (controller)");
        return battleService.getBattlesByTournament(tournamentName);
    }
}
