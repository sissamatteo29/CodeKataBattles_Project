package org.example.user_microservice.controller;

import org.example.user_microservice.model.UserModel;
import org.example.user_microservice.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class UserController {

    @Autowired
    private UserService userService;

    // @GetMapping("/getAllSubscription")
    // public ResponseEntity<List<String>> getAllSubscription(@RequestParam String name) {
    //     System.out.println("Getting the subscriptions");
    //     System.out.println(name);
    //     List<String> tournamentNames = userService.getTournamentNamesBySubscription(name);
    //     if (tournamentNames != null && !tournamentNames.isEmpty()) {
    //         return ResponseEntity.ok(tournamentNames);
    //     } else {
    //         return ResponseEntity.noContent().build();
    //     }
    // }

    @GetMapping("/getUserRole")
    public ResponseEntity<Integer> getUserRole(@RequestParam String username) {
        Integer userRole = userService.getUserRole(username);
        return ResponseEntity.ok(userRole);
    }

    @GetMapping("/userExists")
    public ResponseEntity<Boolean> checkUserExists(@RequestParam String username) {
        boolean userExists = userService.isUserExists(username);
        return ResponseEntity.ok(userExists);
    }

    @GetMapping("/checkUser")
    public ResponseEntity<Boolean> checkUser(@RequestParam String username, Model model) {
        boolean userExists = userService.isUserExists(username);
        return ResponseEntity.ok(userExists);
    }

    @PostMapping("/saveUser")
    public void saveUser(@RequestParam String username, @RequestParam int role) {
        System.out.println("Controller calling for saving the user");
        userService.saveUser(new UserModel(username, role));
    }


    // @PostMapping("/addSubscription")
    // public void addSubscription(@RequestParam String tournament, @RequestParam String username) {
    //     userService.addSubscription(tournament, username);
    // }

}
