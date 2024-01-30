package org.example.user_microservice.service;

import org.example.user_microservice.model.UserModel;
import org.example.user_microservice.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Optional<UserModel> getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public void saveUser(UserModel user) {
        System.out.println("Saving the user");
        userRepository.save(user);
    }

    public List<UserModel> getAllUsers() {
        return userRepository.findAll();
    }
    public Integer getUserRole(String username) { return userRepository.getUserRole(username); }

    public Optional<UserModel> getUserModelById(Long id) {return userRepository.findById(id);}

    public UserModel getUserModel(String username) { return userRepository.getUserModel(username); }


    public boolean isUserExists(String username) {
        return userRepository.existsByUsername(username);
    }

    public void setRoleForUser(String username, int role) {
        Optional<UserModel> optionalUser = userRepository.findByUsername(username);

        optionalUser.ifPresent(user -> {
            user.setRole(role);
            userRepository.save(user);
        });
    }

    public List<String> getNotifications(String username) {
        List<UserModel> users = userRepository.findAllByUsername(username);
        System.out.println("Users retrieved: " + users.toString());
        List<String> notificationsList = new ArrayList<>();

        for (UserModel user : users) {
            // For each user, call the NotificationService to get notifications
            String userNotifications = user.getNotifications();

            // Add user notifications to the overall list
            notificationsList.add(userNotifications);
        }

        System.out.println("Notifications retrieved: "+notificationsList.toString());
        if (!notificationsList.isEmpty()) {
            return notificationsList;
        } else {
            // Handle the case when no notifications are found
            return List.of("No notifications found");
        }
    }
}
