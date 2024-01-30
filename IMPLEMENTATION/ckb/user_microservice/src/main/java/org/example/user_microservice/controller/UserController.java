package org.example.user_microservice.controller;

import org.example.user_microservice.model.UserModel;
import org.example.user_microservice.service.UserService;
import org.example.user_microservice.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;


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

    @PostMapping("/saveMessageToUser")
    public void saveMessageToUser(@RequestBody Map<String, Object> requestBody) {
        System.out.println("Controller calling for create a notification for ending of a tournament");
        String message = (String) requestBody.get("message");
        String userId = (String) requestBody.get("userId");
        UserModel userModel = userService.getUserModel(userId);
        if (userModel != null) {
            // User found, extract necessary information
            String name = userModel.getUsername();
            Integer role = userModel.getRole();

            saveMessageForUser(name, role, message);

            System.out.println("Notification of ended tournament saved for user: " + name);
        } else {
            System.out.println("User not found for ID: " + userId);
        }

    }

    @PostMapping(value = "/saveMessage", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> saveMessage(@RequestBody String message) {
        System.out.println("Message received: " + message);
        List<UserModel> userList = userService.getAllUsers();
        Set<String> encounteredNames = new HashSet<>();

        for (UserModel user : userList) {
            String name = user.getUsername();
            // Check if the name has been encountered before
            if (encounteredNames.contains(name)) {
                continue; // Skip if the name has already been encountered
            }

            // Add the name to the set to mark it as encountered
            encounteredNames.add(name);

            Integer role = user.getRole();

            if (role.equals(0)) {
                saveMessageForUser(name, role, message);
            }
        }
        return ResponseEntity.ok("Message received successfully");
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
