package org.example.user_microservice.service;

import org.example.user_microservice.model.UserModel;
import org.example.user_microservice.repository.UserRepository;
import org.springframework.stereotype.Service;

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
}
