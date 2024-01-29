package org.example.battle_microservice.controller;

import org.example.battle_microservice.model.BattleModel;
import org.example.battle_microservice.service.BattleRankingService;
import org.example.battle_microservice.service.BattleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;
import java.util.List;

@RestController
public class BattleController {
    @Autowired
    private BattleService battleService;
    @Autowired
    private BattleRankingService battleRankingService;

    private Date parseDate(String dateString) {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            return dateFormat.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }
    @PostMapping("/createNewBattle")
    public ResponseEntity<String> createNewBattle(@RequestParam String name,
                                                  @RequestParam String tournament,
                                                  @RequestParam String automation_build_script,
                                                  @RequestParam String code_test,
                                                  @RequestParam String code,
                                                  @RequestParam int max_team_size,
                                                  @RequestParam int min_team_size,
                                                  @RequestParam String repository_link,
                                                  @RequestParam boolean manual_evaluation,
                                                  @RequestParam String reg_deadline,
                                                  @RequestParam String sub_deadline,
                                                  @RequestParam String creator,
                                                  Model model) {
        try {
            // Decode Base64-encoded parameters
            byte[] automationBuildScriptBytes = Base64.getUrlDecoder().decode(automation_build_script);
            byte[] codeTestBytes = Base64.getUrlDecoder().decode(code_test);
            byte[] codeBytes = Base64.getUrlDecoder().decode(code);

            System.out.println("No problems decoding byte[]");
            battleService.saveBattle(new BattleModel(name, tournament, automationBuildScriptBytes, codeTestBytes, codeBytes,
                    max_team_size, min_team_size, repository_link,
                    manual_evaluation, parseDate(reg_deadline), parseDate(sub_deadline), creator));

            return ResponseEntity.ok("Battle created successfully");
        } catch (Exception e) {
            e.printStackTrace();
            // Handle errors appropriately
            System.out.println("Bad trying to decode byte[] or saving battle");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error creating battle");
        }
    }

    @GetMapping("/getAllBattles")
    public List<String> getBattlesByTournament(@RequestParam String tournamentName, Model model) {
        System.out.println("Getting all battles by tournament (controller)");
        return battleService.getBattlesByTournament(tournamentName);
    }

    @PostMapping("/addStudent")
    public ResponseEntity<String> addStudent(
            @RequestParam String tour,
            @RequestParam String battle,
            @RequestParam String stud,
            @RequestParam String team) {
        return battleRankingService.addStudent(tour, battle, stud, team);
    }

    @GetMapping("/getBattlesByTourAndStud")
    public ResponseEntity<List<String>> getBattlesByTourAndStud(
            @RequestParam String tour,
            @RequestParam String stud) {
        List<String> battles = battleRankingService.getBattlesByTourAndStud(tour, stud);
        return ResponseEntity.ok(battles);
    }
}
