package org.example.user_microservice.controller;

import org.example.user_microservice.model.UserModel;
import org.example.user_microservice.service.UserService;
import org.example.user_microservice.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
public class UserController {

    @Autowired
    private UserService userService;

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


    @PostMapping(value = "/saveMessage", consumes = MediaType.APPLICATION_JSON_VALUE)
    public void saveMessage(@RequestBody String message) {
        System.out.println("Message received: " + message);
        List<UserModel> userList = userService.getAllUsers();
        String name_temp = "";
        for (UserModel user : userList) {
            String name = user.getUsername();
            if (name.equals(name_temp)) continue;
            Integer role = user.getRole();
            name_temp = name;

            saveMessageForUser(name, role, message);
        }
    }
    @GetMapping("/getNotifications")
    public List<String> getNotifications(@RequestParam String username) {
        System.out.println("Username: "+ username);
        return userService.getNotifications(username);
    }


    private void saveMessageForUser(String name, Integer role, String message) {
        System.out.println("Saving message for user - Nome: " + name + ", Ruolo: " + role + ", Message: " + message);
        userService.saveUser((new UserModel(name, role, message)));
    }
}
